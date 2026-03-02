//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Decodes registry.json data into internal material and object structures.
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
    }

    public static DecodedRegistry decode(OresRegistryData data) {
        DecodedRegistry decoded = new DecodedRegistry();

        if (data.getMaterialsRegistry().contains("debug")) {
            OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] DEBUG MODE: Activating all possible IDs for all materials.");
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
                    OresCoreCommon.LOGGER.warn("[ORES CORE WARNING] Invalid request '{}' in materials_registry. Ores must be requested via ore_generation array!", requested);
                    continue;
                }

                for (Material material : MaterialsDatabase.MATERIALS.values()) {

                    for (ItemType type : ItemType.values()) {
                        String baseName = type.name().equalsIgnoreCase(material.getBaseType()) ? material.getBaseItemName() : material.getName();
                        String expectedName = String.format(type.getDefaultOverride().getNamingPattern(), baseName);
                        if (requested.equals(expectedName)) {
                            decoded.objects.add(new ResolvedObject(material, type, requested));
                            found = true;
                            break;
                        }
                    }
                    if (found) break;

                    for (BlockType type : BlockType.values()) {

                        if (type == BlockType.ORE) {
                            continue;
                        }
                        String expectedName = String.format(type.getDefaultOverride().getNamingPattern(), material.getName());
                        if (requested.equals(expectedName)) {
                            ensureObjectRegistered(decoded, material, type, requested);
                            found = true;
                            break;
                        }
                    }
                    if (found) break;

                    for (ChemicalType type : ChemicalType.values()) {
                        String expectedName = String.format(type.getDefaultOverride().getNamingPattern(), material.getName());
                        if (requested.equals(expectedName)) {
                            decoded.objects.add(new ResolvedObject(material, type, requested));
                            found = true;
                            break;
                        }
                    }
                    if (found) break;
                }

                if (!found) {
                    OresCoreCommon.LOGGER.warn("[ORES CORE WARNING] Could not map request '{}' to any known Material + Type combination.", requested);
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

                for (String stone : data.getStonesReplacement()) {

                    decoded.ores.add(new ResolvedOre(material, stone));
                }
            } else {
                OresCoreCommon.LOGGER.warn("[ORES CORE WARNING] Unknown material '{}' requested in ore_generation.", oreMaterialName);
            }
        }

        return decoded;
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
        for (ResolvedObject obj : decoded.objects) {
            if (obj.registryName.equals(registryName)) return;
        }
        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Auto-activating object '{}' (type: {}) for material '{}'.", registryName, type, material.getName());
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
