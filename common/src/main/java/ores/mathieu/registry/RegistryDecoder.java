//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Combinatorial computation engine. Takes all loaded raw materials 
// and valid host blocks, cross-multiplies them via Cartesian product logic, 
// resolving the exhaustive flat list of explicit items/blocks to manufacture.
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

import ores.mathieu.material.*;
import ores.mathieu.OresCoreCommon;
import java.util.ArrayList;
import java.util.List;

public class RegistryDecoder {

    public static class ResolvedObject {
        public final Material material;
        public final Enum<?> type;
        public final String registryName;

        public ResolvedObject(Material material, Enum<?> type, String registryName) {
            this.material = material;
            this.type = type;
            this.registryName = registryName;
        }
    }

    public static class ResolvedOre {
        public final Material material;
        public final String stoneReplacement;

        public ResolvedOre(Material material, String stoneReplacement) {
            this.material = material;
            this.stoneReplacement = stoneReplacement;
        }
    }

    public static class DecodedRegistry {
        public final List<ResolvedObject> objects = new ArrayList<>();
        public final List<ResolvedOre> ores = new ArrayList<>();
        public final java.util.Set<String> registeredNames = new java.util.HashSet<>();
    }

    public static DecodedRegistry decode(OresRegistryData data) {
        DecodedRegistry decoded = new DecodedRegistry();

        if (data.getMaterialsRegistry().contains("debug")) {
            OresCoreCommon.LOGGER.debug("[ORES CORE] DEBUG MODE: Activating all possible IDs for all materials.");
            for (Material material : MaterialsDatabase.MATERIALS.values()) {
                for (ItemType type : ItemType.values()) {
                    String baseName = type.name().equalsIgnoreCase(material.getBaseType()) ? material.getBaseItemName() : material.getName();
                    String expectedName = String.format(type.getDefaultOverride().getNamingPattern(), baseName);
                    ensureObjectRegistered(decoded, material, type, expectedName);
                }
                for (BlockType type : BlockType.values()) {
                    if (type == BlockType.ORE) continue;
                    String expectedName = String.format(type.getDefaultOverride().getNamingPattern(), material.getName());
                    ensureObjectRegistered(decoded, material, type, expectedName);
                }
                for (ChemicalType type : ChemicalType.values()) {
                    String expectedName = String.format(type.getDefaultOverride().getNamingPattern(), material.getName());
                    ensureObjectRegistered(decoded, material, type, expectedName);
                }
            }
        } else {
            for (String requested : data.getMaterialsRegistry()) {
                boolean found = false;

                if (requested.endsWith("_ore")) {
                    OresCoreCommon.LOGGER.warn("[ORES CORE] Invalid request '{}' in materials_registry. Ores must be requested via ore_generation array!", requested);
                    continue;
                }

                for (Material material : MaterialsDatabase.MATERIALS.values()) {

                    for (ItemType type : ItemType.values()) {
                        String baseName = type.name().equalsIgnoreCase(material.getBaseType()) ? material.getBaseItemName() : material.getName();
                        String canonicalName = String.format(type.getDefaultOverride().getNamingPattern(), baseName);
                        if (requested.equals(canonicalName)) {
                            if (decoded.registeredNames.add(canonicalName)) {
                                decoded.objects.add(new ResolvedObject(material, type, canonicalName));
                            }
                            found = true;
                            break;
                        }
                        
                        for (String compatPattern : type.getDefaultOverride().getCompatiblePatterns()) {
                            if (requested.equals(String.format(compatPattern, baseName))) {
                                OresCoreCommon.LOGGER.info("[ORES CORE] Alias '{}' matched to canonical ID '{}'", requested, canonicalName);
                                if (decoded.registeredNames.add(canonicalName)) {
                                    decoded.objects.add(new ResolvedObject(material, type, canonicalName));
                                }
                                found = true;
                                break;
                            }
                        }
                        if (found) break;
                    }
                    if (found) break;

                    for (BlockType type : BlockType.values()) {

                        if (type == BlockType.ORE) {
                            continue;
                        }
                        String canonicalName = String.format(type.getDefaultOverride().getNamingPattern(), material.getName());
                        if (requested.equals(canonicalName)) {
                            ensureObjectRegistered(decoded, material, type, canonicalName);
                            found = true;
                            break;
                        }
                        
                        for (String compatPattern : type.getDefaultOverride().getCompatiblePatterns()) {
                            if (requested.equals(String.format(compatPattern, material.getName()))) {
                                OresCoreCommon.LOGGER.info("[ORES CORE] Alias '{}' matched to canonical ID '{}'", requested, canonicalName);
                                ensureObjectRegistered(decoded, material, type, canonicalName);
                                found = true;
                                break;
                            }
                        }
                        if (found) break;
                    }
                    if (found) break;

                    for (ChemicalType type : ChemicalType.values()) {
                        String canonicalName = String.format(type.getDefaultOverride().getNamingPattern(), material.getName());
                        if (requested.equals(canonicalName)) {
                            if (decoded.registeredNames.add(canonicalName)) {
                                decoded.objects.add(new ResolvedObject(material, type, canonicalName));
                            }
                            found = true;
                            break;
                        }
                        
                        for (String compatPattern : type.getDefaultOverride().getCompatiblePatterns()) {
                            if (requested.equals(String.format(compatPattern, material.getName()))) {
                                OresCoreCommon.LOGGER.info("[ORES CORE] Alias '{}' matched to canonical ID '{}'", requested, canonicalName);
                                if (decoded.registeredNames.add(canonicalName)) {
                                    decoded.objects.add(new ResolvedObject(material, type, canonicalName));
                                }
                                found = true;
                                break;
                            }
                        }
                        if (found) break;
                    }
                    if (found) break;
                }

                if (!found) {
                    OresCoreCommon.LOGGER.warn("[ORES CORE] Could not map request '{}' to any known Material + Type combination.", requested);
                }
            }
        }

        for (String oreMaterialName : data.getOreGeneration()) {
            Material material = MaterialsDatabase.get(oreMaterialName);
            if (material != null) {

                String dropItemName = material.getOreDropItem();
                if (dropItemName != null && !dropItemName.isEmpty() && !dropItemName.contains(":")) {
                    ensureObjectRegistered(decoded, material, dropItemName);
                }

                String baseType = material.getBaseType();
                if (baseType != null && !baseType.isEmpty()) {
                    for (ItemType type : ItemType.values()) {
                        if (type.name().equalsIgnoreCase(baseType) || type.getSuffix().equalsIgnoreCase(baseType)) {
                            String baseName = material.getBaseItemName();
                            String expectedBaseName = String.format(type.getDefaultOverride().getNamingPattern(), baseName);
                            ensureObjectRegistered(decoded, material, type, expectedBaseName);
                            break;
                        }
                    }
                }

                for (String stone : data.getStonesReplacement().keySet()) {

                    decoded.ores.add(new ResolvedOre(material, stone));
                }
            } else {
                OresCoreCommon.LOGGER.warn("[ORES CORE] Unknown material '{}' requested in ore_generation.", oreMaterialName);
            }
        }

        if (ores.mathieu.compat.MekanismCompat.isMekanismLoaded()) {
            java.util.List<Material> activeMaterials = new ArrayList<>();
            for (ResolvedObject obj : decoded.objects) {
                if (!activeMaterials.contains(obj.material)) {
                    activeMaterials.add(obj.material);
                }
            }

            for (Material material : activeMaterials) {
                boolean hasDust = decoded.objects.stream().anyMatch(o -> o.material == material && o.type == ItemType.DUST);
                boolean hasDirtyDust = decoded.objects.stream().anyMatch(o -> o.material == material && o.type == ItemType.DIRTY_DUST);
                boolean hasClump = decoded.objects.stream().anyMatch(o -> o.material == material && o.type == ItemType.CLUMP);
                boolean hasShard = decoded.objects.stream().anyMatch(o -> o.material == material && o.type == ItemType.SHARD);
                boolean hasCrystal = decoded.objects.stream().anyMatch(o -> o.material == material && o.type == ItemType.CRYSTAL);
                boolean hasCleanSlurry = decoded.objects.stream().anyMatch(o -> o.material == material && o.type == ChemicalType.CLEAN);
                boolean hasDirtySlurry = decoded.objects.stream().anyMatch(o -> o.material == material && o.type == ChemicalType.DIRTY);

                if (hasDirtySlurry) hasCleanSlurry = true;
                if (hasCleanSlurry) hasCrystal = true;
                if (hasCrystal) hasShard = true;
                if (hasShard) hasClump = true;
                if (hasClump) hasDirtyDust = true;
                if (hasDirtyDust) hasDust = true;

                if (hasCleanSlurry) {
                    String cleanName = String.format(ChemicalType.CLEAN.getDefaultOverride().getNamingPattern(), material.getName());
                    ensureObjectRegistered(decoded, material, ChemicalType.CLEAN, cleanName);
                }
                if (hasDirtySlurry) {
                    String dirtyName = String.format(ChemicalType.DIRTY.getDefaultOverride().getNamingPattern(), material.getName());
                    ensureObjectRegistered(decoded, material, ChemicalType.DIRTY, dirtyName);
                }
                if (hasCrystal) {
                    String crystalName = String.format(ItemType.CRYSTAL.getDefaultOverride().getNamingPattern(), material.getName());
                    ensureObjectRegistered(decoded, material, ItemType.CRYSTAL, crystalName);
                }
                if (hasShard) {
                    String shardName = String.format(ItemType.SHARD.getDefaultOverride().getNamingPattern(), material.getName());
                    ensureObjectRegistered(decoded, material, ItemType.SHARD, shardName);
                }
                if (hasClump) {
                    String clumpName = String.format(ItemType.CLUMP.getDefaultOverride().getNamingPattern(), material.getName());
                    ensureObjectRegistered(decoded, material, ItemType.CLUMP, clumpName);
                }
                if (hasDirtyDust) {
                    String dirtyDustName = String.format(ItemType.DIRTY_DUST.getDefaultOverride().getNamingPattern(), material.getName());
                    ensureObjectRegistered(decoded, material, ItemType.DIRTY_DUST, dirtyDustName);
                }
                if (hasDust) {
                    String dustName = String.format(ItemType.DUST.getDefaultOverride().getNamingPattern(), material.getName());
                    ensureObjectRegistered(decoded, material, ItemType.DUST, dustName);
                    
                    String baseTypeString = material.getBaseType();
                    if (baseTypeString != null) {
                        try {
                            ItemType baseType = ItemType.valueOf(baseTypeString.toUpperCase());
                            String baseName = material.getBaseItemName() != null ? material.getBaseItemName() : material.getName();
                            String expectedName = String.format(baseType.getDefaultOverride().getNamingPattern(), baseName);
                            ensureObjectRegistered(decoded, material, baseType, expectedName);
                        } catch (IllegalArgumentException ignored) {}
                    }
                }
            }
        }

        java.util.Iterator<ResolvedObject> objIt = decoded.objects.iterator();
        while (objIt.hasNext()) {
            ResolvedObject obj = objIt.next();
            if (isObjectBlacklisted(data, obj)) {
                OresCoreCommon.LOGGER.info("[ORES CORE] Blacklisted object '{}' (or its alias) skipped.", obj.registryName);
                objIt.remove();
            }
        }

        java.util.Iterator<ResolvedOre> oreIt = decoded.ores.iterator();
        while (oreIt.hasNext()) {
            ResolvedOre ro = oreIt.next();
            String path = ro.stoneReplacement.contains(":") ? ro.stoneReplacement.split(":")[1] : ro.stoneReplacement;
            String oreName = path + "_" + ro.material.getName() + "_ore";
            if (data.isBlacklisted(oreName)) {
                OresCoreCommon.LOGGER.info("[ORES CORE] Blacklisted ore '{}' skipped.", oreName);
                oreIt.remove();
            }
        }

        return decoded;
    }

    private static boolean isObjectBlacklisted(OresRegistryData data, ResolvedObject obj) {
        if (data.isBlacklisted(obj.registryName)) return true;

        if (obj.type instanceof ItemType) {
            ItemType type = (ItemType) obj.type;
            String baseName = type.name().equalsIgnoreCase(obj.material.getBaseType()) ? obj.material.getBaseItemName() : obj.material.getName();
            for (String pattern : type.getDefaultOverride().getCompatiblePatterns()) {
                if (data.isBlacklisted(String.format(pattern, baseName))) return true;
            }
        } else if (obj.type instanceof BlockType) {
            BlockType type = (BlockType) obj.type;
            for (String pattern : type.getDefaultOverride().getCompatiblePatterns()) {
                if (data.isBlacklisted(String.format(pattern, obj.material.getName()))) return true;
            }
        } else if (obj.type instanceof ChemicalType) {
            ChemicalType type = (ChemicalType) obj.type;
            for (String pattern : type.getDefaultOverride().getCompatiblePatterns()) {
                if (data.isBlacklisted(String.format(pattern, obj.material.getName()))) return true;
            }
        }
        return false;
    }

    private static void ensureObjectRegistered(DecodedRegistry decoded, Material material, String registryName) {

        for (ItemType type : ItemType.values()) {
            String baseName = type.name().equalsIgnoreCase(material.getBaseType()) ? material.getBaseItemName() : material.getName();
            String expected = String.format(type.getDefaultOverride().getNamingPattern(), baseName);
            if (registryName.equals(expected)) {
                ensureObjectRegistered(decoded, material, type, registryName);
                return;
            }
        }
        for (BlockType type : BlockType.values()) {
            if (type == BlockType.ORE) continue;
            String expected = String.format(type.getDefaultOverride().getNamingPattern(), material.getName());
            if (registryName.equals(expected)) {
                ensureObjectRegistered(decoded, material, type, registryName);
                return;
            }
        }
    }

    private static void ensureObjectRegistered(DecodedRegistry decoded, Material material, Enum<?> type, String registryName) {
        if (!decoded.registeredNames.add(registryName)) return;

        OresCoreCommon.LOGGER.debug("[ORES CORE] Auto-activating object '{}' (type: {}) for material '{}'.", registryName, type, material.getName());
        decoded.objects.add(new ResolvedObject(material, type, registryName));

        if (type instanceof BlockType) {
            BlockType bt = (BlockType) type;
            BlockType parent = bt.getParentType();
            if (parent != null) {
                String parentName = String.format(parent.getDefaultOverride().getNamingPattern(), material.getName());
                ensureObjectRegistered(decoded, material, parent, parentName);
            }
        }
    }
}
