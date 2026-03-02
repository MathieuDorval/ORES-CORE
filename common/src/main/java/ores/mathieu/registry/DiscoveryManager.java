//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Scans and discovers registry.json files from all loaded mods.
//
// Author: __mathieu
// Version: 26.1.001
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

@SuppressWarnings("null")
public class DiscoveryManager {
    public static final OresRegistryData REGISTRY = new OresRegistryData();

    public static RegistryDecoder.DecodedRegistry DECODED_DATA;

    public static void scanRegistries() {
        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Scanning for registry.json files in all mods...");
        final int[] registryFilesFound = {0};

        Services.getPlatform().scanModResources("data/ores/registry.json", (modId, inputStream) -> {
            OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Found registry.json in mod: {}", modId);
            registryFilesFound[0]++;
            try (InputStreamReader reader = new InputStreamReader(inputStream)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                parseRegistryFile(jsonObject, modId);
            } catch (Exception e) {
                OresCoreCommon.LOGGER.error("[ORES CORE] Failed to parse registry.json in mod {}", modId, e);
            }
        });

        OresCoreCommon.LOGGER.info("[ORES CORE] {} registry detected", registryFilesFound[0]);
        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Registry scan complete.");
        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] - Raw Materials requested: {}", REGISTRY.getMaterialsRegistry().size());
        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] - Raw Ore generations: {}", REGISTRY.getOreGeneration().size());
        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] - Raw Stone replacements: {}", REGISTRY.getStonesReplacement().size());

        for (ores.mathieu.worldgen.VeinConfig vein : ores.mathieu.config.ConfigManager.LOADED_VEINS) {
            if (vein.materials != null) {
                for (String mat : vein.materials) {
                    if (!REGISTRY.getOreGeneration().contains(mat)) {
                        REGISTRY.addOreGeneration(mat);
                        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Auto-added '{}' to ore_generation (referenced in generation config).", mat);
                    }
                }
            }

            if (vein.associatedBlock != null && !vein.associatedBlock.isEmpty()) {
                if (!REGISTRY.getStonesReplacement().contains(vein.associatedBlock)) {
                    REGISTRY.addStoneReplacement(vein.associatedBlock, "ores");
                    OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Auto-added '{}' to stones_replacement (associatedBlock in generation config).", vein.associatedBlock);
                }
            }

            if (vein.bonusBlock != null && !vein.bonusBlock.isEmpty()) {
                String ns = vein.bonusBlock.contains(":") ? vein.bonusBlock.split(":")[0] : "ores";
                String path = vein.bonusBlock.contains(":") ? vein.bonusBlock.split(":")[1] : vein.bonusBlock;
                if (ns.equals("ores") && !REGISTRY.getMaterialsRegistry().contains(path)) {
                    REGISTRY.addMaterial(path);
                    OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Auto-added '{}' to materials_registry (bonusBlock in generation config).", path);
                }
            }
        }

        DECODED_DATA = RegistryDecoder.decode(REGISTRY);
        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Decoding successful.");
        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Resolved standard objects to create: {}", DECODED_DATA.objects.size());
        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Resolved combinatorial ores to create: {}", DECODED_DATA.ores.size());
    }

    private static void parseRegistryFile(JsonObject json, String defaultNamespace) {
        if (json.has("materials_registry")) {
            JsonArray array = json.getAsJsonArray("materials_registry");
            for (int i = 0; i < array.size(); i++) {
                REGISTRY.addMaterial(array.get(i).getAsString());
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
                REGISTRY.addStoneReplacement(array.get(i).getAsString(), defaultNamespace);
            }
        }
    }
}
