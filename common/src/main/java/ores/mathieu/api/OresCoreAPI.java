//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Public API utility for external mods to interface with Ores Core.
// Provides methods to query dynamically generated materials, items, blocks, 
// and chemicals, evaluate creation candidates, examine active host blocks, 
// inject property overrides from code, and trigger runtime reloads for 
// registries and vein generation configurations.
//
// Author: __mathieu
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.api;

import ores.mathieu.config.ConfigManager;
import ores.mathieu.material.Material;
import ores.mathieu.material.MaterialsDatabase;
import ores.mathieu.material.ItemType;
import ores.mathieu.material.BlockType;
import ores.mathieu.material.ChemicalType;
import ores.mathieu.registry.DiscoveryManager;
import ores.mathieu.registry.RegistrationHandler;
import ores.mathieu.worldgen.VeinConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("null")
public class OresCoreAPI {

    private static String normalizeId(String id) {
        if (id != null && id.startsWith("ores:")) {
            return id.substring(5);
        }
        return id;
    }

    private static final Map<String, Map<String, String>> MATERIAL_PROPERTIES_CACHE = new HashMap<>();
    private static final Map<String, Map<String, String>> ITEM_PROPERTIES_CACHE = new HashMap<>();
    private static final Map<String, Map<String, String>> BLOCK_PROPERTIES_CACHE = new HashMap<>();
    private static final Map<String, Map<String, String>> CHEMICAL_PROPERTIES_CACHE = new HashMap<>();

    private static final Map<String, Map<String, String>> API_MATERIAL_OVERRIDES = new HashMap<>();
    private static final Map<String, Map<String, String>> API_ITEM_OVERRIDES = new HashMap<>();
    private static final Map<String, Map<String, String>> API_BLOCK_OVERRIDES = new HashMap<>();
    private static final Map<String, Map<String, String>> API_CHEMICAL_OVERRIDES = new HashMap<>();

    public static final Set<String> PROTECTED_MATERIAL_PROPS = Set.of("name", "debrisName");
    public static final Set<String> PROTECTED_TYPE_PROPS = Set.of("namingPattern", "tagCategory", "compatiblePatterns");

    /**
     * Gets the map of material property overrides injected via the API.
     * Call: OresCoreAPI.getApiMaterialOverrides()
     * @return A map where keys are material IDs and values are property-value maps.
     */
    public static Map<String, Map<String, String>> getApiMaterialOverrides() { return API_MATERIAL_OVERRIDES; }

    /**
     * Gets the map of item type property overrides injected via the API.
     * Call: OresCoreAPI.getApiItemOverrides()
     * @return A map where keys are item type names (e.g., "INGOT") and values are property-value maps.
     */
    public static Map<String, Map<String, String>> getApiItemOverrides() { return API_ITEM_OVERRIDES; }

    /**
     * Gets the map of block type property overrides injected via the API.
     * Call: OresCoreAPI.getApiBlockOverrides()
     * @return A map where keys are block type names (e.g., "STORAGE_BLOCK") and values are property-value maps.
     */
    public static Map<String, Map<String, String>> getApiBlockOverrides() { return API_BLOCK_OVERRIDES; }

    /**
     * Gets the map of chemical type property overrides injected via the API.
     * Call: OresCoreAPI.getApiChemicalOverrides()
     * @return A map where keys are chemical type names (e.g., "SLURRY") and values are property-value maps.
     */
    public static Map<String, Map<String, String>> getApiChemicalOverrides() { return API_CHEMICAL_OVERRIDES; }

    /**
     * Completely reloads the Ores Core registry. This clears all caches, rescans registered materials, 
     * reloads properties from the configuration file, and restarts the discovery process.
     * Call: OresCoreAPI.reloadRegistry()
     */
    public static void reloadRegistry() {
        DiscoveryManager.REGISTRY.getMaterialsRegistry().clear();
        DiscoveryManager.REGISTRY.getOreGeneration().clear();
        DiscoveryManager.REGISTRY.getStonesReplacement().clear();
        DiscoveryManager.REGISTRY.getBlacklist().clear();
        MATERIAL_PROPERTIES_CACHE.clear();
        ITEM_PROPERTIES_CACHE.clear();
        BLOCK_PROPERTIES_CACHE.clear();
        CHEMICAL_PROPERTIES_CACHE.clear();
        if (java.nio.file.Files.exists(ConfigManager.getPropertiesFilePath())) {
            ConfigManager.loadProperties();
        }
        DiscoveryManager.setScanningDone(false);
        DiscoveryManager.scanRegistries();
    }

    /**
     * Triggers the registration of all dynamic blocks, items, and chemicals, and generates 
     * the necessary client-side resources (models, states, textures).
     * Call: OresCoreAPI.registerResources()
     */
    public static void registerResources() {
        ores.mathieu.registry.RegistrationHandler.registerAll();
        ores.mathieu.resource.DynamicResourceGenerator.generateResources();
    }

    /**
     * Returns the set of IDs that are currently blacklisted from generation or registration.
     * Call: OresCoreAPI.getBlacklistedIds()
     * @return An unmodifiable set of blacklisted ID strings.
     */
    public static Set<String> getBlacklistedIds() {
        return Collections.unmodifiableSet(DiscoveryManager.REGISTRY.getBlacklist());
    }

    /**
     * Checks if a specific ID is in the Ores Core blacklist.
     * Call: OresCoreAPI.isBlacklisted(id)
     * @param id The ID to check (e.g., "ores:tin_ingot" or "tin_ingot").
     * @return True if the ID is blacklisted, false otherwise.
     */
    public static boolean isBlacklisted(String id) {
        return DiscoveryManager.REGISTRY.isBlacklisted(normalizeId(id));
    }

    /**
     * Reloads the ore vein generation configuration from the disk.
     * Call: OresCoreAPI.reloadVeinGeneration()
     */
    public static void reloadVeinGeneration() {
        ConfigManager.preInitialize();
        ConfigManager.initialize();
    }

    /**
     * Injects a property override for a specific entry in a category. This bypasses the config file 
     * but has lower priority than manual user edits in the TOML files.
     * Call: OresCoreAPI.injectPropertyOverride(category, id, property, value)
     * @param category The category: "materials", "items", "blocks", or "chemicals".
     * @param id The ID of the entry (e.g., "tin" for materials, "INGOT" for items).
     * @param property The name of the property to override (e.g., "miningLevel").
     * @param value The value to set as a string.
     */
    public static void injectPropertyOverride(String category, String id, String property, String value) {
        Map<String, Map<String, String>> target = null;
        switch (category.toLowerCase()) {
            case "materials": target = API_MATERIAL_OVERRIDES; break;
            case "items": target = API_ITEM_OVERRIDES; break;
            case "blocks": target = API_BLOCK_OVERRIDES; break;
            case "chemicals": target = API_CHEMICAL_OVERRIDES; break;
        }
        if (target != null) {
            String normId = normalizeId(id);
            Set<String> protectedKeys = category.equalsIgnoreCase("materials") ? PROTECTED_MATERIAL_PROPS : PROTECTED_TYPE_PROPS;
            if (protectedKeys.contains(property)) {
                ores.mathieu.OresCoreCommon.LOGGER.warn("[ORES CORE] Attempted to override protected property '{}' for {} {}. Ignoring.", property, category, id);
                return;
            }
            target.computeIfAbsent(normId, k -> new HashMap<>()).put(property, value);
            
            // Invalidate cache
            switch (category.toLowerCase()) {
                case "materials": MATERIAL_PROPERTIES_CACHE.remove(normId); break;
                case "items": ITEM_PROPERTIES_CACHE.remove(id.toUpperCase()); break;
                case "blocks": BLOCK_PROPERTIES_CACHE.remove(id.toUpperCase()); break;
                case "chemicals": CHEMICAL_PROPERTIES_CACHE.remove(id.toUpperCase()); break;
            }

            // Sync live objects
            ConfigManager.applyOverridesToObject(category, normId);
        }
    }

    /**
     * Checks if a given ID is a valid candidate for creation by Ores Core (matches internal patterns).
     * Call: OresCoreAPI.canCreateId(id)
     * @param id The ID to check.
     * @return True if Ores Core can potentially register this ID.
     */
    public static boolean canCreateId(String id) {
        return RegistrationHandler.isOresCoreObject(normalizeId(id));
    }

    /**
     * Returns a map of all possible IDs that Ores Core can generate based on registered materials and types.
     * Call: OresCoreAPI.getCreatableIds()
     * @return A map where keys are IDs and values are descriptions of the object type.
     */
    public static Map<String, String> getCreatableIds() {
        Map<String, String> creatableIds = new HashMap<>();
        for (Material material : MaterialsDatabase.MATERIALS.values()) {
            for (ItemType type : ItemType.values()) {
                String baseName = type.name().equalsIgnoreCase(material.getBaseType()) ? material.getBaseItemName() : material.getName();
                String canonicalId = String.format(type.getDefaultOverride().getNamingPattern(), baseName);
                creatableIds.put(canonicalId, "item");
                for (String compatPattern : type.getDefaultOverride().getCompatiblePatterns()) {
                    creatableIds.put(String.format(compatPattern, baseName), "item (alias: " + canonicalId + ")");
                }
            }
            for (BlockType type : BlockType.values()) {
                if (type == BlockType.ORE) continue;
                String canonicalId = String.format(type.getDefaultOverride().getNamingPattern(), material.getName());
                creatableIds.put(canonicalId, "block");
                for (String compatPattern : type.getDefaultOverride().getCompatiblePatterns()) {
                    creatableIds.put(String.format(compatPattern, material.getName()), "block (alias: " + canonicalId + ")");
                }
            }
            for (ChemicalType type : ChemicalType.values()) {
                String canonicalId = String.format(type.getDefaultOverride().getNamingPattern(), material.getName());
                creatableIds.put(canonicalId, "chemical");
                for (String compatPattern : type.getDefaultOverride().getCompatiblePatterns()) {
                    creatableIds.put(String.format(compatPattern, material.getName()), "chemical (alias: " + canonicalId + ")");
                }
            }
        }
        return creatableIds;
    }

    /**
     * Returns the set of IDs that are currently registered and active in the game by Ores Core.
     * Call: OresCoreAPI.getCurrentlyAddedIds()
     * @return A set of registered registry names.
     */
    public static Set<String> getCurrentlyAddedIds() {
        if (DiscoveryManager.DECODED_DATA == null) return Collections.emptySet();
        Set<String> currentlyAdded = new HashSet<>();
        DiscoveryManager.DECODED_DATA.objects.forEach(obj -> currentlyAdded.add(obj.registryName));
        DiscoveryManager.DECODED_DATA.ores.forEach(ore -> currentlyAdded.add(ore.material.getName() + "_ore"));
        return currentlyAdded;
    }

    /**
     * Checks if a specific ID is currently registered and active.
     * Call: OresCoreAPI.isIdAdded(id)
     * @param id The registry name to check.
     * @return True if the object is active.
     */
    public static boolean isIdAdded(String id) {
        String normId = normalizeId(id);
        return getCurrentlyAddedIds().contains(normId);
    }

    /**
     * Gets the set of block IDs that are currently acting as host blocks for ore generation.
     * Call: OresCoreAPI.getCurrentlyAddedHostBlocks()
     * @return A set of block ID strings.
     */
    public static Set<String> getCurrentlyAddedHostBlocks() {
        return Collections.unmodifiableSet(DiscoveryManager.REGISTRY.getStonesReplacement().keySet());
    }

    /**
     * Gets host blocks provided directly by other mods via registry.json.
     * Call: OresCoreAPI.getModProvidedHostBlocks()
     * @return A set of block IDs.
     */
    public static Set<String> getModProvidedHostBlocks() {
        Set<String> blocks = new HashSet<>();
        DiscoveryManager.REGISTRY.getStonesReplacement().forEach((id, isConfig) -> {
            if (!isConfig) blocks.add(id);
        });
        return blocks;
    }

    /**
     * Gets host blocks added by the user via the in-game configuration.
     * Call: OresCoreAPI.getConfigHostBlocks()
     * @return A set of block IDs.
     */
    public static Set<String> getConfigHostBlocks() {
        Set<String> blocks = new HashSet<>();
        DiscoveryManager.REGISTRY.getStonesReplacement().forEach((id, isConfig) -> {
            if (isConfig) blocks.add(id);
        });
        return blocks;
    }

    /**
     * Checks if a specific block ID is registered as a host block.
     * Call: OresCoreAPI.isHostBlock(blockId)
     * @param blockId The block ID to check.
     * @return True if it is a host block.
     */
    public static boolean isHostBlock(String blockId) {
        return DiscoveryManager.REGISTRY.getStonesReplacement().containsKey(blockId);
    }

    /**
     * Validates if a block ID refers to a block that can be used as a host for ore veins.
     * Call: OresCoreAPI.isValidHostBlockCandidate(blockId)
     * @param blockId The block ID string.
     * @return True if the block is a valid candidate.
     */
    public static boolean isValidHostBlockCandidate(String blockId) {
        if (blockId == null || blockId.isEmpty()) return false;
        net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.tryParse(blockId);
        if (id == null) return false;
        
        net.minecraft.world.level.block.Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(id).map(net.minecraft.core.Holder::value).orElse(net.minecraft.world.level.block.Blocks.AIR);
        if (block == null || block == net.minecraft.world.level.block.Blocks.AIR) return false;
        
        return isValidHostBlockCandidate(block);
    }

    /**
     * Validates if a Block instance is a compatible host block candidate.
     * Call: OresCoreAPI.isValidHostBlockCandidate(block)
     * @param block The Block instance.
     * @return True if compatible.
     */
    public static boolean isValidHostBlockCandidate(net.minecraft.world.level.block.Block block) {
        if (block == null) return false;
        String idStr = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString();
        return RegistrationHandler.isCompatibleHostBlock(block, idStr);
    }

    /**
     * Gets the names of materials that currently have ores registered for them.
     * Call: OresCoreAPI.getCurrentlyAddedOreMaterials()
     * @return A set of material names.
     */
    public static Set<String> getCurrentlyAddedOreMaterials() {
        if (DiscoveryManager.DECODED_DATA == null) return Collections.emptySet();
        Set<String> oreMaterials = new HashSet<>();
        DiscoveryManager.DECODED_DATA.ores.forEach(ore -> oreMaterials.add(ore.material.getName()));
        return oreMaterials;
    }

    /**
     * Checks if a specific material has an ore registered for it.
     * Call: OresCoreAPI.isOreMaterialAdded(materialName)
     * @param materialName The name of the material.
     * @return True if it has an ore.
     */
    public static boolean isOreMaterialAdded(String materialName) {
        if (DiscoveryManager.DECODED_DATA == null) return false;
        String norm = normalizeId(materialName);
        for (var ore : DiscoveryManager.DECODED_DATA.ores) {
            if (ore.material.getName().equals(norm)) return true;
        }
        return false;
    }

    /**
     * Alias for getMaterialProperty. Retrieves a specific property for a material.
     * Call: OresCoreAPI.getProperty(id, property)
     * @param id The material ID.
     * @param property The property name.
     * @return The property value as a string, or null if not found.
     */
    public static String getProperty(String id, String property) {
        return getMaterialProperty(id, property);
    }

    /**
     * Alias for getAllMaterialProperties. Retrieves all properties for a material.
     * Call: OresCoreAPI.getAllProperties(id)
     * @param id The material ID.
     * @return A map of all properties for that material.
     */
    public static Map<String, String> getAllProperties(String id) {
        return getAllMaterialProperties(id);
    }

    private static void mapPut(Map<String, String> map, String key, String value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    /**
     * Retrieves a specific property for a material.
     * Call: OresCoreAPI.getMaterialProperty(materialName, property)
     * @param materialName The name of the material.
     * @param property The property name (e.g., "baseColorHigh").
     * @return The value as a string.
     */
    public static String getMaterialProperty(String materialName, String property) {
        return getAllMaterialProperties(materialName).get(property);
    }

    /**
     * Retrieves all properties for a material, including hardcoded defaults and all overrides.
     * Call: OresCoreAPI.getAllMaterialProperties(materialName)
     * @param materialName The name of the material.
     * @return A map containing all material properties.
     */
    public static Map<String, String> getAllMaterialProperties(String materialName) {
        String norm = normalizeId(materialName);
        if (MATERIAL_PROPERTIES_CACHE.containsKey(norm)) return MATERIAL_PROPERTIES_CACHE.get(norm);
        
        Material mat = MaterialsDatabase.get(norm);
        if (mat == null) return Collections.emptyMap();
        
        Map<String, String> props = new HashMap<>();
        // Hardcoded
        fillMaterialProperties(mat, props);
        
        // Overrides
        applyOverrides(norm, props, ConfigManager.getOverrideMaterials(), API_MATERIAL_OVERRIDES, PROTECTED_MATERIAL_PROPS);
        
        Map<String, String> unmodifiable = Collections.unmodifiableMap(props);
        MATERIAL_PROPERTIES_CACHE.put(norm, unmodifiable);
        return unmodifiable;
    }

    private static void fillMaterialProperties(Material mat, Map<String, String> props) {
        mapPut(props, "name", mat.getName());
        mapPut(props, "nameEN", mat.getNameEN());
        mapPut(props, "nameFR", mat.getNameFR());
        mapPut(props, "nameES", mat.getNameES());
        mapPut(props, "nameIT", mat.getNameIT());
        mapPut(props, "nameDE", mat.getNameDE());
        mapPut(props, "namePT", mat.getNamePT());
        mapPut(props, "nameRU", mat.getNameRU());
        mapPut(props, "nameZH", mat.getNameZH());
        mapPut(props, "nameJP", mat.getNameJP());
        mapPut(props, "baseItemName", mat.getBaseItemName());
        if (mat.getBaseColorHigh() != null) mapPut(props, "baseColorHigh", String.valueOf(mat.getBaseColorHigh()));
        if (mat.getBaseColorLow() != null) mapPut(props, "baseColorLow", String.valueOf(mat.getBaseColorLow()));
        if (mat.getRawColorHigh() != null) mapPut(props, "rawColorHigh", String.valueOf(mat.getRawColorHigh()));
        if (mat.getRawColorLow() != null) mapPut(props, "rawColorLow", String.valueOf(mat.getRawColorLow()));
        mapPut(props, "baseType", mat.getBaseType());
        
        mapPut(props, "maxStackSize", String.valueOf(mat.getMaxStackSize()));
        if (mat.getRarity() != null) mapPut(props, "rarity", mat.getRarity().name());
        mapPut(props, "fireproof", String.valueOf(mat.isFireproof()));
        mapPut(props, "beacon", String.valueOf(mat.isBeacon()));
        mapPut(props, "piglinLoved", String.valueOf(mat.isPiglinLoved()));
        mapPut(props, "fuelTime", String.valueOf(mat.getFuelTime()));
        mapPut(props, "trimmable", String.valueOf(mat.isTrimmable()));
        
        mapPut(props, "blockHardnessFactor", String.valueOf(mat.getBlockHardnessFactor()));
        mapPut(props, "blockResistanceFactor", String.valueOf(mat.getBlockResistanceFactor()));
        mapPut(props, "blockRedstonePower", String.valueOf(mat.getBlockRedstonePower()));
        mapPut(props, "blockLightLevel", String.valueOf(mat.getBlockLightLevel()));
        mapPut(props, "blockLightMode", mat.getBlockLightMode());
        mapPut(props, "blockParticleMode", mat.getBlockParticleMode());
        mapPut(props, "blockParticleIntensity", String.valueOf(mat.getBlockParticleIntensity()));
        mapPut(props, "blockGravityMode", mat.getBlockGravityMode());
        mapPut(props, "blockColor", String.valueOf(mat.getBlockColor()));
        if (mat.getBlockPushReaction() != null) mapPut(props, "blockPushReaction", mat.getBlockPushReaction().name());
        if (mat.getInstrument() != null) mapPut(props, "instrument", mat.getInstrument().name());
        mapPut(props, "slipperiness", String.valueOf(mat.getSlipperiness()));
        mapPut(props, "speedFactor", String.valueOf(mat.getSpeedFactor()));
        mapPut(props, "jumpFactor", String.valueOf(mat.getJumpFactor()));
        mapPut(props, "ignitedByLava", String.valueOf(mat.isIgnitedByLava()));
        mapPut(props, "compressionRatio", String.valueOf(mat.getCompressionRatio()));
        
        mapPut(props, "oreHardnessFactor", String.valueOf(mat.getOreHardnessFactor()));
        mapPut(props, "oreResistanceFactor", String.valueOf(mat.getOreResistanceFactor()));
        mapPut(props, "miningLevel", String.valueOf(mat.getMiningLevel()));
        mapPut(props, "oreDropItem", mat.getOreDropItem());
        mapPut(props, "oreDropMin", String.valueOf(mat.getOreDropMin()));
        mapPut(props, "oreDropMax", String.valueOf(mat.getOreDropMax()));
        mapPut(props, "oreDropXPMin", String.valueOf(mat.getOreDropXPMin()));
        mapPut(props, "oreDropXPMax", String.valueOf(mat.getOreDropXPMax()));
        mapPut(props, "oreLightLevel", String.valueOf(mat.getOreLightLevel()));
        mapPut(props, "oreLightMode", mat.getOreLightMode());
        mapPut(props, "oreParticleMode", mat.getOreParticleMode());
        mapPut(props, "oreParticleIntensity", String.valueOf(mat.getOreParticleIntensity()));
        mapPut(props, "oreGravityMode", mat.getOreGravityMode());
        if (mat.getOrePushReaction() != null) mapPut(props, "orePushReaction", mat.getOrePushReaction().name());
        mapPut(props, "oreSmeltingTime", String.valueOf(mat.getOreSmeltingTime()));
        mapPut(props, "oreSmeltingXP", String.valueOf(mat.getOreSmeltingXP()));
        mapPut(props, "debrisName", mat.getDebrisName());
        
        mapPut(props, "chemicalCleanColor", String.valueOf(mat.getChemicalCleanColor()));
        mapPut(props, "chemicalDirtyColor", String.valueOf(mat.getChemicalDirtyColor()));
        mapPut(props, "chemicalSlurryColor", String.valueOf(mat.getChemicalSlurryColor()));
    }

    /**
     * Retrieves a specific property for an item type.
     * Call: OresCoreAPI.getItemProperty(itemType, property)
     * @param itemType The item type name (e.g., "INGOT").
     * @param property The property name.
     * @return The value as a string.
     */
    public static String getItemProperty(String itemType, String property) {
        return getAllItemProperties(itemType).get(property);
    }

    /**
     * Retrieves all properties for an item type, including overrides.
     * Call: OresCoreAPI.getAllItemProperties(itemType)
     * @param itemType The item type name.
     * @return A map of properties.
     */
    public static Map<String, String> getAllItemProperties(String itemType) {
        String key = itemType.toUpperCase();
        if (ITEM_PROPERTIES_CACHE.containsKey(key)) return ITEM_PROPERTIES_CACHE.get(key);
        try {
            ItemType type = ItemType.valueOf(key);
            Map<String, String> props = new HashMap<>();
            ores.mathieu.material.ItemOverride override = type.getDefaultOverride();
            
            fillItemProperties(override, props);
            applyOverrides(key, props, ConfigManager.getOverrideItems(), API_ITEM_OVERRIDES, PROTECTED_TYPE_PROPS);
            
            Map<String, String> unmodifiable = Collections.unmodifiableMap(props);
            ITEM_PROPERTIES_CACHE.put(key, unmodifiable);
            return unmodifiable;
        } catch (IllegalArgumentException e) {
            return Collections.emptyMap();
        }
    }

    private static void fillItemProperties(ores.mathieu.material.ItemOverride override, Map<String, String> props) {
        mapPut(props, "namingPattern", override.getNamingPattern());
        mapPut(props, "tagCategory", override.getTagCategory());
        mapPut(props, "compatiblePatterns", String.join(",", override.getCompatiblePatterns()));
            if (override.getRarity() != null) mapPut(props, "rarity", override.getRarity().name());
            if (override.getMaxStackSize() != null) mapPut(props, "maxStackSize", String.valueOf(override.getMaxStackSize()));
            if (override.getSmeltingMultiplier() != null) mapPut(props, "smeltingMultiplier", String.valueOf(override.getSmeltingMultiplier()));
            if (override.getXpMultiplier() != null) mapPut(props, "xpMultiplier", String.valueOf(override.getXpMultiplier()));
            if (override.getCanBeFireproof() != null) mapPut(props, "canBeFireproof", String.valueOf(override.getCanBeFireproof()));
            if (override.getCanBeBeaconPayment() != null) mapPut(props, "canBeBeaconPayment", String.valueOf(override.getCanBeBeaconPayment()));
            if (override.getCanBePiglinLoved() != null) mapPut(props, "canBePiglinLoved", String.valueOf(override.getCanBePiglinLoved()));
            if (override.getCanBeTrimmable() != null) mapPut(props, "canBeTrimmable", String.valueOf(override.getCanBeTrimmable()));
            if (override.getUseRawColor() != null) mapPut(props, "useRawColor", String.valueOf(override.getUseRawColor()));
            if (override.getFuelFactor() != null) mapPut(props, "fuelFactor", String.valueOf(override.getFuelFactor()));
            mapPut(props, "nameEN", override.getNameEN());
            mapPut(props, "nameFR", override.getNameFR());
            mapPut(props, "nameES", override.getNameES());
            mapPut(props, "nameIT", override.getNameIT());
            mapPut(props, "nameDE", override.getNameDE());
            mapPut(props, "namePT", override.getNamePT());
            mapPut(props, "nameRU", override.getNameRU());
            mapPut(props, "nameZH", override.getNameZH());
            mapPut(props, "nameJP", override.getNameJP());
    }

    /**
     * Retrieves a specific property for a block type.
     * Call: OresCoreAPI.getBlockProperty(blockType, property)
     * @param blockType The block type name (e.g., "STORAGE_BLOCK").
     * @param property The property name.
     * @return The value as a string.
     */
    public static String getBlockProperty(String blockType, String property) {
        return getAllBlockProperties(blockType).get(property);
    }

    /**
     * Retrieves all properties for a block type, including overrides.
     * Call: OresCoreAPI.getAllBlockProperties(blockType)
     * @param blockType The block type name.
     * @return A map of properties.
     */
    public static Map<String, String> getAllBlockProperties(String blockType) {
        String key = blockType.toUpperCase();
        if (BLOCK_PROPERTIES_CACHE.containsKey(key)) return BLOCK_PROPERTIES_CACHE.get(key);
        try {
            BlockType type = BlockType.valueOf(key);
            Map<String, String> props = new HashMap<>();
            ores.mathieu.material.BlockOverride override = type.getDefaultOverride();
            
            fillBlockProperties(override, props);
            applyOverrides(key, props, ConfigManager.getOverrideBlocks(), API_BLOCK_OVERRIDES, PROTECTED_TYPE_PROPS);
            
            Map<String, String> unmodifiable = Collections.unmodifiableMap(props);
            BLOCK_PROPERTIES_CACHE.put(key, unmodifiable);
            return unmodifiable;
        } catch (IllegalArgumentException e) {
            return Collections.emptyMap();
        }
    }

    private static void fillBlockProperties(ores.mathieu.material.BlockOverride override, Map<String, String> props) {
        mapPut(props, "namingPattern", override.getNamingPattern());
        mapPut(props, "tagCategory", override.getTagCategory());
        mapPut(props, "compatiblePatterns", String.join(",", override.getCompatiblePatterns()));
            if (override.getRarity() != null) mapPut(props, "rarity", override.getRarity().name());
            if (override.getMaxStackSize() != null) mapPut(props, "maxStackSize", String.valueOf(override.getMaxStackSize()));
            if (override.getSmeltingMultiplier() != null) mapPut(props, "smeltingMultiplier", String.valueOf(override.getSmeltingMultiplier()));
            if (override.getXpMultiplier() != null) mapPut(props, "xpMultiplier", String.valueOf(override.getXpMultiplier()));
            if (override.getCanBeFireproof() != null) mapPut(props, "canBeFireproof", String.valueOf(override.getCanBeFireproof()));
            if (override.getCanBeBeaconPayment() != null) mapPut(props, "canBeBeaconPayment", String.valueOf(override.getCanBeBeaconPayment()));
            if (override.getCanBePiglinLoved() != null) mapPut(props, "canBePiglinLoved", String.valueOf(override.getCanBePiglinLoved()));
            if (override.getCanBeTrimmable() != null) mapPut(props, "canBeTrimmable", String.valueOf(override.getCanBeTrimmable()));
            if (override.getUseRawColor() != null) mapPut(props, "useRawColor", String.valueOf(override.getUseRawColor()));
            if (override.getFuelFactor() != null) mapPut(props, "fuelFactor", String.valueOf(override.getFuelFactor()));
            mapPut(props, "nameEN", override.getNameEN());
            mapPut(props, "nameFR", override.getNameFR());
            mapPut(props, "nameES", override.getNameES());
            mapPut(props, "nameIT", override.getNameIT());
            mapPut(props, "nameDE", override.getNameDE());
            mapPut(props, "namePT", override.getNamePT());
            mapPut(props, "nameRU", override.getNameRU());
            mapPut(props, "nameZH", override.getNameZH());
            mapPut(props, "nameJP", override.getNameJP());

            if (override.getColor() != null) mapPut(props, "color", String.valueOf(override.getColor()));
            if (override.getHardness() != null) mapPut(props, "hardness", String.valueOf(override.getHardness()));
            if (override.getResistance() != null) mapPut(props, "resistance", String.valueOf(override.getResistance()));
            mapPut(props, "requiredTool", override.getRequiredTool());
            if (override.getLightLevel() != null) mapPut(props, "lightLevel", String.valueOf(override.getLightLevel()));
            mapPut(props, "lightMode", override.getLightMode());
            if (override.getUseMaterialLight() != null) mapPut(props, "useMaterialLight", String.valueOf(override.getUseMaterialLight()));
            mapPut(props, "particleMode", override.getParticleMode());
            if (override.getUseMaterialParticles() != null) mapPut(props, "useMaterialParticles", String.valueOf(override.getUseMaterialParticles()));
            if (override.getParticleIntensity() != null) mapPut(props, "particleIntensity", String.valueOf(override.getParticleIntensity()));
            if (override.getUseMaterialParticleIntensity() != null) mapPut(props, "useMaterialParticleIntensity", String.valueOf(override.getUseMaterialParticleIntensity()));
            if (override.getTranslucent() != null) mapPut(props, "translucent", String.valueOf(override.getTranslucent()));
            if (override.getPushReaction() != null) mapPut(props, "pushReaction", override.getPushReaction().name());
            if (override.getRedstoneFactor() != null) mapPut(props, "redstoneFactor", String.valueOf(override.getRedstoneFactor()));
            if (override.getRedstonePower() != null) mapPut(props, "redstonePower", String.valueOf(override.getRedstonePower()));
            if (override.getUseMaterialRedstone() != null) mapPut(props, "useMaterialRedstone", String.valueOf(override.getUseMaterialRedstone()));
            mapPut(props, "gravityMode", override.getGravityMode());
            if (override.getSlipperiness() != null) mapPut(props, "slipperiness", String.valueOf(override.getSlipperiness()));
            if (override.getUseMaterialSlipperiness() != null) mapPut(props, "useMaterialSlipperiness", String.valueOf(override.getUseMaterialSlipperiness()));
            if (override.getSpeedFactor() != null) mapPut(props, "speedFactor", String.valueOf(override.getSpeedFactor()));
            if (override.getUseMaterialSpeedFactor() != null) mapPut(props, "useMaterialSpeedFactor", String.valueOf(override.getUseMaterialSpeedFactor()));
            if (override.getJumpFactor() != null) mapPut(props, "jumpFactor", String.valueOf(override.getJumpFactor()));
            if (override.getUseMaterialJumpFactor() != null) mapPut(props, "useMaterialJumpFactor", String.valueOf(override.getUseMaterialJumpFactor()));
            if (override.getIsFlammable() != null) mapPut(props, "isFlammable", String.valueOf(override.getIsFlammable()));
            if (override.getInstrument() != null) mapPut(props, "instrument", override.getInstrument().name());
            if (override.getIgnitedByLava() != null) mapPut(props, "ignitedByLava", String.valueOf(override.getIgnitedByLava()));
            if (override.getIsReplaceable() != null) mapPut(props, "isReplaceable", String.valueOf(override.getIsReplaceable()));
            if (override.getNoCollision() != null) mapPut(props, "noCollision", String.valueOf(override.getNoCollision()));
            if (override.getShouldDropWhenFallingHitTorch() != null) mapPut(props, "shouldDropWhenFallingHitTorch", String.valueOf(override.getShouldDropWhenFallingHitTorch()));
            if (override.getCanBeBeaconBase() != null) mapPut(props, "canBeBeaconBase", String.valueOf(override.getCanBeBeaconBase()));
            if (override.getCompressionLevel() != null) mapPut(props, "compressionLevel", String.valueOf(override.getCompressionLevel()));
    }

    /**
     * Retrieves a specific property for a chemical type.
     * Call: OresCoreAPI.getChemicalProperty(chemicalType, property)
     * @param chemicalType The chemical type name (e.g., "SLURRY").
     * @param property The property name.
     * @return The value as a string.
     */
    public static String getChemicalProperty(String chemicalType, String property) {
        return getAllChemicalProperties(chemicalType).get(property);
    }

    /**
     * Retrieves all properties for a chemical type, including overrides.
     * Call: OresCoreAPI.getAllChemicalProperties(chemicalType)
     * @param chemicalType The chemical type name.
     * @return A map of properties.
     */
    public static Map<String, String> getAllChemicalProperties(String chemicalType) {
        String key = chemicalType.toUpperCase();
        if (CHEMICAL_PROPERTIES_CACHE.containsKey(key)) return CHEMICAL_PROPERTIES_CACHE.get(key);
        try {
            ChemicalType type = ChemicalType.valueOf(key);
            Map<String, String> props = new HashMap<>();
            ores.mathieu.material.ChemicalOverride override = type.getDefaultOverride();
            
            fillChemicalProperties(override, props);
            applyOverrides(key, props, ConfigManager.getOverrideChemicals(), API_CHEMICAL_OVERRIDES, PROTECTED_TYPE_PROPS);
            
            Map<String, String> unmodifiable = Collections.unmodifiableMap(props);
            CHEMICAL_PROPERTIES_CACHE.put(key, unmodifiable);
            return unmodifiable;
        } catch (IllegalArgumentException e) {
            return Collections.emptyMap();
        }
    }

    private static void fillChemicalProperties(ores.mathieu.material.ChemicalOverride override, Map<String, String> props) {
        mapPut(props, "namingPattern", override.getNamingPattern());
        mapPut(props, "tagCategory", override.getTagCategory());
        mapPut(props, "compatiblePatterns", String.join(",", override.getCompatiblePatterns()));
            if (override.getChemicalCleanColor() != null) mapPut(props, "chemicalCleanColor", String.valueOf(override.getChemicalCleanColor()));
            if (override.getChemicalDirtyColor() != null) mapPut(props, "chemicalDirtyColor", String.valueOf(override.getChemicalDirtyColor()));
            if (override.getChemicalSlurryColor() != null) mapPut(props, "chemicalSlurryColor", String.valueOf(override.getChemicalSlurryColor()));
            mapPut(props, "nameEN", override.getNameEN());
            mapPut(props, "nameFR", override.getNameFR());
            mapPut(props, "nameES", override.getNameES());
            mapPut(props, "nameIT", override.getNameIT());
            mapPut(props, "nameDE", override.getNameDE());
            mapPut(props, "namePT", override.getNamePT());
            mapPut(props, "nameRU", override.getNameRU());
            mapPut(props, "nameZH", override.getNameZH());
            mapPut(props, "nameJP", override.getNameJP());
    }

    /**
     * Returns the set of tags associated with a specific object ID.
     * Call: OresCoreAPI.getTags(id)
     * @param id The registry name of the object.
     * @return A set of tags (e.g., "c:ingots", "c:iron_ingots").
     */
    public static Set<String> getTags(String id) {
        return RegistrationHandler.resolveTags(normalizeId(id));
    }

    /**
     * Heuristically determines the most descriptive tag for an object (usually the one with a slash).
     * Call: OresCoreAPI.getUniqueTag(id)
     * @param id The registry name.
     * @return The best matching tag string.
     */
    public static String getUniqueTag(String id) {
        Set<String> tags = getTags(id);
        if (tags.isEmpty()) return "";
        String best = "";
        for (String t : tags) {
            if (t.contains("/")) {
                best = t;
                break;
            }
            best = t;
        }
        return best;
    }


    /**
     * Checks if a specific material is currently configured for world generation.
     * Call: OresCoreAPI.isMaterialGenerated(material)
     * @param material The material name.
     * @return True if it exists in any vein configuration.
     */
    public static boolean isMaterialGenerated(String material) {
        for (VeinConfig vein : ConfigManager.LOADED_VEINS) {
            if (vein.materials != null && vein.materials.contains(material)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a list of all vein configurations that contain the specified material.
     * Call: OresCoreAPI.getVeinGenerationProperties(material)
     * @param material The material name.
     * @return A list of VeinConfig objects.
     */
    public static List<VeinConfig> getVeinGenerationProperties(String material) {
        List<VeinConfig> configs = new ArrayList<>();
        for (VeinConfig vein : ConfigManager.LOADED_VEINS) {
            if (vein.materials != null && vein.materials.contains(material)) {
                configs.add(vein);
            }
        }
        return configs;
    }

    private static void applyOverrides(String id, Map<String, String> props, Map<String, Map<String, String>> configOverrides, Map<String, Map<String, String>> apiOverrides, Set<String> protectedKeys) {
        // API Overrides first (lower priority than config file)
        Map<String, String> api = apiOverrides.get(id);
        if (api != null) {
            for (Map.Entry<String, String> entry : api.entrySet()) {
                if (!protectedKeys.contains(entry.getKey())) props.put(entry.getKey(), entry.getValue());
            }
        }
        
        // Config File Overrides (highest priority)
        Map<String, String> config = configOverrides.get(id);
        if (config != null) {
            for (Map.Entry<String, String> entry : config.entrySet()) {
                if (!protectedKeys.contains(entry.getKey())) props.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
