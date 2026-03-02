//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Data configuration for vein carvers.
//
// Author: __mathieu
// Version: 26.1.001
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;

@SuppressWarnings("null")
public class VeinCarverConfiguration extends CarverConfiguration {
    public static final Codec<VeinCarverConfiguration> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("vein_index").forGetter(c -> c.veinIndex)
        ).apply(instance, VeinCarverConfiguration::new)
    );

    public final int veinIndex;

    public VeinCarverConfiguration(int veinIndex) {
        super(0.0f, ConstantHeight.of(VerticalAnchor.absolute(0)), ConstantFloat.of(1.0f), VerticalAnchor.absolute(0), CarverDebugSettings.of(false, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()), HolderSet.direct());
        this.veinIndex = veinIndex;
    }
}
