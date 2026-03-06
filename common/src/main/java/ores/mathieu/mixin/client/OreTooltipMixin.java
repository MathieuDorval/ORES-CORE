//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Client mixin that intercepts ItemStack tooltip rendering.
// Appends dynamic Ores Core specific tooltips (mining levels, required tools,
// and exact dimension/height spawn ranges) to all valid block ores.
//
// Author: __mathieu
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.mixin.client;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import ores.mathieu.client.OreTooltipData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@SuppressWarnings("null")
@Mixin(ItemStack.class)
public abstract class OreTooltipMixin {

    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    private void orescore$addOreTooltip(Item.TooltipContext context, Player player, TooltipFlag flag,
                                        CallbackInfoReturnable<List<Component>> cir) {
        ItemStack self = (ItemStack) (Object) this;
        var id = BuiltInRegistries.ITEM.getKey(self.getItem());

        String namespace = id.getNamespace();
        String path = id.getPath();
        String lookupKey;

        if (namespace.equals("ores") && path.endsWith("_ore")) {
            lookupKey = path;
        } else if (namespace.equals("minecraft") && path.equals("ancient_debris")) {
            lookupKey = "minecraft:ancient_debris";
        } else {
            return;
        }

        OreTooltipData.OreInfo info = OreTooltipData.getOreInfo(lookupKey);
        if (info == null) return;

        List<Component> tooltip = cir.getReturnValue();

        
        tooltip.add(Component.empty());

        
        int levelColor = OreTooltipData.getMiningLevelColor(info.miningLevel);
        String levelKey = OreTooltipData.getLevelTranslationKey(info.miningLevel);
        String toolKey = OreTooltipData.getToolTranslationKey(info.toolType);

        MutableComponent toolLine = Component.translatable(levelKey)
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(levelColor)).withBold(true));
        toolLine.append(Component.literal(" ")
                .withStyle(Style.EMPTY.withBold(false)));
        toolLine.append(Component.translatable(toolKey)
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(levelColor)).withBold(true)));
        toolLine.append(Component.literal("+")
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999)).withBold(false)));

        tooltip.add(toolLine);

        
        if (!info.dimensionRanges.isEmpty()) {
            for (OreTooltipData.DimensionRange range : info.dimensionRanges) {
                boolean isDimAll = OreTooltipData.DIM_ALL.equals(range.dimensionId);

                if (isDimAll) {
                    
                    tooltip.add(Component.literal("  Y " + range.minY + " → " + range.maxY)
                            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x55FFFF))));
                } else {
                    
                    int dimColor = OreTooltipData.getDimensionColor(range.dimensionId);

                    MutableComponent dimName;
                    String dimKey = OreTooltipData.getDimensionTranslationKey(range.dimensionId);
                    if (dimKey != null) {
                        dimName = Component.translatable(dimKey);
                    } else {
                        dimName = Component.literal(OreTooltipData.getDimensionFallbackName(range.dimensionId));
                    }

                    MutableComponent dimLine = Component.literal("  ◆ ")
                            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(dimColor)));
                    dimLine.append(dimName.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(dimColor))));
                    dimLine.append(Component.literal("  Y " + range.minY + " → " + range.maxY)
                            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x55FFFF))));

                    tooltip.add(dimLine);
                }
            }
        } else {
            tooltip.add(Component.literal("  ◆ ")
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA0000)))
                    .append(Component.translatable("tooltip.ores.no_generation")
                            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA0000)).withItalic(true))));
        }
    }
}
