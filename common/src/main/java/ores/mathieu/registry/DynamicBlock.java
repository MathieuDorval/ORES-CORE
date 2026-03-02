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

package ores.mathieu.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("null")
public class DynamicBlock extends Block implements Fallable {

    private final boolean hasGravity;
    private int xpMin = 0;
    private int xpMax = 0;
    protected boolean constantParticles = false;
    protected int particleColor = -1;
    protected float particleIntensity = 1.0f;
    protected int redstonePower = 0;
    protected boolean dropWhenHitTorch = true;
    protected boolean translucent = false;

    public DynamicBlock(Properties properties) {
        this(properties, false);
    }

    public DynamicBlock(Properties properties, boolean hasGravity) {
        super(properties);
        this.hasGravity = hasGravity;
    }

    public DynamicBlock setXpRange(int min, int max) {
        this.xpMin = min;
        this.xpMax = max;
        return this;
    }

    public DynamicBlock setParticles(boolean constantParticles, int particleColor, float particleIntensity) {
        this.constantParticles = constantParticles;
        this.particleColor = particleColor;
        this.particleIntensity = particleIntensity;
        return this;
    }

    public DynamicBlock setRedstonePower(int power) {
        this.redstonePower = power;
        return this;
    }

    public DynamicBlock setTranslucent(boolean translucent) {
        this.translucent = translucent;
        return this;
    }

    public DynamicBlock setDropWhenHitTorch(boolean drop) {
        this.dropWhenHitTorch = drop;
        return this;
    }

    @Override
    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.world.item.ItemStack stack, boolean dropExperience) {
        super.spawnAfterBreak(state, level, pos, stack, dropExperience);
        if (dropExperience && (xpMax > 0)) {
            int xp = level.getRandom().nextInt(xpMax - xpMin + 1) + xpMin;
            if (xp > 0) {
                this.popExperience(level, pos, xp);
            }
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (hasGravity) {
            level.scheduleTick(pos, this, 2);
        }
    }

    @Override
    protected BlockState updateShape(
        BlockState state,
        LevelReader level,
        ScheduledTickAccess scheduledTickAccess,
        BlockPos pos,
        Direction direction,
        BlockPos neighborPos,
        BlockState neighborState,
        RandomSource random
    ) {
        if (hasGravity) {
            scheduledTickAccess.scheduleTick(pos, this, 2);
        }
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (hasGravity && isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinY()) {
            FallingBlockEntity entity = FallingBlockEntity.fall(level, pos, state);
            this.falling(entity);
        }
    }

    protected void falling(FallingBlockEntity entity) {
        entity.dropItem = this.dropWhenHitTorch;
    }

    public static boolean isFree(BlockState state) {
        return state.isAir() || state.is(BlockTags.FIRE) || state.canBeReplaced();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (hasGravity && random.nextInt(16) == 0) {
            BlockPos below = pos.below();
            if (isFree(level.getBlockState(below))) {
                double d0 = (double)pos.getX() + random.nextDouble();
                double d1 = (double)pos.getY() - 0.05;
                double d2 = (double)pos.getZ() + random.nextDouble();
                level.addParticle(
                    new BlockParticleOption(ParticleTypes.FALLING_DUST, state),
                    d0, d1, d2, 0.0, 0.0, 0.0
                );
            }
        }

        if (constantParticles && particleColor != -1) {
            float chance = 0.2f * particleIntensity;
            while (chance > 0) {
                if (random.nextFloat() < chance) {
                    double x = (double)pos.getX() + random.nextDouble();
                    double y = (double)pos.getY() + random.nextDouble();
                    double z = (double)pos.getZ() + random.nextDouble();
                    int colorWithAlpha = particleColor | 0xFF000000;
                    level.addParticle(new net.minecraft.core.particles.DustParticleOptions(colorWithAlpha, 1.0F), x, y, z, 0.0D, 0.0D, 0.0D);
                }
                chance -= 1.0f;
            }
        }
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return redstonePower > 0;
    }

    @Override
    protected int getSignal(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, Direction direction) {
        return redstonePower;
    }

    @Override
    protected float getShadeBrightness(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos) {
        return state.canOcclude() ? 0.2F : 1.0F;
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction direction) {
        if (this.translucent && adjacentBlockState.is(this)) {
            return true;
        }
        return super.skipRendering(state, adjacentBlockState, direction);
    }

    public boolean hasGravity() {
        return hasGravity;
    }
}
