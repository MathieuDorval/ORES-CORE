//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Main orchestrator for registering blocks, items, and chemicals.
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
import java.util.Map;

@SuppressWarnings("null")
public class RegistrationHandler {

    private static final Map<Item, Integer> FUELS = new HashMap<>();

    public static void registerAll() {
        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Starting registration...");

        if (DiscoveryManager.DECODED_DATA == null) {
            OresCoreCommon.LOGGER.error("[ORES CORE] CRITICAL: Decoded Data is null! DiscoveryManager.scanRegistries() must be called first.");
            return;
        }

        int objectsCount = 0;
        int oresCount = 0;
        int chemicalsCount = 0;

        for (RegistryDecoder.ResolvedObject obj : DiscoveryManager.DECODED_DATA.objects) {
            try {
                processDirectObject(obj);
                if (obj.type instanceof ores.mathieu.material.ChemicalType) {
                    chemicalsCount++;
                } else {
                    objectsCount++;
                }
            } catch (Exception e) {
                OresCoreCommon.LOGGER.error("[ORES CORE] Failed to register object: {}", obj.registryName, e);
            }
        }

        for (RegistryDecoder.ResolvedOre ore : DiscoveryManager.DECODED_DATA.ores) {
            try {
                processCombinatorialOre(ore);
                oresCount++;
            } catch (Exception e) {
                OresCoreCommon.LOGGER.error("[ORES CORE] Failed to register combinatorial ore: {}/{}", ore.stoneReplacement, ore.material.getName(), e);
            }
        }

        OresCoreCommon.LOGGER.info("[ORES CORE] {} items/blocks/slurries added", objectsCount + chemicalsCount);
        OresCoreCommon.LOGGER.info("[ORES CORE] {} ores added", oresCount);

        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Registering features...");
        Services.getRegistration().registerFeature(Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, "dynamic_ore"), new DynamicOreFeature(ores.mathieu.worldgen.VeinFeatureConfiguration.CODEC));
        Services.getRegistration().registerCarver(Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, "giant_vein"), new ores.mathieu.worldgen.GiantVeinCarver(ores.mathieu.worldgen.VeinCarverConfiguration.CODEC));

        for (int i = 0; i < ores.mathieu.config.ConfigManager.LOADED_VEINS.size(); i++) {
            ores.mathieu.worldgen.VeinConfig config = ores.mathieu.config.ConfigManager.LOADED_VEINS.get(i);
            Services.getRegistration().registerBiomeModification(i, config);
        }

        Services.getRegistration().registerFuels(FUELS);

        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Registering creative tabs...");
        ores.mathieu.tabs.OresCreativeTabs.registerTabs();

        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Registration completed successfully.");
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
            OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Using vanilla equivalent for: {}", obj.registryName);
            return;
        }

        Identifier id = Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, obj.registryName);
        if (obj.type instanceof BlockType blockType) {
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

            if (blockType == BlockType.ORE) {
                ores.mathieu.tabs.OresCreativeTabs.addSafe(ores.mathieu.tabs.OresCreativeTabs.REGISTERED_ORES, new ores.mathieu.tabs.OresCreativeTabs.TabEntry(item, obj.material, blockType));
            } else {
                ores.mathieu.tabs.OresCreativeTabs.addSafe(ores.mathieu.tabs.OresCreativeTabs.REGISTERED_BLOCKS, new ores.mathieu.tabs.OresCreativeTabs.TabEntry(item, obj.material, blockType));
            }

            registerFuel(item, obj.material, blockType);
        } else if (obj.type instanceof ores.mathieu.material.ChemicalType) {
            processChemical(obj);
        } else if (obj.type instanceof ItemType itemType) {
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

        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Registered chemical item: {} (type: {})", baseName, type);

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

        if (!ores.mathieu.resource.TextureGenerator.canResolveBaseTexture(stoneNamespace, stonePath)) {
            OresCoreCommon.LOGGER.warn("[ORES CORE WARNING] Skipping ore {} — no resolvable texture for base block {}.", oreName, stoneName);
            return;
        }

        Identifier baseBlockId = Identifier.parse(stoneName);
        Block baseBlock = BuiltInRegistries.BLOCK.getValue(baseBlockId);
        Block.Properties blockProps;
        boolean hasGravity = false;
        boolean needsDeferredPatch = false;

        if (baseBlock != null && baseBlock != net.minecraft.world.level.block.Blocks.AIR) {
            if (!baseBlock.defaultBlockState().canOcclude()) {
                OresCoreCommon.LOGGER.warn("[ORES CORE WARNING] Skipping ore generation for {}; {} is not a full solid block.", oreName, stoneName);
                return;
            }
            float hardness = baseBlock.defaultDestroyTime() * ore.material.getOreHardnessFactor();
            float resistance = baseBlock.getExplosionResistance() * ore.material.getOreResistanceFactor();

            blockProps = Block.Properties.ofFullCopy(baseBlock)
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .strength(hardness, resistance)
                .overrideLootTable(java.util.Optional.of(ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, id.withPrefix("blocks/"))))
                .requiresCorrectToolForDrops();
            hasGravity = baseBlock instanceof net.minecraft.world.level.block.FallingBlock;
            OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Cloned properties from {} for {}", stoneName, oreName);
        } else {
            OresCoreCommon.LOGGER.warn("[ORES CORE WARNING] Base block {} not yet registered for ore {}. Using stone fallback + deferred patch.", stoneName, oreName);
            Block stone = net.minecraft.world.level.block.Blocks.STONE;
            float hardness = stone.defaultDestroyTime() * ore.material.getOreHardnessFactor();
            float resistance = stone.getExplosionResistance() * ore.material.getOreResistanceFactor();
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
                OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Target fuel: {} for {} ticks ({} * {})", item, finalFuelTime, baseFuelTime, fuelFactor);
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
