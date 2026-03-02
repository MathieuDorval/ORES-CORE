//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - NeoForge Module
//
// Description: System component for Ores Core.
//
// Author: __mathieu
// Version: 26.1.001
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.neoforge;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import ores.mathieu.OresCoreCommon;
import ores.mathieu.platform.RegistrationHelper;
import ores.mathieu.worldgen.VeinConfig;

import java.util.HashMap;
import java.util.Map;

public class NeoForgeRegistrationHelper implements RegistrationHelper {

    private final Map<Item, Integer> fuelMap = new HashMap<>();
    private boolean registrationDone = false;

    public NeoForgeRegistrationHelper(IEventBus modEventBus) {

        modEventBus.addListener(this::onRegisterEvent);

        NeoForge.EVENT_BUS.addListener(this::onFuelBurnTime);
    }

    private void onRegisterEvent(RegisterEvent event) {

        if (!registrationDone) {
            event.register(Registries.BLOCK, helper -> {
                registrationDone = true;
                OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] RegisterEvent fired — running full registration...");

                ores.mathieu.registry.RegistrationHandler.registerAll();

                OresCoreCommon.generateResources();
            });
        }
    }

    @Override
    public void registerBlock(Identifier id, Block block) {
        net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.BLOCK, id, block);
    }

    @Override
    public void registerItem(Identifier id, Item item) {
        net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.ITEM, id, item);
    }

    @Override
    public void registerFeature(Identifier id, Feature<?> feature) {
        net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.FEATURE, id, feature);
    }

    @Override
    public void registerCarver(Identifier id, WorldCarver<?> carver) {
        net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.CARVER, id, carver);
    }

    @Override
    public void registerCreativeTab(ResourceKey<CreativeModeTab> key, CreativeModeTab tab) {
        net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB, key, tab);
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

        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Biome modification requested for vein {}.", veinIndex);
    }
}
