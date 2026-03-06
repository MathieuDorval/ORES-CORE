//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Intermediate item abstraction used primarily for linking
// internal chemical states to materialized in-game equivalents.
//
// Author: __mathieu
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.material;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class ChemicalItem extends Item {
    private final Material material;
    private final ChemicalType type;

    public ChemicalItem(Properties properties, Material material, ChemicalType type) {
        super(properties);
        this.material = material;
        this.type = type;
    }

    public Material getMaterial() {
        return material;
    }

    public ChemicalType getType() {
        return type;
    }

    @Override
    public @NonNull Component getName(@NonNull ItemStack stack) {
        String baseName = type.getDefaultOverride().getNamingPattern().replace("%s", material.getName());
        return Component.translatable("item.ores." + baseName);
    }
}
