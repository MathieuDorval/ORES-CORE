//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Manages loading and saving of mod configuration files.
//
// Author: __mathieu
// Version: 26.1.001
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
import ores.mathieu.worldgen.VeinConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("null")
public class ConfigManager {
    private static final Path CONFIG_DIR = Services.getPlatform().getConfigDir().resolve("ores");
    private static final Path ORE_GEN_FILE = CONFIG_DIR.resolve("ores-generation.toml");
    public static final List<VeinConfig> LOADED_VEINS = new ArrayList<>();

    public static void preInitialize() {
        if (Files.exists(ORE_GEN_FILE)) {
            loadVeins();
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
            OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] ores-generation.toml already exists. Using user configuration.");
        }
    }

    private static void loadVeins() {
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
        } catch (Exception e) {
            OresCoreCommon.LOGGER.error("[ORES CORE] Failed to load veins from ores-generation.toml!", e);
        }
    }

    private static void generateDefaultOreConfig() {
        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Generating default ores-generation.toml...");

        StringBuilder toml = new StringBuilder();

        toml.append("# OresCore Generation Configuration\n\n");
        toml.append("# === GLOBAL PARAMETERS ===\n");
        toml.append("# - materials: List of materials (ores) in this vein (ex: [\"iron\", \"coal\"]).\n");
        toml.append("# - [OPTIONAL] materialsRatio: Weights for each material to define probability ratios (ex: [3, 1] means 75%/25%).\n");
        toml.append("# - veinType: CLASSIC (small blobs/veins) or GIANT (massive deposits replaced in specific blocks).\n");
        toml.append("# - rarity: CLASSIC (Attempts per chunk) | GIANT (1 chance in N chunks).\n");
        toml.append("# - minY / maxY: Altitude range for generation (ex: -64 to 320).\n");
        toml.append("# - [OPTIONAL] dimensionsWhitelist / dimensionsBlacklist: List of dimension IDs.\n");
        toml.append("# - [OPTIONAL] biomesWhitelist / biomesBlacklist: List of biome IDs.\n\n");
        toml.append("# === CLASSIC SPECIFIC ===\n");
        toml.append("# - density: Approximate number of ore blocks per vein.\n");
        toml.append("# - shape: BLOB (Sphere), PLATE (Flat disk), HORIZONTAL (Tube), VERTICAL (Column), SCATTERED (Dispersed).\n");
        toml.append("# - distribution:\n");
        toml.append("#     * UNIFORM: Equal probability throughout the Y range.\n");
        toml.append("#     * TRAPEZOID: Peak probability in the middle of the Y range, tapering off at ends.\n");
        toml.append("#     * GAUSSIAN: Bell curve centered in the middle of the Y range (more concentrated).\n");
        toml.append("#     * TRIANGLE_HIGH: Linear increase from minY (0%) to maxY (100%).\n");
        toml.append("#     * TRIANGLE_LOW: Linear decrease from minY (100%) to maxY (0%).\n");
        toml.append("# - [OPTIONAL] airExposureChance: 0.0 (always generate) to 1.0 (never generate if touching a cave wall).\n");
        toml.append("# - [OPTIONAL] specifics:\n");
        toml.append("#     * NONE: No special generation rules.\n");
        toml.append("#     * AIR_ONLY: Only generate if surrounded by air.\n");
        toml.append("#     * CAVE_ONLY: Only generate if touching at least one air block (cave walls).\n");
        toml.append("#     * WATER_ONLY: Only generate in water.\n");
        toml.append("#     * LAVA_ONLY: Only generate in lava.\n");
        toml.append("#     * UNIQUE: Prevents placing two identical ores side-by-side (dispersed effect).\n\n");
        toml.append("# === GIANT SPECIFIC ===\n");
        toml.append("# - associatedBlock: The host block to be replaced (ex: \"minecraft:tuff\").\n");
        toml.append("# - oreDensity: Probability (0.0 to 1.0) of a block being an ore.\n");
        toml.append("# - [OPTIONAL] bonusBlock: Optional rare block (ex: \"minecraft:raw_iron_block\").\n");
        toml.append("# - [OPTIONAL] bonusBlockChance: Probability of the bonus block appearing.\n");
        toml.append("# - rarity: 1 chance in N chunks.\n");
        toml.append("# - minY / maxY: Altitude range for generation.\n\n");

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
            OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Successfully generated default ores-generation.toml");
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
}
