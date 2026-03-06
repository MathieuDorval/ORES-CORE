//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Core registry discovery service. Scans all loaded mods,
// configuration directories, and virtual resource packs for registry.json
// configuration structures. Merges these dynamically loaded configurations
// with properties implied by active vein generation configs, decodes them 
// into combinatorial material mappings, and implements hard/soft memory limits
// to prevent OutOfMemory errors on huge modpacks.
//
// Author: __mathieu
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ores.mathieu.OresCoreCommon;
import ores.mathieu.platform.Services;

import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.ByteArrayInputStream;

public class DiscoveryManager {
    public static final OresRegistryData REGISTRY = new OresRegistryData();

    public static RegistryDecoder.DecodedRegistry DECODED_DATA;

    public static final int ORE_SOFT_LIMIT = 8000;
    public static final int ORE_HARD_LIMIT = 15000;

    private static boolean scanningDone = false;
    public static void setScanningDone(boolean done) { scanningDone = done; }
    public static synchronized void scanRegistries() {
        if (scanningDone) return;
        scanningDone = true;
        
        long startTime = System.currentTimeMillis();
        
        final int[] registryFilesFound = {0};

        Services.getPlatform().scanModResources("data/ores/registry.json", (modId, inputStream) -> {
            registryFilesFound[0]++;
            try (InputStreamReader reader = new InputStreamReader(inputStream)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                parseRegistryFile(jsonObject, modId, false);
            } catch (Exception e) {
                OresCoreCommon.LOGGER.error("[ORES CORE] Failed to parse registry.json in mod {}", modId, e);
            }
        });

        Path configRegistryPath = Services.getPlatform().getConfigDir().resolve("ores").resolve("registry.json");
        if (Services.getPlatform().isModLoaded("oresmods") && Files.exists(configRegistryPath)) {
            registryFilesFound[0]++;
            try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(configRegistryPath))) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                parseRegistryFile(jsonObject, "ores_config", true);
            } catch (Exception e) {
                OresCoreCommon.LOGGER.error("[ORES CORE] Failed to parse registry.json in config/ores", e);
            }
        }

        byte[] vPackData = ores.mathieu.resource.VirtualResourcePack.SERVER.getBytes("data/ores/registry.json");
        if (vPackData != null) {
            registryFilesFound[0]++;
            try (InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(vPackData))) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                parseRegistryFile(jsonObject, "ores_virtual", false);
            } catch (Exception e) {
                OresCoreCommon.LOGGER.error("[ORES CORE] Failed to parse registry.json in VirtualResourcePack.SERVER", e);
            }
        }

        OresCoreCommon.LOGGER.info("[ORES CORE] {} registry detected", registryFilesFound[0]);

        for (ores.mathieu.worldgen.VeinConfig vein : ores.mathieu.config.ConfigManager.LOADED_VEINS) {
            if (vein.materials != null) {
                for (String mat : vein.materials) {
                    if (!REGISTRY.getOreGeneration().contains(mat)) {
                        REGISTRY.addOreGeneration(mat);
                    }
                }
            }

            if (vein.associatedBlock != null && !vein.associatedBlock.isEmpty()) {
                if (!vein.associatedBlock.contains(":")) {
                } else if (!REGISTRY.getStonesReplacement().containsKey(vein.associatedBlock)) {
                    REGISTRY.addStoneReplacement(vein.associatedBlock, "minecraft", false);
                }
            }

            if (vein.bonusBlock != null && !vein.bonusBlock.isEmpty()) {
                String ns = vein.bonusBlock.contains(":") ? vein.bonusBlock.split(":")[0] : "ores";
                String path = vein.bonusBlock.contains(":") ? vein.bonusBlock.split(":")[1] : vein.bonusBlock;
                if (ns.equals("ores") && !REGISTRY.getMaterialsRegistry().contains(path)) {
                    REGISTRY.addMaterial(path);
                }
            }
        }

        long parseEndTime = System.currentTimeMillis();

        DECODED_DATA = RegistryDecoder.decode(REGISTRY);
        
        long decodeEndTime = System.currentTimeMillis();
        
        OresCoreCommon.LOGGER.info("[ORES CORE] Discovery parsing took {} ms", (parseEndTime - startTime));
        OresCoreCommon.LOGGER.info("[ORES CORE] Registry decoding took {} ms", (decodeEndTime - parseEndTime));
        OresCoreCommon.LOGGER.info("[ORES CORE] Total Discovery time: {} ms", (decodeEndTime - startTime));
        
        
        
        int objectsCount = DECODED_DATA.objects.size();
        int oresCount = DECODED_DATA.ores.size();

        OresCoreCommon.LOGGER.info("[ORES CORE] Resolved standard objects to create: {}", objectsCount);
        OresCoreCommon.LOGGER.info("[ORES CORE] Resolved combinatorial ores to create: {}", oresCount);

        if (oresCount > ORE_HARD_LIMIT) {
            OresCoreCommon.LOGGER.error("[ORES CORE] CRITICAL: Combinatorial ore limit exceeded ({} > {}). Registration will be TRUNCATED to prevent game crash!", oresCount, ORE_HARD_LIMIT);
            java.util.List<RegistryDecoder.ResolvedOre> truncated = new java.util.ArrayList<>(DECODED_DATA.ores.subList(0, ORE_HARD_LIMIT));
            DECODED_DATA.ores.clear();
            DECODED_DATA.ores.addAll(truncated);
            OresCoreCommon.LOGGER.error("[ORES CORE] Please reduce the number of host blocks or materials in your registry.json.");
        } else if (oresCount > ORE_SOFT_LIMIT) {
            OresCoreCommon.LOGGER.warn("[ORES CORE] WARNING: High number of combinatorial ores detected ({}). This may cause long loading times and high memory usage.", oresCount);
        }
    }

    private static void parseRegistryFile(JsonObject json, String defaultNamespace, boolean isConfig) {
        if (json.has("materials_registry")) {
            JsonArray array = json.getAsJsonArray("materials_registry");
            for (int i = 0; i < array.size(); i++) {
                REGISTRY.addMaterial(array.get(i).getAsString(), isConfig);
            }
        }

        if (json.has("ore_generation")) {
            JsonArray array = json.getAsJsonArray("ore_generation");
            for (int i = 0; i < array.size(); i++) {
                REGISTRY.addOreGeneration(array.get(i).getAsString());
            }
        }

        if (json.has("stones_replacement")) {
            JsonArray array = json.getAsJsonArray("stones_replacement");
            for (int i = 0; i < array.size(); i++) {
                String id = array.get(i).getAsString();
                if (id.contains(":")) {
                    REGISTRY.addStoneReplacement(id, defaultNamespace, isConfig);
                } else {
                    OresCoreCommon.LOGGER.warn("[ORES CORE] Ignored host block '{}' in registry from '{}' (requires namespace).", id, defaultNamespace);
                }
            }
        }

        if (json.has("blacklist")) {
            JsonArray array = json.getAsJsonArray("blacklist");
            for (int i = 0; i < array.size(); i++) {
                REGISTRY.addBlacklist(array.get(i).getAsString());
            }
        }
    }
}
