//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Computes and caches tooltip information for ore blocks.
// Merges data from internal material properties, tag scanner, and TOML 
// generation configurations to provide players with precise mining levels, 
// required tools, and exact dimension/height ranges directly in the UI.
//
// Author: __mathieu
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.client;

import ores.mathieu.config.ConfigManager;
import ores.mathieu.material.Material;
import ores.mathieu.worldgen.VeinConfig;

import java.util.*;

@SuppressWarnings("null")
public class OreTooltipData {

    
    public static final String DIM_ALL = "all";

    public static class DimensionRange {
        public final String dimensionId;
        public int minY;
        public int maxY;

        public DimensionRange(String dimensionId, int minY, int maxY) {
            this.dimensionId = dimensionId;
            this.minY = minY;
            this.maxY = maxY;
        }

        public void expand(int newMinY, int newMaxY) {
            this.minY = Math.min(this.minY, newMinY);
            this.maxY = Math.max(this.maxY, newMaxY);
        }
    }

    public static class OreInfo {
        public final String materialName;
        public final int miningLevel;
        public final String toolType;
        public final List<DimensionRange> dimensionRanges;

        public OreInfo(String materialName, int miningLevel, String toolType, List<DimensionRange> dimensionRanges) {
            this.materialName = materialName;
            this.miningLevel = miningLevel;
            this.toolType = toolType;
            this.dimensionRanges = dimensionRanges;
        }
    }

    private static final Map<String, OreInfo> CACHE = new HashMap<>();
    private static long lastConfigVersion = -1;
    private static long configVersion = 0;

    public static void invalidateCache() {
        configVersion++;
    }

    public static OreInfo getOreInfo(String oreRegistryName) {
        if (lastConfigVersion != configVersion) {
            rebuildCache();
            lastConfigVersion = configVersion;
        }
        return CACHE.get(oreRegistryName);
    }

    private static void rebuildCache() {
        CACHE.clear();

        if (ores.mathieu.registry.DiscoveryManager.DECODED_DATA == null) return;

        for (ores.mathieu.registry.RegistryDecoder.ResolvedOre ore : ores.mathieu.registry.DiscoveryManager.DECODED_DATA.ores) {
            Material material = ore.material;
            String stoneName = ore.stoneReplacement;
            String stonePath = stoneName.contains(":") ? stoneName.split(":")[1] : stoneName;
            String oreName = stonePath + "_" + material.getName() + "_ore";

            String toolType = determineToolType(stoneName, stonePath);
            int baseBlockLevel = determineBaseBlockLevel(stoneName);
            int finalLevel = Math.max(baseBlockLevel, material.getMiningLevel());

            List<DimensionRange> ranges = buildDimensionRanges(material.getName());
            CACHE.put(oreName, new OreInfo(material.getName(), finalLevel, toolType, ranges));
        }

        
        Material netherite = ores.mathieu.material.MaterialsDatabase.get("netherite");
        if (netherite != null) {
            int debrisLevel = Math.max(3, netherite.getMiningLevel()); 
            List<DimensionRange> ranges = buildDimensionRanges("netherite");
            CACHE.put("minecraft:ancient_debris", new OreInfo("netherite", debrisLevel, "pickaxe", ranges));
        }
    }

    private static List<DimensionRange> buildDimensionRanges(String materialName) {
        Map<String, DimensionRange> dimMap = new LinkedHashMap<>();
        for (VeinConfig vein : ConfigManager.LOADED_VEINS) {
            if (vein.materials == null || !vein.materials.contains(materialName)) continue;

            List<String> dims = new ArrayList<>();
            boolean hasWhitelist = vein.dimensionsWhitelist != null && !vein.dimensionsWhitelist.isEmpty();

            if (hasWhitelist) {
                dims.addAll(vein.dimensionsWhitelist);
            } else {
                
                dims.add(DIM_ALL);
            }

            for (String dim : dims) {
                DimensionRange existing = dimMap.get(dim);
                if (existing != null) {
                    existing.expand(vein.minY, vein.maxY);
                } else {
                    dimMap.put(dim, new DimensionRange(dim, vein.minY, vein.maxY));
                }
            }
        }
        return new ArrayList<>(dimMap.values());
    }

    private static int determineBaseBlockLevel(String stoneFullId) {
        net.minecraft.resources.Identifier baseId = net.minecraft.resources.Identifier.tryParse(stoneFullId);
        if (baseId != null) {
            if (ores.mathieu.registry.TagScanner.hasTag(baseId, net.minecraft.resources.Identifier.parse("minecraft:needs_diamond_tool"))) {
                return 3;
            } else if (ores.mathieu.registry.TagScanner.hasTag(baseId, net.minecraft.resources.Identifier.parse("minecraft:needs_iron_tool"))) {
                return 2;
            } else if (ores.mathieu.registry.TagScanner.hasTag(baseId, net.minecraft.resources.Identifier.parse("minecraft:needs_stone_tool"))) {
                return 1;
            }
        }
        return 0;
    }

    private static String determineToolType(String stoneFullId, String stonePath) {
        net.minecraft.resources.Identifier baseId = net.minecraft.resources.Identifier.tryParse(stoneFullId);
        if (baseId != null) {
            if (ores.mathieu.registry.TagScanner.hasTag(baseId, net.minecraft.resources.Identifier.parse("minecraft:mineable/shovel"))) {
                return "shovel";
            } else if (ores.mathieu.registry.TagScanner.hasTag(baseId, net.minecraft.resources.Identifier.parse("minecraft:mineable/axe"))) {
                return "axe";
            } else if (ores.mathieu.registry.TagScanner.hasTag(baseId, net.minecraft.resources.Identifier.parse("minecraft:mineable/hoe"))) {
                return "hoe";
            } else if (ores.mathieu.registry.TagScanner.hasTag(baseId, net.minecraft.resources.Identifier.parse("minecraft:mineable/pickaxe"))) {
                return "pickaxe";
            }
        }

        if (stonePath.contains("sand") || stonePath.contains("gravel") || stonePath.contains("dirt") || stonePath.contains("clay")) {
            return "shovel";
        } else if (stonePath.contains("wood") || stonePath.contains("log") || stonePath.contains("planks")) {
            return "axe";
        }
        return "pickaxe";
    }

    public static String getToolTranslationKey(String toolType) {
        return "tooltip.ores.tool." + toolType;
    }

    public static String getLevelTranslationKey(int level) {
        return switch (level) {
            case 0 -> "tooltip.ores.level.wood";
            case 1 -> "tooltip.ores.level.stone";
            case 2 -> "tooltip.ores.level.iron";
            case 3 -> "tooltip.ores.level.diamond";
            case 4 -> "tooltip.ores.level.netherite";
            default -> "tooltip.ores.level.netherite";
        };
    }

    public static int getMiningLevelColor(int level) {
        return switch (level) {
            case 0 -> 0xC8A96E;  
            case 1 -> 0xAAAAAA;  
            case 2 -> 0xD4D4D4;  
            case 3 -> 0x5CE6E6;  
            case 4 -> 0x8B4E6B;  
            default -> 0xFF5555;
        };
    }

    public static String getDimensionTranslationKey(String dimId) {
        if (DIM_ALL.equals(dimId)) return "tooltip.ores.dim.all";
        return switch (dimId) {
            case "minecraft:overworld" -> "tooltip.ores.dim.overworld";
            case "minecraft:the_nether" -> "tooltip.ores.dim.nether";
            case "minecraft:the_end" -> "tooltip.ores.dim.end";
            case "aether:the_aether" -> "tooltip.ores.dim.aether";
            case "twilightforest:twilight_forest" -> "tooltip.ores.dim.twilight_forest";
            case "ad_astra:moon" -> "tooltip.ores.dim.moon";
            case "ad_astra:mars" -> "tooltip.ores.dim.mars";
            case "ad_astra:venus" -> "tooltip.ores.dim.venus";
            case "ad_astra:mercury" -> "tooltip.ores.dim.mercury";
            case "ad_astra:glacio" -> "tooltip.ores.dim.glacio";
            case "undergarden:undergarden" -> "tooltip.ores.dim.undergarden";
            case "deeperdarker:otherside" -> "tooltip.ores.dim.otherside";
            case "tropicraft:tropics" -> "tooltip.ores.dim.tropics";
            case "allthemodium:mining" -> "tooltip.ores.dim.mining";
            case "allthemodium:the_other" -> "tooltip.ores.dim.the_other";
            case "allthemodium:the_beyond" -> "tooltip.ores.dim.the_beyond";
            case "blue_skies:everbright" -> "tooltip.ores.dim.everbright";
            case "blue_skies:everdawn" -> "tooltip.ores.dim.everdawn";
            case "lostcities:lostcity" -> "tooltip.ores.dim.lostcity";
            case "voidscape:void" -> "tooltip.ores.dim.void";
            default -> null;
        };
    }

    public static String getDimensionFallbackName(String dimId) {
        String path = dimId.contains(":") ? dimId.split(":")[1] : dimId;
        StringBuilder sb = new StringBuilder();
        for (String part : path.split("_")) {
            if (part.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }

    public static int getDimensionColor(String dimId) {
        if (DIM_ALL.equals(dimId)) return 0xFFAA00;
        return switch (dimId) {
            case "minecraft:overworld" -> 0x55FF55;  
            case "minecraft:the_nether" -> 0xFF5555;  
            case "minecraft:the_end" -> 0xFFFF55;     
            case "aether:the_aether" -> 0x88CCFF;     
            case "twilightforest:twilight_forest" -> 0x44AA44; 
            case "ad_astra:moon" -> 0xCCCCCC;         
            case "ad_astra:mars" -> 0xCC4422;         
            case "ad_astra:venus" -> 0xFFAA33;        
            case "ad_astra:mercury" -> 0x888888;      
            case "ad_astra:glacio" -> 0x66DDFF;       
            case "undergarden:undergarden" -> 0x6644AA; 
            case "deeperdarker:otherside" -> 0x222266; 
            case "tropicraft:tropics" -> 0x33CCAA;     
            case "allthemodium:mining" -> 0xDDAA33;    
            case "allthemodium:the_other" -> 0xAA3355; 
            case "allthemodium:the_beyond" -> 0x9955FF; 
            case "blue_skies:everbright" -> 0xFFDD77;  
            case "blue_skies:everdawn" -> 0xFF7744;    
            case "lostcities:lostcity" -> 0x999999;    
            case "voidscape:void" -> 0x220044;         
            default -> 0xAA55FF;
        };
    }
}
