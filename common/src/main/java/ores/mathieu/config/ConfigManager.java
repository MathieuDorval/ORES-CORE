//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Core configuration engine for Ores Core. Responsible for using  
// ElectronWill's NightConfig to parse properties.toml and ores-generation.toml.
// Processes material, block, chemical, and item override parameters dynamically,
// and manages the saving and default generation of complex ore vein configurations.
//
// Author: __mathieu
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.io.FileWriter;
import ores.mathieu.OresCoreCommon;
import ores.mathieu.platform.Services;
import ores.mathieu.material.Material;
import ores.mathieu.material.MaterialsDatabase;
import ores.mathieu.material.ItemType;
import ores.mathieu.material.BlockType;
import ores.mathieu.material.ChemicalType;
import ores.mathieu.api.OresCoreAPI;
import ores.mathieu.worldgen.VeinConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("null")
public class ConfigManager {
    private static final Path CONFIG_DIR = Services.getPlatform().getConfigDir().resolve("ores");
    private static final Path ORE_GEN_FILE = CONFIG_DIR.resolve("ores-generation.toml");
    private static final Path PROPERTIES_FILE = CONFIG_DIR.resolve("properties.toml");
    
    public static final List<VeinConfig> LOADED_VEINS = new ArrayList<>();
    
    private static final Map<String, Map<String, String>> OVERRIDE_MATERIALS = new HashMap<>();
    private static final Map<String, Map<String, String>> OVERRIDE_ITEMS = new HashMap<>();
    private static final Map<String, Map<String, String>> OVERRIDE_BLOCKS = new HashMap<>();
    private static final Map<String, Map<String, String>> OVERRIDE_CHEMICALS = new HashMap<>();

    public static void preInitialize() {
        if (Files.exists(ORE_GEN_FILE)) {
            loadVeins();
        }
        if (Services.getPlatform().isModLoaded("oresmods") && Files.exists(PROPERTIES_FILE)) {
            loadProperties();
        }
    }

    public static void initialize() {
        if (!Files.exists(CONFIG_DIR)) {
            try {
                Files.createDirectories(CONFIG_DIR);
            } catch (IOException e) {
                OresCoreCommon.LOGGER.error("[ORES CORE] Failed to create ores config directory!", e);
                return;
            }
        }

        if (!Files.exists(ORE_GEN_FILE)) {
            generateDefaultOreConfig();
            loadVeins();
        } else {
            OresCoreCommon.LOGGER.debug("[ORES CORE] ores-generation.toml already exists. Using user configuration.");
        }

        if (Services.getPlatform().isModLoaded("oresmods") && Files.exists(PROPERTIES_FILE)) {
            loadProperties();
        }
    }

    public static void loadProperties() {
        OVERRIDE_MATERIALS.clear();
        OVERRIDE_ITEMS.clear();
        OVERRIDE_BLOCKS.clear();
        OVERRIDE_CHEMICALS.clear();

        try (CommentedFileConfig config = CommentedFileConfig.builder(PROPERTIES_FILE)
                .sync()
                .build()) {
            OresCoreCommon.LOGGER.info("[ORES CORE] Reading properties from: {}", PROPERTIES_FILE.toAbsolutePath());
            config.load();
            
            parseOverrideSection(config, "materials", OVERRIDE_MATERIALS);
            parseOverrideSection(config, "items", OVERRIDE_ITEMS);
            parseOverrideSection(config, "blocks", OVERRIDE_BLOCKS);
            parseOverrideSection(config, "chemicals", OVERRIDE_CHEMICALS);
            
            OresCoreCommon.LOGGER.info("[ORES CORE] Loaded properties overrides from properties.toml");

            
            applyAllOverrides();
        } catch (Exception e) {
            OresCoreCommon.LOGGER.error("[ORES CORE] Failed to load properties.toml", e);
        }
    }

    private static void parseOverrideSection(CommentedFileConfig config, String section, Map<String, Map<String, String>> target) {
        if (!config.contains(section)) {
            OresCoreCommon.LOGGER.debug("[ORES CORE] No section '{}' found in properties.toml", section);
            return;
        }

        Object sectionObj = config.get(section);
        if (!(sectionObj instanceof CommentedConfig sectionConfig)) {
            OresCoreCommon.LOGGER.warn("[ORES CORE] Section '{}' is not a Config object (found {})", section, sectionObj != null ? sectionObj.getClass().getSimpleName() : "null");
            return;
        }

        int count = 0;
        for (CommentedConfig.Entry entry : sectionConfig.entrySet()) {
            try {
                String id = entry.getKey();
                if (section.equals("blocks") && id.equalsIgnoreCase("ore")) continue;

                Object val = entry.getValue();
                if (val instanceof CommentedConfig props) {
                    Map<String, String> propMap = target.computeIfAbsent(id, k -> new HashMap<>());
                    for (CommentedConfig.Entry propEntry : props.entrySet()) {
                        Object propVal = propEntry.getValue();
                        if (propVal != null) {
                            
                            String valStr;
                            if (propVal instanceof String s) {
                                valStr = s;
                            } else {
                                valStr = "" + propVal;
                            }
                            propMap.put(propEntry.getKey(), valStr);
                        }
                    }
                    count++;
                }
            } catch (Exception e) {
                OresCoreCommon.LOGGER.error("[ORES CORE] Error parsing entry in section '{}'", section, e);
            }
        }
        OresCoreCommon.LOGGER.info("[ORES CORE] Parsed {} entries from section '{}'", count, section);
    }

    private static void applyAllOverrides() {
        applyApiOverrides();
        applyTomlOverrides();
    }

    private static void applyApiOverrides() {
        for (Map.Entry<String, Map<String, String>> entry : OresCoreAPI.getApiMaterialOverrides().entrySet()) {
            Material mat = MaterialsDatabase.get(entry.getKey());
            if (mat != null) {
                OresCoreCommon.LOGGER.debug("[ORES CORE] Applying API overrides for material: {}", entry.getKey());
                mat.applyOverrides(entry.getValue(), OresCoreAPI.PROTECTED_MATERIAL_PROPS);
            }
        }
        for (Map.Entry<String, Map<String, String>> entry : OresCoreAPI.getApiItemOverrides().entrySet()) {
            try {
                ItemType type = ItemType.valueOf(entry.getKey().toUpperCase());
                type.getDefaultOverride().applyOverrides(entry.getValue(), OresCoreAPI.PROTECTED_TYPE_PROPS);
            } catch (Exception e) {}
        }
        for (Map.Entry<String, Map<String, String>> entry : OresCoreAPI.getApiBlockOverrides().entrySet()) {
            if (entry.getKey().equalsIgnoreCase("ore")) continue;
            try {
                BlockType type = BlockType.valueOf(entry.getKey().toUpperCase());
                type.getDefaultOverride().applyOverrides(entry.getValue(), OresCoreAPI.PROTECTED_TYPE_PROPS);
            } catch (Exception e) {}
        }
        for (Map.Entry<String, Map<String, String>> entry : OresCoreAPI.getApiChemicalOverrides().entrySet()) {
            try {
                ChemicalType type = ChemicalType.valueOf(entry.getKey().toUpperCase());
                type.getDefaultOverride().applyOverrides(entry.getValue(), OresCoreAPI.PROTECTED_TYPE_PROPS);
            } catch (Exception e) {}
        }
    }

    private static void applyTomlOverrides() {
        for (Map.Entry<String, Map<String, String>> entry : OVERRIDE_MATERIALS.entrySet()) {
            Material mat = MaterialsDatabase.get(entry.getKey());
            if (mat != null) {
                OresCoreCommon.LOGGER.info("[ORES CORE] Applying TOML overrides for material: {}", entry.getKey());
                mat.applyOverrides(entry.getValue(), OresCoreAPI.PROTECTED_MATERIAL_PROPS);
            }
        }
        for (Map.Entry<String, Map<String, String>> entry : OVERRIDE_ITEMS.entrySet()) {
            try {
                ItemType type = ItemType.valueOf(entry.getKey().toUpperCase());
                OresCoreCommon.LOGGER.info("[ORES CORE] Applying TOML overrides for item type: {}", entry.getKey());
                type.getDefaultOverride().applyOverrides(entry.getValue(), OresCoreAPI.PROTECTED_TYPE_PROPS);
            } catch (Exception e) {}
        }
        for (Map.Entry<String, Map<String, String>> entry : OVERRIDE_BLOCKS.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("ore")) continue;
            try {
                BlockType type = BlockType.valueOf(entry.getKey().toUpperCase());
                OresCoreCommon.LOGGER.info("[ORES CORE] Applying TOML overrides for block type: {}", entry.getKey());
                type.getDefaultOverride().applyOverrides(entry.getValue(), OresCoreAPI.PROTECTED_TYPE_PROPS);
            } catch (Exception e) {}
        }
        for (Map.Entry<String, Map<String, String>> entry : OVERRIDE_CHEMICALS.entrySet()) {
            try {
                ChemicalType type = ChemicalType.valueOf(entry.getKey().toUpperCase());
                OresCoreCommon.LOGGER.info("[ORES CORE] Applying TOML overrides for chemical type: {}", entry.getKey());
                type.getDefaultOverride().applyOverrides(entry.getValue(), OresCoreAPI.PROTECTED_TYPE_PROPS);
            } catch (Exception e) {}
        }
    }

    public static void applyOverridesToObject(String category, String id) {
        Map<String, String> apiProps = null;
        Map<String, String> tomlProps = null;
        Object targetObj = null;
        Set<String> prot = null;

        id = id.toLowerCase();
        switch (category.toLowerCase()) {
            case "materials":
                apiProps = OresCoreAPI.getApiMaterialOverrides().get(id);
                tomlProps = OVERRIDE_MATERIALS.get(id);
                targetObj = MaterialsDatabase.get(id);
                prot = OresCoreAPI.PROTECTED_MATERIAL_PROPS;
                break;
            case "items":
                apiProps = OresCoreAPI.getApiItemOverrides().get(id);
                tomlProps = OVERRIDE_ITEMS.get(id);
                try { targetObj = ItemType.valueOf(id.toUpperCase()).getDefaultOverride(); } catch (Exception e) {}
                prot = OresCoreAPI.PROTECTED_TYPE_PROPS;
                break;
            case "blocks":
                if (id.equalsIgnoreCase("ore")) return;
                apiProps = OresCoreAPI.getApiBlockOverrides().get(id);
                tomlProps = OVERRIDE_BLOCKS.get(id);
                try { targetObj = BlockType.valueOf(id.toUpperCase()).getDefaultOverride(); } catch (Exception e) {}
                prot = OresCoreAPI.PROTECTED_TYPE_PROPS;
                break;
            case "chemicals":
                apiProps = OresCoreAPI.getApiChemicalOverrides().get(id);
                tomlProps = OVERRIDE_CHEMICALS.get(id);
                try { targetObj = ChemicalType.valueOf(id.toUpperCase()).getDefaultOverride(); } catch (Exception e) {}
                prot = OresCoreAPI.PROTECTED_TYPE_PROPS;
                break;
        }

        if (targetObj != null) {
            
            if (apiProps != null) {
                if (targetObj instanceof Material m) m.applyOverrides(apiProps, prot);
                else if (targetObj instanceof ores.mathieu.material.ItemOverride io) io.applyOverrides(apiProps, prot);
                else if (targetObj instanceof ores.mathieu.material.ChemicalOverride co) co.applyOverrides(apiProps, prot);
            }
            
            if (tomlProps != null) {
                if (targetObj instanceof Material m) m.applyOverrides(tomlProps, prot);
                else if (targetObj instanceof ores.mathieu.material.ItemOverride io) io.applyOverrides(tomlProps, prot);
                else if (targetObj instanceof ores.mathieu.material.ChemicalOverride co) co.applyOverrides(tomlProps, prot);
            }
        }
    }

    public static Map<String, Map<String, String>> getOverrideMaterials() { return OVERRIDE_MATERIALS; }
    public static Map<String, Map<String, String>> getOverrideItems() { return OVERRIDE_ITEMS; }
    public static Map<String, Map<String, String>> getOverrideBlocks() { return OVERRIDE_BLOCKS; }
    public static Map<String, Map<String, String>> getOverrideChemicals() { return OVERRIDE_CHEMICALS; }

    public static Path getPropertiesFilePath() { return PROPERTIES_FILE; }

    public static void loadVeins() {
        try (CommentedFileConfig config = CommentedFileConfig.builder(ORE_GEN_FILE).sync().build()) {
            config.load();
            LOADED_VEINS.clear();

            if (config.contains("veins")) {
                List<? extends CommentedConfig> veinsList = config.get("veins");
                for (CommentedConfig veinEntry : veinsList) {
                    VeinConfig veinConfig = new VeinConfig();
                    veinConfig.materials = veinEntry.getOrElse("materials", Collections.emptyList());
                    List<Number> rawRatio = veinEntry.get("materialsRatio");
                    if (rawRatio != null) {
                        veinConfig.materialsRatio = new ArrayList<>();
                        for (Number n : rawRatio) veinConfig.materialsRatio.add(n.intValue());
                    }
                    veinConfig.veinType = veinEntry.get("veinType");
                    veinConfig.shape = veinEntry.getOrElse("shape", "BLOB");
                    veinConfig.distribution = veinEntry.getOrElse("distribution", "TRAPEZOID");
                    veinConfig.rarity = veinEntry.getIntOrElse("rarity", 10);
                    veinConfig.density = veinEntry.getIntOrElse("density", 8);
                    veinConfig.airExposureChance = veinEntry.<Double>getOrElse("airExposureChance", 0.5).floatValue();
                    veinConfig.minY = veinEntry.getIntOrElse("minY", -64);
                    veinConfig.maxY = veinEntry.getIntOrElse("maxY", 64);
                    veinConfig.specifics = veinEntry.getOrElse("specifics", "NONE");
                    veinConfig.associatedBlock = veinEntry.get("associatedBlock");
                    veinConfig.oreDensity = veinEntry.<Double>getOrElse("oreDensity", 0.1).floatValue();
                    veinConfig.bonusBlock = veinEntry.get("bonusBlock");
                    veinConfig.bonusBlockChance = veinEntry.<Double>getOrElse("bonusBlockChance", 0.05).floatValue();
                    veinConfig.dimensionsWhitelist = veinEntry.getOrElse("dimensionsWhitelist", Collections.emptyList());
                    veinConfig.dimensionsBlacklist = veinEntry.getOrElse("dimensionsBlacklist", Collections.emptyList());
                    veinConfig.biomesWhitelist = veinEntry.getOrElse("biomesWhitelist", Collections.emptyList());
                    veinConfig.biomesBlacklist = veinEntry.getOrElse("biomesBlacklist", Collections.emptyList());

                    LOADED_VEINS.add(veinConfig);
                }
            }
            OresCoreCommon.LOGGER.info("[ORES CORE] {} ore generation detected", LOADED_VEINS.size());
            ores.mathieu.client.OreTooltipData.invalidateCache();
        } catch (Exception e) {
            OresCoreCommon.LOGGER.error("[ORES CORE] Failed to load veins from ores-generation.toml!", e);
        }
    }

    private static void generateDefaultOreConfig() {
        OresCoreCommon.LOGGER.debug("[ORES CORE] Generating default ores-generation.toml...");

        StringBuilder toml = new StringBuilder();

        toml.append("# ORES CORE - Generation Configuration\n");
        toml.append("# This file manages how and where ores are generated in the world.\n");
        toml.append("# \n");
        toml.append("# OPTIONS:\n");
        toml.append("# - Modify in-game via the configuration menu.\n");
        toml.append("# - Modify online using the Ore Gen Manager tool at:\n");
        toml.append("#   https://mathieudorval.github.io/ORES-CORE/tools/ore_gen_manager.html\n");
        toml.append("#\n\n");

        for (String matName : ores.mathieu.registry.DiscoveryManager.REGISTRY.getOreGeneration()) {
            Material mat = MaterialsDatabase.get(matName);
            if (mat == null) continue;

            switch (matName) {
                case "iron" -> {
                    writeClassicVein(toml, matName, 90, 9, 0.0f, "TRAPEZOID", 80, 384, "minecraft:overworld");
                    writeClassicVein(toml, matName, 10, 9, 0.0f, "TRAPEZOID", -24, 56, "minecraft:overworld");
                    writeClassicVein(toml, matName, 10, 4, 0.0f, "UNIFORM", -64, 72, "minecraft:overworld");
                    writeGiantVein(toml, matName, "minecraft:tuff", "minecraft:raw_iron_block", 0.05f, 0.02f, -60, 0);
                }
                case "copper" -> {
                    writeClassicVein(toml, matName, 16, 10, 0.0f, "TRAPEZOID", -16, 112, "minecraft:overworld");
                    writeClassicVein(toml, matName, 16, 20, 0.0f, "TRAPEZOID", -16, 112, "minecraft:overworld");
                    writeGiantVein(toml, matName, "minecraft:granite", "minecraft:raw_copper_block", 0.05f, 0.02f, -16, 112);
                }
                case "gold" -> {
                    writeClassicVein(toml, matName, 4, 9, 0.5f, "TRAPEZOID", -64, 32, "minecraft:overworld");
                    writeClassicVein(toml, matName, 1, 9, 0.5f, "UNIFORM", -64, -48, "minecraft:overworld");
                    writeClassicVeinWithBiomes(toml, matName, 50, 9, 0.0f, "UNIFORM", 32, 256, List.of("minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands"), "minecraft:overworld");
                    writeClassicVein(toml, matName, 10, 10, 0.0f, "UNIFORM", 10, 117, "minecraft:the_nether");
                }
                case "coal" -> {
                    writeClassicVein(toml, matName, 30, 17, 0.0f, "UNIFORM", 136, 320, "minecraft:overworld");
                    writeClassicVein(toml, matName, 20, 17, 0.5f, "TRAPEZOID", 0, 192, "minecraft:overworld");
                }
                case "diamond" -> {
                    writeClassicVein(toml, matName, 7, 2, 0.5f, "TRAPEZOID", -80, 16, "minecraft:overworld");
                    writeClassicVein(toml, matName, 2, 6, 0.5f, "UNIFORM", -64, 16, "minecraft:overworld");
                    writeClassicVein(toml, matName, 4, 6, 1.0f, "TRAPEZOID", -80, 16, "minecraft:overworld");
                    writeClassicVein(toml, matName, 1, 8, 0.7f, "TRAPEZOID", -80, 16, "minecraft:overworld");
                }
                case "emerald" -> {
                    writeClassicVeinWithBiomes(toml, matName, 100, 3, 0.0f, "TRAPEZOID", -16, 480, List.of("minecraft:jagged_peaks", "minecraft:stony_peaks", "minecraft:frozen_peaks", "minecraft:meadow", "cherry_grove", "grove", "windswept_hills", "windswept_gravelly_hills", "windswept_forest"), "minecraft:overworld");
                }
                case "quartz" -> {
                    writeClassicVein(toml, matName, 16, 14, 0.0f, "UNIFORM", 10, 117, "minecraft:the_nether");
                }
                case "netherite" -> {
                    writeClassicVein(toml, matName, 1, 3, 1.0f, "TRAPEZOID", 8, 24, "minecraft:the_nether");
                    writeClassicVein(toml, matName, 1, 2, 1.0f, "UNIFORM", 8, 119, "minecraft:the_nether");
                }
                case "tin" -> {
                    writeClassicVein(toml, matName, 16, 9, 0.0f, "TRAPEZOID", -20, 112, "minecraft:overworld");
                }
                case "osmium" -> {
                    writeClassicVein(toml, matName, 8, 7, 0.0f, "TRAPEZOID", -64, 32, "minecraft:overworld");
                }
                case "silver" -> {
                    writeClassicVein(toml, matName, 7, 8, 0.0f, "TRAPEZOID", -64, 32, "minecraft:overworld");
                }
                case "lead" -> {
                    writeClassicVein(toml, matName, 10, 8, 0.0f, "TRAPEZOID", -64, 32, "minecraft:overworld");
                }
                case "uranium" -> {
                    writeClassicVein(toml, matName, 4, 4, 0.0f, "TRAPEZOID", -64, 0, "minecraft:overworld");
                }
                case "zinc" -> {
                    writeClassicVein(toml, matName, 12, 8, 0.0f, "UNIFORM", -64, 64, "minecraft:overworld");
                }
                case "aluminum" -> {
                    writeClassicVein(toml, matName, 14, 9, 0.0f, "TRAPEZOID", 0, 96, "minecraft:overworld");
                }
                case "nickel" -> {
                    writeClassicVein(toml, matName, 6, 7, 0.0f, "TRAPEZOID", -64, 16, "minecraft:overworld");
                }
                default -> {
                    writeClassicVein(toml, matName, 10, 8, 0.0f, "TRAPEZOID", -64, 64, "minecraft:overworld");
                }
            }
        }

        try (FileWriter writer = new FileWriter(ORE_GEN_FILE.toFile())) {
            writer.write(toml.toString());
            OresCoreCommon.LOGGER.debug("[ORES CORE] Successfully generated default ores-generation.toml");
        } catch (IOException e) {
            OresCoreCommon.LOGGER.error("[ORES CORE] Failed to generate default ores-generation.toml!", e);
        }
    }

    private static void writeClassicVein(StringBuilder sb, String mat, int rarity, int density, float air, String dist, int minY, int maxY, String... dims) {
        writeClassicVeinWithBiomes(sb, mat, rarity, density, air, dist, minY, maxY, Collections.emptyList(), dims);
    }

    private static void writeClassicVeinWithBiomes(StringBuilder sb, String mat, int rarity, int density, float air, String dist, int minY, int maxY, List<String> biomes, String... dims) {
        sb.append("[[veins]]\n");
        sb.append("\tmaterials = [\"").append(mat).append("\"]\n");
        sb.append("\tveinType = \"CLASSIC\"\n");
        sb.append("\trarity = ").append(rarity).append("\n");
        sb.append("\tdensity = ").append(density).append("\n");
        sb.append("\tshape = \"BLOB\"\n");
        sb.append("\tdistribution = \"").append(dist).append("\"\n");
        sb.append("\tminY = ").append(minY).append("\n");
        sb.append("\tmaxY = ").append(maxY).append("\n");

        sb.append("\tdimensionsWhitelist = [");
        for (int i = 0; i < dims.length; i++) {
            sb.append("\"").append(dims[i]).append("\"").append(i < dims.length - 1 ? ", " : "");
        }
        sb.append("]\n");

        sb.append("\tdimensionsBlacklist = []\n");

        sb.append("\tbiomesWhitelist = [");
        for (int i = 0; i < biomes.size(); i++) {
            sb.append("\"").append(biomes.get(i)).append("\"").append(i < biomes.size() - 1 ? ", " : "");
        }
        sb.append("]\n");

        sb.append("\tbiomesBlacklist = []\n");
        sb.append("\tairExposureChance = ").append(air).append("\n");
        sb.append("\tspecifics = \"NONE\"\n\n");
    }

    private static void writeGiantVein(StringBuilder sb, String mat, String block, String bonus, float dens, float bonusCh, int minY, int maxY) {
        sb.append("[[veins]]\n");
        sb.append("\tmaterials = [\"").append(mat).append("\"]\n");
        sb.append("\tveinType = \"GIANT\"\n");
        sb.append("\tassociatedBlock = \"").append(block).append("\"\n");
        sb.append("\toreDensity = ").append(dens).append("\n");
        sb.append("\tbonusBlock = \"").append(bonus).append("\"\n");
        sb.append("\tbonusBlockChance = ").append(bonusCh).append("\n");
        sb.append("\trarity = 200\n");
        sb.append("\tminY = ").append(minY).append("\n");
        sb.append("\tmaxY = ").append(maxY).append("\n");
        sb.append("\tdimensionsWhitelist = [\"minecraft:overworld\"]\n");
        sb.append("\tdimensionsBlacklist = []\n");
        sb.append("\tbiomesWhitelist = []\n");
        sb.append("\tbiomesBlacklist = []\n\n");
    }

    public static void saveVeins() {
        StringBuilder sb = new StringBuilder();
        sb.append("# ORES CORE config file\n");
        sb.append("# Modified via in-game configuration\n");
        sb.append("# Can be modified via the online configurator at:\n");
        sb.append("# https://mathieudorval.github.io/ORES-CORE/tools/ore_gen_manager.html\n\n");

        for (VeinConfig vein : LOADED_VEINS) {
            sb.append("[[veins]]\n");

            
            sb.append("\tmaterials = [");
            if (vein.materials != null) {
                for (int i = 0; i < vein.materials.size(); i++) {
                    sb.append("\"").append(vein.materials.get(i)).append("\"");
                    if (i < vein.materials.size() - 1) sb.append(", ");
                }
            }
            sb.append("]\n");

            
            if (vein.materialsRatio != null && !vein.materialsRatio.isEmpty()) {
                sb.append("\tmaterialsRatio = [");
                for (int i = 0; i < vein.materialsRatio.size(); i++) {
                    sb.append(vein.materialsRatio.get(i));
                    if (i < vein.materialsRatio.size() - 1) sb.append(", ");
                }
                sb.append("]\n");
            }

            
            sb.append("\tveinType = \"").append(vein.veinType != null ? vein.veinType : "CLASSIC").append("\"\n");

            if ("CLASSIC".equalsIgnoreCase(vein.veinType) || vein.veinType == null) {
                sb.append("\tshape = \"").append(vein.shape).append("\"\n");
                sb.append("\tdistribution = \"").append(vein.distribution).append("\"\n");
                sb.append("\tdensity = ").append(vein.density).append("\n");
                sb.append("\tairExposureChance = ").append(vein.airExposureChance).append("\n");
                sb.append("\tspecifics = \"").append(vein.specifics != null ? vein.specifics : "NONE").append("\"\n");
            } else {
                sb.append("\tassociatedBlock = \"").append(vein.associatedBlock != null ? vein.associatedBlock : "").append("\"\n");
                sb.append("\toreDensity = ").append(vein.oreDensity).append("\n");
                if (vein.bonusBlock != null && !vein.bonusBlock.isEmpty()) {
                    sb.append("\tbonusBlock = \"").append(vein.bonusBlock).append("\"\n");
                    sb.append("\tbonusBlockChance = ").append(vein.bonusBlockChance).append("\n");
                }
            }

            sb.append("\trarity = ").append(vein.rarity).append("\n");
            sb.append("\tminY = ").append(vein.minY).append("\n");
            sb.append("\tmaxY = ").append(vein.maxY).append("\n");

            
            writeStringList(sb, "dimensionsWhitelist", vein.dimensionsWhitelist);
            writeStringList(sb, "dimensionsBlacklist", vein.dimensionsBlacklist);
            writeStringList(sb, "biomesWhitelist", vein.biomesWhitelist);
            writeStringList(sb, "biomesBlacklist", vein.biomesBlacklist);

            sb.append("\n");
        }

        try (FileWriter fw = new FileWriter(ORE_GEN_FILE.toFile())) {
            fw.write(sb.toString());
            OresCoreCommon.LOGGER.info("[ORES CORE] Saved ore generation config ({} veins).", LOADED_VEINS.size());
        } catch (IOException e) {
            OresCoreCommon.LOGGER.error("[ORES CORE] Failed to save ores-generation.toml!", e);
        }

        ores.mathieu.client.OreTooltipData.invalidateCache();
    }

    public static void saveProperties() {
        StringBuilder sb = new StringBuilder();
        sb.append("# ORES CORE config file\n");
        sb.append("# Modified via in-game configuration\n");

        writeOverrideSection(sb, "materials", OVERRIDE_MATERIALS);
        writeOverrideSection(sb, "items", OVERRIDE_ITEMS);
        writeOverrideSection(sb, "blocks", OVERRIDE_BLOCKS);
        writeOverrideSection(sb, "chemicals", OVERRIDE_CHEMICALS);

        try (FileWriter fw = new FileWriter(PROPERTIES_FILE.toFile())) {
            fw.write(sb.toString());
            OresCoreCommon.LOGGER.info("[ORES CORE] Saved properties config to properties.toml");
        } catch (IOException e) {
            OresCoreCommon.LOGGER.error("[ORES CORE] Failed to save properties.toml!", e);
        }
    }

    private static void writeOverrideSection(StringBuilder sb, String section, Map<String, Map<String, String>> data) {
        if (data.isEmpty()) return;
        
        sb.append("[").append(section).append("]\n");
        List<String> sortedIds = new ArrayList<>(data.keySet());
        Collections.sort(sortedIds);

        for (String id : sortedIds) {
            Map<String, String> props = data.get(id);
            if (props == null || props.isEmpty()) continue;

            sb.append("\t[").append(section).append(".").append(id).append("]\n");
            List<String> sortedKeys = new ArrayList<>(props.keySet());
            Collections.sort(sortedKeys);

            for (String key : sortedKeys) {
                String val = props.get(key);
                
                if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("false")) {
                    sb.append("\t\t").append(key).append(" = ").append(val.toLowerCase()).append("\n");
                } else {
                    try {
                        if (val.startsWith("0x")) {
                             Integer.decode(val);
                             sb.append("\t\t").append(key).append(" = \"").append(val).append("\"\n");
                        } else {
                             Double.parseDouble(val);
                             sb.append("\t\t").append(key).append(" = ").append(val).append("\n");
                        }
                    } catch (NumberFormatException e) {
                        sb.append("\t\t").append(key).append(" = \"").append(val.replace("\"", "\\\"")).append("\"\n");
                    }
                }
            }
            sb.append("\n");
        }
        sb.append("\n");
    }

    private static void writeStringList(StringBuilder sb, String key, List<String> list) {
        sb.append("\t").append(key).append(" = [");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                sb.append("\"").append(list.get(i)).append("\"");
                if (i < list.size() - 1) sb.append(", ");
            }
        }
        sb.append("]\n");
    }
}
