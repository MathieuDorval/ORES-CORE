//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Custom world carver for large ore deposits.
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("null")
public class GiantVeinCarver extends WorldCarver<VeinCarverConfiguration> {

    private static final ThreadLocal<Integer> CURRENT_VEIN = new ThreadLocal<>();
    private final CaveWorldCarver caveCarver;

    public GiantVeinCarver(Codec<VeinCarverConfiguration> codec) {
        super(codec);
        this.caveCarver = new CaveWorldCarver(CaveCarverConfiguration.CODEC) {
            @Override
            protected int getCaveBound() {

                return 2;
            }

            @Override
            protected boolean carveBlock(CarvingContext context, CaveCarverConfiguration _config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor, CarvingMask mask, BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos checkPos, Aquifer aquifer, MutableBoolean reachedSurface) {
                Integer veinIndex = CURRENT_VEIN.get();
                if (veinIndex == null) return false;
                return GiantVeinCarver.this.placeOre(context, veinIndex, chunk, pos);
            }
        };
    }

    @Override
    public boolean isStartChunk(VeinCarverConfiguration config, RandomSource random) {
        int index = config.veinIndex;
        if (index < 0 || index >= ores.mathieu.config.ConfigManager.LOADED_VEINS.size()) return false;
        VeinConfig veinConfig = ores.mathieu.config.ConfigManager.LOADED_VEINS.get(index);

        return random.nextInt(veinConfig.rarity) == 0;
    }

    @Override
    public boolean carve(CarvingContext context, VeinCarverConfiguration config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor, RandomSource random, Aquifer aquifer, ChunkPos centerChunkPos, CarvingMask mask) {
        int index = config.veinIndex;
        if (index < 0 || index >= ores.mathieu.config.ConfigManager.LOADED_VEINS.size()) return false;
        VeinConfig veinConfig = ores.mathieu.config.ConfigManager.LOADED_VEINS.get(index);

        java.util.List<Holder<Block>> replaceableBlocks = new java.util.ArrayList<>();
        for (String stoneId : ores.mathieu.registry.DiscoveryManager.REGISTRY.getStonesReplacement()) {
            Identifier stoneIdent = Identifier.tryParse(stoneId);
            if (stoneIdent != null) {
                net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(stoneIdent)
                    .ifPresent(replaceableBlocks::add);
            }
        }
        HolderSet<Block> replaceables = HolderSet.direct(replaceableBlocks);

        CaveCarverConfiguration caveConfig = new CaveCarverConfiguration(
            1.0f,
            UniformHeight.of(VerticalAnchor.absolute(veinConfig.minY), VerticalAnchor.absolute(veinConfig.maxY)),
            UniformFloat.of(0.1f, 0.9f),
            VerticalAnchor.aboveBottom(8),
            CarverDebugSettings.of(false, Blocks.AIR.defaultBlockState()),
            replaceables,
            UniformFloat.of(0.7f, 1.4f),
            UniformFloat.of(0.8f, 1.3f),
            ConstantFloat.of(-0.7f)
        );

        CURRENT_VEIN.set(config.veinIndex);
        try {

            return caveCarver.carve(context, caveConfig, chunk, biomeAccessor, random, aquifer, centerChunkPos, mask);
        } finally {
            CURRENT_VEIN.remove();
        }
    }

    protected boolean placeOre(CarvingContext context, int index, ChunkAccess chunk, BlockPos.MutableBlockPos pos) {
        BlockState currentState = chunk.getBlockState(pos);
        String currentBlockName = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(currentState.getBlock()).toString();

        if (ores.mathieu.registry.DiscoveryManager.REGISTRY.getStonesReplacement().contains(currentBlockName)) {
            VeinConfig veinConfig = ores.mathieu.config.ConfigManager.LOADED_VEINS.get(index);

            RandomSource random = RandomSource.create(pos.asLong());
            String chosenMaterial = veinConfig.getMaterialWeighted(random);
            if (chosenMaterial == null) return false;

            Optional<Holder.Reference<Block>> assocHolder = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(Identifier.tryParse(veinConfig.associatedBlock));
            Block associatedBlock = assocHolder.map(Holder.Reference::value).orElse(Blocks.TUFF);

            Block bonusBlock = Blocks.AIR;
            if (veinConfig.bonusBlock != null && !veinConfig.bonusBlock.isEmpty()) {
                String bonusId = veinConfig.bonusBlock.contains(":") ? veinConfig.bonusBlock : "ores:" + veinConfig.bonusBlock;
                Optional<Holder.Reference<Block>> bonusHolder = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(Identifier.tryParse(bonusId));
                bonusBlock = bonusHolder.map(Holder.Reference::value).orElse(Blocks.AIR);
            }

            String stonePath = veinConfig.associatedBlock.contains(":") ? veinConfig.associatedBlock.split(":")[1] : veinConfig.associatedBlock;
            String oreBlockId = "ores:" + stonePath + "_" + chosenMaterial + "_ore";

            Optional<Holder.Reference<Block>> oreHolder = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(Identifier.tryParse(oreBlockId));
            Block oreBlock = oreHolder.map(Holder.Reference::value).orElse(Blocks.AIR);

            float roll = random.nextFloat();
            BlockState toPlace = associatedBlock.defaultBlockState();

            if (roll < veinConfig.oreDensity) {
                if (oreBlock != Blocks.AIR) {

                    if (random.nextFloat() < veinConfig.bonusBlockChance && bonusBlock != Blocks.AIR) {
                        toPlace = bonusBlock.defaultBlockState();
                    } else {
                        toPlace = oreBlock.defaultBlockState();
                    }
                }
            }

            chunk.setBlockState(pos, toPlace, 0);
            return true;
        }

        return false;
    }

    @Override
    protected boolean carveBlock(CarvingContext context, VeinCarverConfiguration config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor, CarvingMask mask, BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos checkPos, Aquifer aquifer, MutableBoolean reachedSurface) {
        return false;
    }
}
