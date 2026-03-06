//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Universal framework wrapper finalizing the instantiation 
// of runtime DynamicBlocks, DynamicItems, and Chemicals, then mapping 
// them officially into native modloader registries.
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

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import ores.mathieu.OresCoreCommon;
import ores.mathieu.platform.Services;
import ores.mathieu.worldgen.DynamicOreFeature;
import ores.mathieu.material.BlockType;
import ores.mathieu.material.ItemType;
import ores.mathieu.material.BlockOverride;
import ores.mathieu.material.ItemOverride;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("null")
public class RegistrationHandler {

    private static final Map<Item, Integer> FUELS = new HashMap<>();
    public static final Set<Identifier> QUEUED_BLOCKS = new HashSet<>();
    public static final Set<Identifier> QUEUED_ITEMS = new HashSet<>();
    private static final Set<String> HOST_BLACKLIST = Set.of(
        "respawn_anchor", "observer", "mushroom_stem", "piston", "sticky_piston",
        "glazed_terracotta", "dried_kelp_block", "mushroom_block", "grass_block", "podzol",
        "loom", "bulb"
    );

    public static boolean isCompatibleHostBlock(Block block, String id) {
        if (block == null || block == net.minecraft.world.level.block.Blocks.AIR) return false;

        boolean isFullCube = block.defaultBlockState().canOcclude() && block.defaultBlockState().isCollisionShapeFullBlock(net.minecraft.world.level.EmptyBlockGetter.INSTANCE, net.minecraft.core.BlockPos.ZERO);
        if (!isFullCube) return false;

        if (block instanceof net.minecraft.world.level.block.EntityBlock) return false;

        for (String blacklisted : HOST_BLACKLIST) {
            if (id.contains(blacklisted)) return false;
        }

        if (id.contains("waxed_") || id.contains("infested_")) return false;

        if (isOresCoreObject(id)) return false;

        if (ores.mathieu.resource.TextureGenerator.isBiomeTinted(id.contains(":") ? id.split(":")[0] : "minecraft", id.contains(":") ? id.split(":")[1] : id)) {
            return false;
        }

        for (net.minecraft.world.level.block.state.properties.Property<?> prop : block.getStateDefinition().getProperties()) {
            String name = prop.getName();
            if (!name.equals("axis") && !name.equals("waterlogged") && !name.equals("snowy")) {
                return false;
            }
        }

        return true;
    }

    private static final Set<String> KNOWN_CORE_OBJECTS = new java.util.HashSet<>();
    private static final Map<String, Set<String>> PRECOMPUTED_TAGS = new java.util.HashMap<>();
    private static boolean cachesInitialized = false;

    private static void initializeCaches() {
        if (cachesInitialized) return;
        for (ores.mathieu.material.Material mat : ores.mathieu.material.MaterialsDatabase.MATERIALS.values()) {
            KNOWN_CORE_OBJECTS.add(mat.getName() + "_ore");
            
            for (ores.mathieu.material.BlockType type : ores.mathieu.material.BlockType.values()) {
                if (type == ores.mathieu.material.BlockType.ORE) continue;
                String name = String.format(type.getDefaultOverride().getNamingPattern(), mat.getName());
                KNOWN_CORE_OBJECTS.add(name);
                Set<String> tags = new java.util.HashSet<>();
                populateObjectTags(tags, mat, type.getDefaultOverride());
                if (Boolean.TRUE.equals(type.getDefaultOverride().getCanBeBeaconBase())) tags.add("minecraft:beacon_base_blocks");
                PRECOMPUTED_TAGS.put(name, tags);
                
                if (type.getDefaultOverride().getCompatiblePatterns() != null) {
                    for (String p : type.getDefaultOverride().getCompatiblePatterns()) KNOWN_CORE_OBJECTS.add(String.format(p, mat.getName()));
                }
            }
            
            for (ores.mathieu.material.ItemType type : ores.mathieu.material.ItemType.values()) {
                String baseName = type.name().equalsIgnoreCase(mat.getBaseType()) ? mat.getBaseItemName() : mat.getName();
                String name = String.format(type.getDefaultOverride().getNamingPattern(), baseName);
                KNOWN_CORE_OBJECTS.add(name);
                Set<String> tags = new java.util.HashSet<>();
                populateObjectTags(tags, mat, type.getDefaultOverride());
                PRECOMPUTED_TAGS.put(name, tags);
                
                if (type.getDefaultOverride().getCompatiblePatterns() != null) {
                    for (String p : type.getDefaultOverride().getCompatiblePatterns()) KNOWN_CORE_OBJECTS.add(String.format(p, baseName));
                }
            }
            
            for (ores.mathieu.material.ChemicalType type : ores.mathieu.material.ChemicalType.values()) {
                String name = String.format(type.getDefaultOverride().getNamingPattern(), mat.getName());
                KNOWN_CORE_OBJECTS.add(name);
                Set<String> tags = new java.util.HashSet<>();
                populateObjectTags(tags, mat, type.getDefaultOverride());
                PRECOMPUTED_TAGS.put(name, tags);
            }
        }
        cachesInitialized = true;
    }

    public static boolean isOresCoreObject(String id) {
        if (id == null) return false;
        if (id.startsWith("ores:")) return true;
        
        String normalized = id.contains(":") ? id.split(":")[1] : id;

        initializeCaches();

        if (KNOWN_CORE_OBJECTS.contains(normalized)) return true;

        if (normalized.endsWith("_ore")) {
            for (String matName : ores.mathieu.material.MaterialsDatabase.MATERIALS.keySet()) {
                if (normalized.endsWith("_" + matName + "_ore")) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public static Set<String> resolveTags(String id) {
        if (id == null) return java.util.Collections.emptySet();
        String normalized = id.contains(":") ? id.split(":")[1] : id;
        
        initializeCaches();
        if (PRECOMPUTED_TAGS.containsKey(normalized)) {
            return new java.util.HashSet<>(PRECOMPUTED_TAGS.get(normalized));
        }

        Set<String> tags = new java.util.HashSet<>();

        if (normalized.endsWith("_ore")) {
            for (String matName : ores.mathieu.material.MaterialsDatabase.MATERIALS.keySet()) {
                if (normalized.endsWith("_" + matName + "_ore") || normalized.equals(matName + "_ore")) {
                    tags.add("c:ores");
                    tags.add("c:ores/" + matName);
                    return tags;
                }
            }
        }

        return tags;
    }

    private static void populateObjectTags(Set<String> tags, ores.mathieu.material.Material material, ores.mathieu.material.ItemOverride override) {
        String tagGroup = override.getTagCategory();
        String tagSuffix = override.getTagPattern().replace("%s", material.getName());
        tags.add("c:" + tagGroup);
        tags.add("c:" + tagGroup + "/" + tagSuffix);
        
        if (material.isBeacon() && Boolean.TRUE.equals(override.getCanBeBeaconPayment())) {
            tags.add("minecraft:beacon_payment_items");
        }
        if (material.isTrimmable() && Boolean.TRUE.equals(override.getCanBeTrimmable())) {
            tags.add("c:trim_materials/" + material.getName());
            tags.add("minecraft:trim_materials");
        }
    }

    private static void populateObjectTags(Set<String> tags, ores.mathieu.material.Material material, ores.mathieu.material.BlockOverride override) {
        String tagGroup = override.getTagCategory();
        String tagSuffix = override.getTagPattern().replace("%s", material.getName());
        tags.add("c:" + tagGroup);
        tags.add("c:" + tagGroup + "/" + tagSuffix);
    }

    private static void populateObjectTags(Set<String> tags, ores.mathieu.material.Material material, ores.mathieu.material.ChemicalOverride override) {
        String tagGroup = override.getTagCategory();
        String tagSuffix = override.getTagPattern().replace("%s", material.getName());
        tags.add("c:" + tagGroup);
        tags.add("c:" + tagGroup + "/" + tagSuffix);
    }

    public static void registerAll() {
        QUEUED_BLOCKS.clear();
        QUEUED_ITEMS.clear();

        if (DiscoveryManager.DECODED_DATA == null) {
            OresCoreCommon.LOGGER.error("[ORES CORE] CRITICAL: Decoded Data is null! DiscoveryManager.scanRegistries() must be called first.");
            return;
        }

        int objectsCount = 0;
        int oresCount = 0;
        int chemicalsCount = 0;

        for (RegistryDecoder.ResolvedObject obj : DiscoveryManager.DECODED_DATA.objects) {
            try {
                if (BuiltInRegistries.ITEM.containsKey(Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, obj.registryName)) || 
                    BuiltInRegistries.BLOCK.containsKey(Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, obj.registryName))) {
                    continue;
                }
                processDirectObject(obj);
                if (!VANILLA_ITEMS.contains(obj.registryName)) {
                    if (obj.type instanceof ores.mathieu.material.ChemicalType) {
                        chemicalsCount++;
                    } else {
                        objectsCount++;
                    }
                }
            } catch (Exception e) {
                OresCoreCommon.LOGGER.error("[ORES CORE] Failed to register object: {}", obj.registryName, e);
            }
        }

        for (RegistryDecoder.ResolvedOre ore : DiscoveryManager.DECODED_DATA.ores) {
            try {
                String oreName = ore.stoneReplacement.contains(":") ? ore.stoneReplacement.split(":")[1] : ore.stoneReplacement;
                oreName = oreName + "_" + ore.material.getName() + "_ore";
                if (BuiltInRegistries.BLOCK.containsKey(Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, oreName))) {
                    continue;
                }
                processCombinatorialOre(ore);
                if (!ore.material.getName().equals("netherite")) {
                    oresCount++;
                }
            } catch (Exception e) {
                OresCoreCommon.LOGGER.error("[ORES CORE] Failed to register combinatorial ore: {}/{}", ore.stoneReplacement, ore.material.getName(), e);
            }
        }
        OresCoreCommon.LOGGER.info("[ORES CORE] {} items/blocks/slurries added", objectsCount + chemicalsCount);
        OresCoreCommon.LOGGER.info("[ORES CORE] {} ores added", oresCount);

        Identifier dynamicOreId = Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, "dynamic_ore");
        if (!BuiltInRegistries.FEATURE.containsKey(dynamicOreId)) {
            Services.getRegistration().registerFeature(dynamicOreId, new DynamicOreFeature(ores.mathieu.worldgen.VeinFeatureConfiguration.CODEC));
        }

        Identifier giantVeinId = Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, "giant_vein");
        if (!BuiltInRegistries.CARVER.containsKey(giantVeinId)) {
            Services.getRegistration().registerCarver(giantVeinId, new ores.mathieu.worldgen.GiantVeinCarver(ores.mathieu.worldgen.VeinCarverConfiguration.CODEC));
        }

        for (int i = 0; i < ores.mathieu.config.ConfigManager.LOADED_VEINS.size(); i++) {
            ores.mathieu.worldgen.VeinConfig config = ores.mathieu.config.ConfigManager.LOADED_VEINS.get(i);
            Services.getRegistration().registerBiomeModification(i, config);
        }

        Services.getRegistration().registerFuels(FUELS);
        ores.mathieu.tabs.OresCreativeTabs.registerTabs();
    }

    public static final java.util.Set<String> VANILLA_ITEMS = java.util.Set.of(
        "coal", "coal_block",
        "copper_nugget", "copper_ingot", "raw_copper", "copper_block", "raw_copper_block",
        "iron_nugget", "iron_ingot", "raw_iron", "iron_block", "raw_iron_block",
        "gold_nugget", "gold_ingot", "raw_gold", "gold_block", "raw_gold_block",
        "diamond", "diamond_block",
        "emerald", "emerald_block",
        "lapis_lazuli", "lapis_block",
        "redstone", "redstone_block",
        "quartz", "quartz_block",
        "netherite_ingot", "netherite_block", "netherite_scrap", "ancient_debris"
    );

    public static final java.util.Set<String> VANILLA_TRIM_MATERIALS = java.util.Set.of(
        "iron", "gold", "copper", "diamond", "emerald", "lapis", "redstone", "quartz", "netherite", "amethyst", "resin"
    );

    private static void processDirectObject(RegistryDecoder.ResolvedObject obj) {
        if (VANILLA_ITEMS.contains(obj.registryName)) {
            Identifier vanillaId = getVanillaEquivalent(obj.registryName);
            Item vanillaItem = BuiltInRegistries.ITEM.getValue(vanillaId);
            if (vanillaItem != null && vanillaItem != net.minecraft.world.item.Items.AIR) {
                if (obj.type instanceof BlockType) {
                    ores.mathieu.tabs.OresCreativeTabs.addSafe(ores.mathieu.tabs.OresCreativeTabs.REGISTERED_BLOCKS, new ores.mathieu.tabs.OresCreativeTabs.TabEntry(vanillaItem, obj.material, obj.type));
                } else {
                    ores.mathieu.tabs.OresCreativeTabs.addSafe(ores.mathieu.tabs.OresCreativeTabs.REGISTERED_ITEMS, new ores.mathieu.tabs.OresCreativeTabs.TabEntry(vanillaItem, obj.material, obj.type));
                }
            }
            return;
        }

        Identifier id = Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, obj.registryName);
        if (obj.type instanceof BlockType blockType) {
            if (QUEUED_BLOCKS.contains(id)) return;
            QUEUED_BLOCKS.add(id);
            
            BlockOverride override = blockType.getDefaultOverride();

            Block.Properties blockProps = Block.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .sound(override.getSound() != null ? override.getSound() : obj.material.getSound())
                .strength(
                    (override.getHardness() != null ? override.getHardness() : obj.material.getBlockHardnessFactor()) * 1.5f,
                    (override.getResistance() != null ? override.getResistance() : obj.material.getBlockResistanceFactor()) * 6.0f
                );

            if ("pickaxe".equals(override.getRequiredTool())) blockProps.requiresCorrectToolForDrops();

            if (override.getCanBeBeaconBase() != null && override.getCanBeBeaconBase()) blockProps.mapColor(override.getMapColor() != null ? override.getMapColor() : obj.material.getMapColor());

            String gravity = resolveString(override.getGravityMode(), obj.material.getBlockGravityMode());
            boolean hasGravity = "true".equalsIgnoreCase(gravity) || "falling".equalsIgnoreCase(gravity);

            int lightLevel = override.getLightLevel() != null && override.getLightLevel() != -1 ? override.getLightLevel() : 0;
            String lightMode = override.getLightMode() != null ? override.getLightMode() : "NONE";

            if (override.getUseMaterialLight() != null && override.getUseMaterialLight()) {
                lightLevel = obj.material.getBlockLightLevel();
                lightMode = obj.material.getBlockLightMode();
            }

            boolean interactiveLight = "interaction".equalsIgnoreCase(lightMode) || "interactive".equalsIgnoreCase(lightMode) || "dynamic".equalsIgnoreCase(lightMode);
            boolean constantLight = "constant".equalsIgnoreCase(lightMode) || "always".equalsIgnoreCase(lightMode);

            final int finalLightLevel = lightLevel;
            if (constantLight && lightLevel > 0) {
                blockProps = blockProps.lightLevel(state -> finalLightLevel);
            } else if (interactiveLight && lightLevel > 0) {
                blockProps = blockProps.lightLevel(state -> state.hasProperty(InteractiveDynamicBlock.LIT) && state.getValue(InteractiveDynamicBlock.LIT) ? finalLightLevel : 0);
            }

            int redstonePower = override.getRedstonePower() != null && override.getRedstonePower() != -1 ? override.getRedstonePower() : 0;
            if (override.getUseMaterialRedstone() != null && override.getUseMaterialRedstone()) {
                redstonePower = obj.material.getBlockRedstonePower();
            }

            String particleMode = override.getParticleMode() != null ? override.getParticleMode() : "NONE";
            if (override.getUseMaterialParticles() != null && override.getUseMaterialParticles()) {
                particleMode = obj.material.getBlockParticleMode();
            }
            boolean interactiveParticles = "interaction".equalsIgnoreCase(particleMode) || "interactive".equalsIgnoreCase(particleMode) || "dynamic".equalsIgnoreCase(particleMode);
            boolean constantParticles = "constant".equalsIgnoreCase(particleMode) || "always".equalsIgnoreCase(particleMode);

            float particleIntensity = obj.material.getBlockParticleIntensity();
            if (override.getUseMaterialParticleIntensity() != null && override.getUseMaterialParticleIntensity()) {
                particleIntensity = obj.material.getBlockParticleIntensity();
            } else if (override.getParticleIntensity() != null) {
                particleIntensity = override.getParticleIntensity();
            }

            if (override.getUseMaterialSlipperiness() != null && override.getUseMaterialSlipperiness()) {
                blockProps.friction(obj.material.getSlipperiness());
            } else if (override.getSlipperiness() != null) {
                blockProps.friction(override.getSlipperiness());
            }

            if (override.getUseMaterialSpeedFactor() != null && override.getUseMaterialSpeedFactor()) {
                blockProps.speedFactor(obj.material.getSpeedFactor());
            } else if (override.getSpeedFactor() != null) {
                blockProps.speedFactor(override.getSpeedFactor());
            }

            if (override.getUseMaterialJumpFactor() != null && override.getUseMaterialJumpFactor()) {
                blockProps.jumpFactor(obj.material.getJumpFactor());
            } else if (override.getJumpFactor() != null) {
                blockProps.jumpFactor(override.getJumpFactor());
            }

            if (override.getTranslucent() != null && override.getTranslucent()) {
                blockProps.noOcclusion()
                    .isViewBlocking((state, world, pos) -> false)
                    .isSuffocating((state, world, pos) -> false);
            }
            if (override.getNoCollision() != null && override.getNoCollision()) blockProps.noCollision();
            if (override.getPushReaction() != null) blockProps.pushReaction(override.getPushReaction());
            if (resolveBoolean(override.getIgnitedByLava(), obj.material.isIgnitedByLava())) blockProps.ignitedByLava();
            if (override.getInstrument() != null) blockProps.instrument(override.getInstrument());

            Block block;
            if (interactiveLight || interactiveParticles) {
                block = new InteractiveDynamicBlock(blockProps, hasGravity, interactiveLight, interactiveParticles);
            } else {
                block = new DynamicBlock(blockProps, hasGravity);
            }

            if (block instanceof DynamicBlock dynamicBlock) {
                dynamicBlock.setRedstonePower(redstonePower);

                if (override.getShouldDropWhenFallingHitTorch() != null) {
                    dynamicBlock.setDropWhenHitTorch(override.getShouldDropWhenFallingHitTorch());
                }

                if (override.getTranslucent() != null && override.getTranslucent()) {
                    dynamicBlock.setTranslucent(override.getTranslucent());
                }

                if (constantParticles || interactiveParticles) {
                    int particleColor = (override.getUseRawColor() != null && override.getUseRawColor()) ? (obj.material.getRawColorHigh() != null ? obj.material.getRawColorHigh() : obj.material.getMaterialColor()) : obj.material.getMaterialColor();
                    dynamicBlock.setParticles(constantParticles, particleColor, particleIntensity);
                }
            }

            if (resolveBoolean(override.getIsFlammable(), false)) {
                Services.getRegistration().registerFlammable(block, 5, 20);
            }

            Services.getRegistration().registerBlock(id, block);

            Item.Properties itemProps = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id));

            if (resolveBoolean(override.getCanBeFireproof(), obj.material.isFireproof())) itemProps.fireResistant();

            if (override.getRarity() != null) {
                Rarity matRarity = obj.material.getRarity() != null ? obj.material.getRarity() : Rarity.COMMON;
                Rarity ovRarity = override.getRarity();
                Rarity highestRarity = matRarity.ordinal() > ovRarity.ordinal() ? matRarity : ovRarity;
                if (highestRarity != Rarity.COMMON) {
                    itemProps.rarity(highestRarity);
                }
            }

            int finalStackSize = Math.min(override.getMaxStackSize(), obj.material.getMaxStackSize());
            if (finalStackSize != 64) {
                itemProps.stacksTo(finalStackSize);
            }

            if (obj.material.isTrimmable() && Boolean.TRUE.equals(override.getCanBeTrimmable())) {
                String matName = obj.material.getName();
                boolean isVanillaTrim = VANILLA_TRIM_MATERIALS.contains(matName);
                String namespace = isVanillaTrim ? "minecraft" : OresCoreCommon.MOD_ID;

                itemProps.component(net.minecraft.core.component.DataComponents.PROVIDES_TRIM_MATERIAL,
                    net.minecraft.core.Holder.direct(new net.minecraft.world.item.equipment.trim.TrimMaterial(
                        new net.minecraft.world.item.equipment.trim.MaterialAssetGroup(
                            new net.minecraft.world.item.equipment.trim.MaterialAssetGroup.AssetInfo(matName),
                            java.util.Map.of()
                        ),
                        net.minecraft.network.chat.Component.translatable("trim_material." + namespace + "." + matName)
                    ))
                );
            }

            Item item = new net.minecraft.world.item.BlockItem(block, itemProps);
            Services.getRegistration().registerItem(id, item);
            QUEUED_ITEMS.add(id);

            if (blockType == BlockType.ORE) {
                ores.mathieu.tabs.OresCreativeTabs.addSafe(ores.mathieu.tabs.OresCreativeTabs.REGISTERED_ORES, new ores.mathieu.tabs.OresCreativeTabs.TabEntry(item, obj.material, blockType));
            } else {
                ores.mathieu.tabs.OresCreativeTabs.addSafe(ores.mathieu.tabs.OresCreativeTabs.REGISTERED_BLOCKS, new ores.mathieu.tabs.OresCreativeTabs.TabEntry(item, obj.material, blockType));
            }

            registerFuel(item, obj.material, blockType);
        } else if (obj.type instanceof ores.mathieu.material.ChemicalType) {
            if (QUEUED_ITEMS.contains(id)) return;
            QUEUED_ITEMS.add(id);
            processChemical(obj);
        } else if (obj.type instanceof ItemType itemType) {
            if (QUEUED_ITEMS.contains(id)) return;
            QUEUED_ITEMS.add(id);

            ItemOverride override = itemType.getDefaultOverride();
            Item.Properties itemProps = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id));

            if (resolveBoolean(override.getCanBeFireproof(), obj.material.isFireproof())) itemProps.fireResistant();

            if (override.getRarity() != null) {
                Rarity matRarity = obj.material.getRarity() != null ? obj.material.getRarity() : Rarity.COMMON;
                Rarity ovRarity = override.getRarity();
                Rarity highestRarity = matRarity.ordinal() > ovRarity.ordinal() ? matRarity : ovRarity;
                if (highestRarity != Rarity.COMMON) {
                    itemProps.rarity(highestRarity);
                }
            }

            int finalStackSize = Math.min(override.getMaxStackSize(), obj.material.getMaxStackSize());
            if (finalStackSize != 64) {
                itemProps.stacksTo(finalStackSize);
            }

            if (obj.material.isTrimmable() && Boolean.TRUE.equals(override.getCanBeTrimmable())) {
                String matName = obj.material.getName();
                boolean isVanillaTrim = VANILLA_TRIM_MATERIALS.contains(matName);
                String namespace = isVanillaTrim ? "minecraft" : OresCoreCommon.MOD_ID;

                itemProps.component(net.minecraft.core.component.DataComponents.PROVIDES_TRIM_MATERIAL,
                    net.minecraft.core.Holder.direct(new net.minecraft.world.item.equipment.trim.TrimMaterial(
                        new net.minecraft.world.item.equipment.trim.MaterialAssetGroup(
                            new net.minecraft.world.item.equipment.trim.MaterialAssetGroup.AssetInfo(matName),
                            java.util.Map.of()
                        ),
                        net.minecraft.network.chat.Component.translatable("trim_material." + namespace + "." + matName)
                    ))
                );
            }

            Item item = new DynamicItem(itemProps);
            Services.getRegistration().registerItem(id, item);
            ores.mathieu.tabs.OresCreativeTabs.addSafe(ores.mathieu.tabs.OresCreativeTabs.REGISTERED_ITEMS, new ores.mathieu.tabs.OresCreativeTabs.TabEntry(item, obj.material, obj.type));

            registerFuel(item, obj.material, itemType);
        }
    }

    private static void processChemical(RegistryDecoder.ResolvedObject obj) {
        String baseName = obj.registryName;
        Identifier id = Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, baseName);
        ores.mathieu.material.ChemicalType type = (ores.mathieu.material.ChemicalType) obj.type;
        ores.mathieu.material.Material mat = obj.material;

        Item.Properties itemProps = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id));
        Item chemicalItem = new ores.mathieu.material.ChemicalItem(itemProps, mat, type);
        Services.getRegistration().registerItem(id, chemicalItem);
        ores.mathieu.tabs.OresCreativeTabs.addSafe(ores.mathieu.tabs.OresCreativeTabs.REGISTERED_ITEMS, new ores.mathieu.tabs.OresCreativeTabs.TabEntry(chemicalItem, mat, type));

        OresCoreCommon.LOGGER.debug("[ORES CORE] Registered chemical item: {} (type: {})", baseName, type);

        if (ores.mathieu.compat.MekanismCompat.isMekanismLoaded()) {
            ores.mathieu.compat.MekanismCompat.registerChemical(mat.getName(), mat, type);
        }
    }

    private static void processCombinatorialOre(RegistryDecoder.ResolvedOre ore) {
        if (ore.material.getName().equals("netherite")) {
            Item vanillaDebris = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("minecraft", "ancient_debris"));
            if (vanillaDebris != null && vanillaDebris != net.minecraft.world.item.Items.AIR) {
                ores.mathieu.tabs.OresCreativeTabs.addSafe(ores.mathieu.tabs.OresCreativeTabs.REGISTERED_ORES, new ores.mathieu.tabs.OresCreativeTabs.TabEntry(vanillaDebris, ore.material, BlockType.ORE));
            }
            return;
        }

        String stoneName = ore.stoneReplacement;
        String stonePath = stoneName.contains(":") ? stoneName.split(":")[1] : stoneName;
        String stoneNamespace = stoneName.contains(":") ? stoneName.split(":")[0] : "minecraft";
        String oreName = stonePath + "_" + ore.material.getName() + "_ore";
        Identifier id = Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, oreName);

        if (QUEUED_BLOCKS.contains(id)) return;

        if (!ores.mathieu.resource.TextureGenerator.canResolveBaseTexture(stoneNamespace, stonePath)) {
            OresCoreCommon.LOGGER.warn("[ORES CORE] Skipping ore {} — no resolvable texture for base block {}.", oreName, stoneName);
            return;
        }

        Identifier stoneId = Identifier.parse(stoneName);
        Block baseBlock = BuiltInRegistries.BLOCK.getValue(stoneId);
        boolean exists = baseBlock != null && baseBlock != net.minecraft.world.level.block.Blocks.AIR;
        
        Block.Properties blockProps;
        boolean hasGravity = false;
        boolean needsDeferredPatch = false;
        float hardness;
        float resistance;

        if (exists) {
            if (!isCompatibleHostBlock(baseBlock, stoneName)) {
                return;
            }
            QUEUED_BLOCKS.add(id);
            hasGravity = baseBlock instanceof net.minecraft.world.level.block.Fallable;
            hardness = baseBlock.defaultDestroyTime() * ore.material.getOreHardnessFactor();
            resistance = baseBlock.getExplosionResistance() * ore.material.getOreResistanceFactor();

            blockProps = Block.Properties.ofFullCopy(baseBlock)
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .strength(hardness, resistance)
                .overrideLootTable(java.util.Optional.of(ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, id.withPrefix("blocks/"))))
                .requiresCorrectToolForDrops();
        } else {
            QUEUED_BLOCKS.add(id);
            OresCoreCommon.LOGGER.warn("[ORES CORE] Base block {} not yet registered for ore {}. Using stone fallback + deferred patch.", stoneName, oreName);
            Block stone = net.minecraft.world.level.block.Blocks.STONE;
            hardness = stone.defaultDestroyTime() * ore.material.getOreHardnessFactor();
            resistance = stone.getExplosionResistance() * ore.material.getOreResistanceFactor();
            blockProps = Block.Properties.ofFullCopy(stone)
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .strength(hardness, resistance)
                .overrideLootTable(java.util.Optional.of(ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, id.withPrefix("blocks/"))))
                .requiresCorrectToolForDrops();
            needsDeferredPatch = true;
        }

        boolean interactiveLight = "interaction".equalsIgnoreCase(ore.material.getOreLightMode()) || "interactive".equalsIgnoreCase(ore.material.getOreLightMode()) || "dynamic".equalsIgnoreCase(ore.material.getOreLightMode());
        boolean interactiveParticles = "interaction".equalsIgnoreCase(ore.material.getOreParticleMode()) || "interactive".equalsIgnoreCase(ore.material.getOreParticleMode()) || "dynamic".equalsIgnoreCase(ore.material.getOreParticleMode());
        boolean constantLight = "constant".equalsIgnoreCase(ore.material.getOreLightMode()) || "always".equalsIgnoreCase(ore.material.getOreLightMode());
        boolean constantParticles = "constant".equalsIgnoreCase(ore.material.getOreParticleMode()) || "always".equalsIgnoreCase(ore.material.getOreParticleMode());

        if (constantLight && ore.material.getOreLightLevel() > 0) {
            blockProps = blockProps.lightLevel(state -> ore.material.getOreLightLevel());
        } else if (interactiveLight && ore.material.getOreLightLevel() > 0) {
            blockProps = blockProps.lightLevel(state -> state.hasProperty(InteractiveDynamicBlock.LIT) && state.getValue(InteractiveDynamicBlock.LIT) ? ore.material.getOreLightLevel() : 0);
        } else {
            blockProps = blockProps.lightLevel(state -> 0);
        }

        Block block;
        if (baseBlock instanceof net.minecraft.world.level.block.RotatedPillarBlock) {
            if (interactiveLight || interactiveParticles) {
                block = new InteractivePillarBlock(blockProps, hasGravity, interactiveLight, interactiveParticles);
            } else {
                block = new DynamicPillarBlock(blockProps, hasGravity);
            }
        } else {
            if (interactiveLight || interactiveParticles) {
                block = new InteractiveDynamicBlock(blockProps, hasGravity, interactiveLight, interactiveParticles);
            } else {
                block = new DynamicBlock(blockProps, hasGravity);
            }
        }

        if (block instanceof DynamicBlock dynamicBlock) {
            dynamicBlock.setXpRange(ore.material.getOreDropXPMin(), ore.material.getOreDropXPMax());
            if (constantParticles || interactiveParticles) {
                int particleColor = ore.material.getRawColorHigh() != null ? ore.material.getRawColorHigh() : ore.material.getMaterialColor();
                dynamicBlock.setParticles(constantParticles, particleColor, ore.material.getOreParticleIntensity());
            }
            dynamicBlock.setDropWhenHitTorch(false);
        }

        if (needsDeferredPatch) {
            PendingPatchManager.enqueue(block, stoneName, ore.material.getOreHardnessFactor(), ore.material.getOreResistanceFactor());
        }

        Services.getRegistration().registerBlock(id, block);

        Item.Properties itemProps = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id));

        BlockOverride override = BlockType.ORE.getDefaultOverride();
        if (override.getRarity() != null) {
            Rarity matRarity = ore.material.getRarity() != null ? ore.material.getRarity() : Rarity.COMMON;
            Rarity ovRarity = override.getRarity();
            Rarity highestRarity = matRarity.ordinal() > ovRarity.ordinal() ? matRarity : ovRarity;
            if (highestRarity != Rarity.COMMON) {
                itemProps.rarity(highestRarity);
            }
        }

        Item item = new net.minecraft.world.item.BlockItem(block, itemProps);
        Services.getRegistration().registerItem(id, item);
        QUEUED_ITEMS.add(id);
        ores.mathieu.tabs.OresCreativeTabs.addSafe(ores.mathieu.tabs.OresCreativeTabs.REGISTERED_ORES, new ores.mathieu.tabs.OresCreativeTabs.TabEntry(item, ore.material, BlockType.ORE));

        registerFuel(item, ore.material, BlockType.ORE);
    }

    private static void registerFuel(Item item, ores.mathieu.material.Material material, Enum<?> type) {
        int baseFuelTime = material.getFuelTime();
        if (baseFuelTime <= 0) return;

        float fuelFactor = 0.0f;
        if (type instanceof ItemType it) {
            fuelFactor = it.getDefaultOverride().getFuelFactor() != null ? it.getDefaultOverride().getFuelFactor() : 0.0f;
        } else if (type instanceof BlockType bt) {
            fuelFactor = bt.getDefaultOverride().getFuelFactor() != null ? bt.getDefaultOverride().getFuelFactor() : 0.0f;
        }

        if (fuelFactor > 0) {
            int finalFuelTime = Math.round(baseFuelTime * fuelFactor);
            if (finalFuelTime > 0) {
                FUELS.put(item, finalFuelTime);
            }
        }
    }

    private static boolean resolveBoolean(Boolean overrideVal, boolean materialVal) {
        if (overrideVal == null) return false;
        if (overrideVal) return true;
        return materialVal;
    }

    private static String resolveString(String overrideVal, String materialVal) {
        if (overrideVal == null || "AUTO".equalsIgnoreCase(overrideVal)) return materialVal;
        if ("NONE".equalsIgnoreCase(overrideVal)) return "NONE";
        return overrideVal;
    }

    private static Identifier getVanillaEquivalent(String registryName) {
        String vanillaName = registryName;
        if (registryName.endsWith("_raw")) {
            vanillaName = "raw_" + registryName.replace("_raw", "");
        } else if (registryName.endsWith("_raw_block")) {
            vanillaName = "raw_" + registryName.replace("_raw_block", "") + "_block";
        }
        return Identifier.fromNamespaceAndPath("minecraft", vanillaName);
    }
}
