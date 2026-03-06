//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Manages custom creative inventory tabs.
//
// Author: __mathieu
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.tabs;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import ores.mathieu.OresCoreCommon;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("null")
public class OresCreativeTabs {

    public static final ResourceKey<CreativeModeTab> ITEMS_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, "items"));
    public static final ResourceKey<CreativeModeTab> BLOCKS_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, "blocks"));
    public static final ResourceKey<CreativeModeTab> ORES_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, "ores"));

    public record TabEntry(Item item, ores.mathieu.material.Material material, Enum<?> type) {}

    public static final List<TabEntry> REGISTERED_ITEMS = new ArrayList<>();
    public static final List<TabEntry> REGISTERED_BLOCKS = new ArrayList<>();
    public static final List<TabEntry> REGISTERED_ORES = new ArrayList<>();

    private static final java.util.Set<Item> UNIQUE_ITEMS = new java.util.HashSet<>();
    private static final java.util.Set<Item> UNIQUE_BLOCKS = new java.util.HashSet<>();
    private static final java.util.Set<Item> UNIQUE_ORES = new java.util.HashSet<>();

    public static synchronized void addSafe(List<TabEntry> list, TabEntry entry) {
        if (entry.item == null || entry.item == net.minecraft.world.item.Items.AIR) return;
        
        java.util.Set<Item> uniqueSet;
        if (list == REGISTERED_ITEMS) uniqueSet = UNIQUE_ITEMS;
        else if (list == REGISTERED_BLOCKS) uniqueSet = UNIQUE_BLOCKS;
        else if (list == REGISTERED_ORES) uniqueSet = UNIQUE_ORES;
        else return;

        if (uniqueSet.add(entry.item)) {
            list.add(entry);
        }
    }

    public static final CreativeModeTab ITEMS_TAB = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(Items.IRON_INGOT))
            .title(Component.translatable("itemGroup.ores.items"))
            .displayItems((params, output) -> {
                sortAndAccept(REGISTERED_ITEMS, output);
            })
            .build();

    public static final CreativeModeTab BLOCKS_TAB = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(Items.IRON_BLOCK))
            .title(Component.translatable("itemGroup.ores.blocks"))
            .displayItems((params, output) -> {
                sortAndAccept(REGISTERED_BLOCKS, output);
            })
            .build();

    public static final CreativeModeTab ORES_TAB = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(Items.IRON_ORE))
            .title(Component.translatable("itemGroup.ores.ores"))
            .displayItems((params, output) -> {
                sortAndAccept(REGISTERED_ORES, output);
            })
            .build();

    private static void sortAndAccept(List<TabEntry> entries, CreativeModeTab.Output output) {
        entries.stream()
            .sorted((e1, e2) -> {

                int matCompare = e1.material.getName().compareTo(e2.material.getName());
                if (matCompare != 0) return matCompare;

                return Integer.compare(getTypeWeight(e1.type), getTypeWeight(e2.type));
            })
            .forEach(entry -> output.accept(entry.item));
    }

    private static int getTypeWeight(Enum<?> type) {
        if (type instanceof ores.mathieu.material.ItemType it) {
            return switch (it) {
                case NUGGET -> 10;
                case INGOT, GEM, SELF, REFINED_INGOT -> 20;
                case RAW, CRUSHED_RAW-> 30;
                case DUST, POWDER, DIRTY_DUST, SMALL_DUST -> 40;
                case ROD, PLATE, GEAR, RING, CRYSTAL, CHARGED_CRYSTAL, SHARD, CLUMP, SMALL_CLUMP, SCRAP, LARGE_PLATE, DOUBLE_INGOT, ENRICHED, COIN -> 50;
                default -> 100;
            };
        }
        if (type instanceof ores.mathieu.material.BlockType bt) {
            if (bt == ores.mathieu.material.BlockType.ORE) return 10;
            if (bt == ores.mathieu.material.BlockType.RAW_BLOCK) return 20;
            if (bt == ores.mathieu.material.BlockType.BLOCK) return 30;
            if (bt == ores.mathieu.material.BlockType.DUST_BLOCK || bt == ores.mathieu.material.BlockType.POWDER_BLOCK) return 40;

            int level = bt.getCompressionLevel();
            if (level > 0) {
                int offset = 0;
                if (bt.name().contains("RAW")) offset = 1;
                else if (bt.name().contains("DUST") || bt.name().contains("POWDER")) offset = 3;
                else offset = 2;
                return 1000 + (level * 10) + offset;
            }
            return 500;
        }
        if (type instanceof ores.mathieu.material.ChemicalType ct) {
            return switch (ct) {
                case CLEAN -> 11;
                case DIRTY -> 12;
                case SLURRY -> 13;
            };
        }
        return 999;
    }

    public static void registerTabs() {
        ores.mathieu.platform.Services.getRegistration().registerCreativeTab(ITEMS_KEY, ITEMS_TAB);
        ores.mathieu.platform.Services.getRegistration().registerCreativeTab(BLOCKS_KEY, BLOCKS_TAB);
        ores.mathieu.platform.Services.getRegistration().registerCreativeTab(ORES_KEY, ORES_TAB);
    }
}
