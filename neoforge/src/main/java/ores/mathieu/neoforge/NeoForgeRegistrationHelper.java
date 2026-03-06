//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - NeoForge Module
//
// Description: NeoForge implementation of the registration service.
// Manages deferred register buses for blocks, items, features, and carvers,
// while handling server-side reload listeners and fuel registries.
//
// Author: __mathieu
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.neoforge;


import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import ores.mathieu.OresCoreCommon;
import ores.mathieu.platform.RegistrationHelper;
import ores.mathieu.worldgen.VeinConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class NeoForgeRegistrationHelper implements RegistrationHelper {

    private final Map<Item, Integer> fuelMap = new HashMap<>();
    private final AtomicBoolean prepared = new AtomicBoolean(false);

    private final Map<Identifier, Block> blockQueue = new java.util.concurrent.ConcurrentHashMap<>();
    private final Map<Identifier, Item> itemQueue = new java.util.concurrent.ConcurrentHashMap<>();
    private final Map<Identifier, Feature<?>> featureQueue = new java.util.concurrent.ConcurrentHashMap<>();
    private final Map<Identifier, WorldCarver<?>> carverQueue = new java.util.concurrent.ConcurrentHashMap<>();
    private final Map<ResourceKey<CreativeModeTab>, CreativeModeTab> tabQueue = new java.util.concurrent.ConcurrentHashMap<>();

    public NeoForgeRegistrationHelper(IEventBus modEventBus) {

        modEventBus.addListener(net.neoforged.bus.api.EventPriority.LOWEST, this::onRegisterEvent);

        NeoForge.EVENT_BUS.addListener(this::onFuelBurnTime);
        NeoForge.EVENT_BUS.addListener(this::onAddReloadListeners);
    }

    private void onAddReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(net.minecraft.resources.Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, "reload_listener"), ores.mathieu.resource.OresCoreReloadListener.INSTANCE);
    }

    private void onRegisterEvent(RegisterEvent event) {
        if (prepared.compareAndSet(false, true)) {
            OresCoreCommon.LOGGER.debug("[ORES CORE] RegisterEvent started; preparing Ores Core internal registries...");
            ores.mathieu.registry.RegistrationHandler.registerAll();
            OresCoreCommon.generateResources();
            OresCoreCommon.LOGGER.debug("[ORES CORE] Internal preparation done. Queues populated.");
        }

        ResourceKey<? extends net.minecraft.core.Registry<?>> registryKey = event.getRegistryKey();

        if (registryKey.equals(net.minecraft.core.registries.Registries.BLOCK)) {
            event.register(net.minecraft.core.registries.Registries.BLOCK, helper -> {
                OresCoreCommon.LOGGER.debug("[ORES CORE] Dispatching {} blocks to NeoForge...", blockQueue.size());
                for (Map.Entry<Identifier, Block> entry : blockQueue.entrySet()) {
                    helper.register(entry.getKey(), entry.getValue());
                }
            });
        } else if (registryKey.equals(net.minecraft.core.registries.Registries.ITEM)) {
            event.register(net.minecraft.core.registries.Registries.ITEM, helper -> {
                OresCoreCommon.LOGGER.debug("[ORES CORE] Dispatching {} items to NeoForge...", itemQueue.size());
                for (Map.Entry<Identifier, Item> entry : itemQueue.entrySet()) {
                    helper.register(entry.getKey(), entry.getValue());
                }
            });
        } else if (registryKey.equals(net.minecraft.core.registries.Registries.FEATURE)) {
            event.register(net.minecraft.core.registries.Registries.FEATURE, helper -> {
                for (Map.Entry<Identifier, Feature<?>> entry : featureQueue.entrySet()) {
                    helper.register(entry.getKey(), entry.getValue());
                }
            });
        } else if (registryKey.equals(net.minecraft.core.registries.Registries.CARVER)) {
            event.register(net.minecraft.core.registries.Registries.CARVER, helper -> {
                for (Map.Entry<Identifier, WorldCarver<?>> entry : carverQueue.entrySet()) {
                    helper.register(entry.getKey(), entry.getValue());
                }
            });
        } else if (registryKey.equals(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB)) {
            event.register(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, helper -> {
                for (Map.Entry<ResourceKey<CreativeModeTab>, CreativeModeTab> entry : tabQueue.entrySet()) {
                    helper.register(entry.getKey(), entry.getValue());
                }
            });
        }
    }

    @Override
    public void registerBlock(Identifier id, Block block) {
        blockQueue.put(id, block);
    }

    @Override
    public void registerItem(Identifier id, Item item) {
        itemQueue.put(id, item);
    }

    @Override
    public void registerFeature(Identifier id, Feature<?> feature) {
        featureQueue.put(id, feature);
    }

    @Override
    public void registerCarver(Identifier id, WorldCarver<?> carver) {
        carverQueue.put(id, carver);
    }

    @Override
    public void registerCreativeTab(ResourceKey<CreativeModeTab> key, CreativeModeTab tab) {
        tabQueue.put(key, tab);
    }

    @Override
    public void registerFuels(Map<Item, Integer> fuels) {
        fuelMap.putAll(fuels);
    }

    private void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        Item item = event.getItemStack().getItem();
        Integer burnTime = fuelMap.get(item);
        if (burnTime != null) {
            event.setBurnTime(burnTime);
        }
    }

    @Override
    public void registerBiomeModification(int veinIndex, VeinConfig config) {

        OresCoreCommon.LOGGER.debug("[ORES CORE] Biome modification requested for vein {}.", veinIndex);
    }
}
