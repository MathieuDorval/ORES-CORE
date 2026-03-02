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
// Version: 26.1.001
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import java.util.List;

@Mixin(BiomeGenerationSettings.class)
public class BiomeGenerationSettingsMixin {

    @Inject(method = "features", at = @At("RETURN"), cancellable = true)
    private void filterOres(CallbackInfoReturnable<List<net.minecraft.core.HolderSet<net.minecraft.world.level.levelgen.placement.PlacedFeature>>> cir) {
        List<net.minecraft.core.HolderSet<net.minecraft.world.level.levelgen.placement.PlacedFeature>> original = cir.getReturnValue();
        if (original == null || original.isEmpty()) return;

        if (ores.mathieu.material.MaterialsDatabase.MATERIALS.isEmpty()) return;

        java.util.List<net.minecraft.core.HolderSet<net.minecraft.world.level.levelgen.placement.PlacedFeature>> filtered = new java.util.ArrayList<>();

        for (net.minecraft.core.HolderSet<net.minecraft.world.level.levelgen.placement.PlacedFeature> holders : original) {
            java.util.List<net.minecraft.core.Holder<net.minecraft.world.level.levelgen.placement.PlacedFeature>> innerList = new java.util.ArrayList<>();
            boolean changed = false;

            for (net.minecraft.core.Holder<net.minecraft.world.level.levelgen.placement.PlacedFeature> holder : holders) {

                String id = holder.getRegisteredName();

                boolean shouldRemove = false;
                if (!id.startsWith("ores:")) {
                    for (ores.mathieu.material.Material material : ores.mathieu.material.MaterialsDatabase.MATERIALS.values()) {
                        String matName = material.getName();

                        if (id.contains(matName) ||
                            (material.getDebrisName() != null && !material.getDebrisName().isEmpty() && id.contains(material.getDebrisName().replace("minecraft:", "")))) {
                            shouldRemove = true;
                            break;
                        }
                    }
                }

                if (shouldRemove) {
                    changed = true;
                } else {
                    innerList.add(holder);
                }
            }

            if (changed) {
                filtered.add(net.minecraft.core.HolderSet.direct(innerList));
            } else {
                filtered.add(holders);
            }
        }

        cir.setReturnValue(filtered);
    }
}
