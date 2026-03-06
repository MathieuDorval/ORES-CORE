//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: System component for Ores Core.
//
// Author: __mathieu
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.platform;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;

import ores.mathieu.worldgen.VeinConfig;

import java.util.Map;

@SuppressWarnings("null")
public interface RegistrationHelper {

    void registerFuels(Map<Item, Integer> fuels);
    void registerBiomeModification(int veinIndex, VeinConfig config);

    default void registerFlammable(Block block, int catchSpeed, int burnSpeed) {}

    default void registerBlock(Identifier id, Block block) {
        if (!net.minecraft.core.registries.BuiltInRegistries.BLOCK.containsKey(id)) {
            Registry.register(net.minecraft.core.registries.BuiltInRegistries.BLOCK, id, block);
        }
    }

    default void registerItem(Identifier id, Item item) {
        if (!net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(id)) {
            Registry.register(net.minecraft.core.registries.BuiltInRegistries.ITEM, id, item);
        }
    }

    default void registerFeature(Identifier id, Feature<?> feature) {
        if (!net.minecraft.core.registries.BuiltInRegistries.FEATURE.containsKey(id)) {
            Registry.register(net.minecraft.core.registries.BuiltInRegistries.FEATURE, id, feature);
        }
    }

    default void registerCarver(Identifier id, WorldCarver<?> carver) {
        if (!net.minecraft.core.registries.BuiltInRegistries.CARVER.containsKey(id)) {
            Registry.register(net.minecraft.core.registries.BuiltInRegistries.CARVER, id, carver);
        }
    }

    default void registerCreativeTab(ResourceKey<CreativeModeTab> key, CreativeModeTab tab) {
        if (!net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(key)) {
            Registry.register(net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB, key, tab);
        }
    }
}
