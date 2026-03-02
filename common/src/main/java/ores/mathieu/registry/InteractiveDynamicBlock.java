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
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

@SuppressWarnings("null")
public class InteractiveDynamicBlock extends DynamicBlock {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    private final boolean interactiveLight;
    private final boolean interactiveParticles;

    public InteractiveDynamicBlock(Properties properties, boolean hasGravity, boolean interactiveLight, boolean interactiveParticles) {
        super(properties, hasGravity);
        this.interactiveLight = interactiveLight;
        this.interactiveParticles = interactiveParticles;
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            spawnParticles(level, pos);
        } else {
            interact(state, level, pos);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        interact(state, level, pos);
        super.attack(state, level, pos, player);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!entity.isSteppingCarefully()) {
            interact(state, level, pos);
        }
        super.stepOn(level, pos, state, entity);
    }

    private void interact(BlockState state, Level level, BlockPos pos) {
        spawnParticles(level, pos);
        if ((interactiveLight || interactiveParticles) && !state.getValue(LIT)) {
            level.setBlock(pos, state.setValue(LIT, Boolean.valueOf(true)), 3);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(LIT);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            level.setBlock(pos, state.setValue(LIT, Boolean.valueOf(false)), 3);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (interactiveParticles && state.getValue(LIT)) {
            spawnParticles(level, pos);
        }
        super.animateTick(state, level, pos, random);
    }

    private void spawnParticles(Level level, BlockPos pos) {
        if (interactiveParticles && particleColor != -1) {
            RandomSource random = level.getRandom();
            float chance = 1.0f * particleIntensity;
            Direction[] dirs = Direction.values();
            for (Direction dir : dirs) {
                BlockPos offset = pos.relative(dir);
                if (!level.getBlockState(offset).isSolidRender()) {
                    float currentChance = chance;
                    while (currentChance > 0) {
                        if (random.nextFloat() < currentChance) {
                            Direction.Axis axis = dir.getAxis();
                            double x = axis == Direction.Axis.X ? 0.5D + 0.5625D * (double)dir.getStepX() : (double)random.nextFloat();
                            double y = axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double)dir.getStepY() : (double)random.nextFloat();
                            double z = axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double)dir.getStepZ() : (double)random.nextFloat();

                            int colorWithAlpha = particleColor | 0xFF000000;
                            level.addParticle(new DustParticleOptions(colorWithAlpha, 1.0F), (double)pos.getX() + x, (double)pos.getY() + y, (double)pos.getZ() + z, 0.0D, 0.0D, 0.0D);
                        }
                        currentChance -= 1.0f;
                    }
                }
            }
        }
    }
}
