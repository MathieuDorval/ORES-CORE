//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Flexible world generation feature implementation for standard 
// Ores Core deposits. It calculates exact Z-axis and Y-axis placement 
// using configurable distribution algorithms (Uniform, Triangle, Trapezoid, 
// Gaussian). It supports several geometric cluster shapes (Blob, Plate, 
// Horizontal, Vertical, Scattered) and respects advanced environmental 
// constraints like AIR_ONLY or WATER_ONLY specificities, dynamically 
// mapping ore variants to the underlying host block (stone, deepslate, 
// etc.) on the fly.
//
// Author: __mathieu
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

@SuppressWarnings("null")
public class DynamicOreFeature extends Feature<VeinFeatureConfiguration> {

    public DynamicOreFeature(Codec<VeinFeatureConfiguration> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<VeinFeatureConfiguration> context) {
        net.minecraft.world.level.WorldGenLevel level = context.level();
        net.minecraft.core.BlockPos origin = context.origin();
        net.minecraft.util.RandomSource random = context.random();

        int index = context.config().veinIndex;
        if (index < 0 || index >= ores.mathieu.config.ConfigManager.LOADED_VEINS.size()) return false;

        VeinConfig config = ores.mathieu.config.ConfigManager.LOADED_VEINS.get(index);

        int minYRange = config.minY;
        int maxYRange = config.maxY;
        int deltaY = maxYRange - minYRange;
        int finalY = origin.getY();

        if (deltaY > 0) {
            finalY = switch (config.distribution) {
                case "TRIANGLE_HIGH" -> minYRange + (int) (deltaY * Math.sqrt(random.nextDouble()));
                case "TRIANGLE_LOW" -> maxYRange - (int) (deltaY * Math.sqrt(random.nextDouble()));
                case "TRAPEZOID" -> {
                    int plateau = deltaY / 2;
                    int side = (deltaY - plateau) / 2;
                    yield minYRange + random.nextInt(side + plateau + 1) + random.nextInt(side + 1);
                }
                case "GAUSSIAN" -> {
                    double gaussian = random.nextGaussian() * 0.12 + 0.5; 
                    yield minYRange + (int) (deltaY * net.minecraft.util.Mth.clamp(gaussian, 0, 1));
                }
                default -> minYRange + random.nextInt(deltaY + 1);
            };
        }

        final net.minecraft.core.BlockPos baseOrigin = new net.minecraft.core.BlockPos(origin.getX(), finalY, origin.getZ());
        
        if (config.materials == null || config.materials.isEmpty()) return false;
        
        int baseSize = config.density;
        int size = baseSize + random.nextInt(Math.max(1, baseSize / 2 + 1)) - (baseSize / 4);
        if (size <= 0) return false;

        int placedCount = 0;

        float f1 = (float) size / 8.0F;
        int i = net.minecraft.util.Mth.ceil(((float) size / 16.0F * 2.0F + 1.0F) / 2.0F);

        int minX = 0, minY = 0, minZ = 0, maxX = 0, maxY = 0, maxZ = 0;

        if (config.shape.equals("PLATE")) {
            int radiusTarget = Math.min(size / 2 + random.nextInt(2), 14);
            minX = baseOrigin.getX() - radiusTarget; maxX = baseOrigin.getX() + radiusTarget;
            minZ = baseOrigin.getZ() - radiusTarget; maxZ = baseOrigin.getZ() + radiusTarget;
            minY = baseOrigin.getY(); maxY = baseOrigin.getY();
        } else if (config.shape.equals("HORIZONTAL")) {
            int radiusTarget = Math.min(size / 2 + random.nextInt(2), 14);
            int heightRange = Math.max(1, size / 8) + random.nextInt(2);
            minX = baseOrigin.getX() - radiusTarget; maxX = baseOrigin.getX() + radiusTarget;
            minZ = baseOrigin.getZ() - radiusTarget; maxZ = baseOrigin.getZ() + radiusTarget;
            minY = baseOrigin.getY() - heightRange; maxY = baseOrigin.getY() + heightRange;
        } else if (config.shape.equals("VERTICAL")) {
            int heightTarget = Math.min(size / 2 + 2 + random.nextInt(2), 14);
            int radiusTarget = Math.max(1, size / 10) + random.nextInt(2);
            minY = baseOrigin.getY() - heightTarget; maxY = baseOrigin.getY() + heightTarget;
            minX = baseOrigin.getX() - radiusTarget; maxX = baseOrigin.getX() + radiusTarget;
            minZ = baseOrigin.getZ() - radiusTarget; maxZ = baseOrigin.getZ() + radiusTarget;
        } else if (config.shape.equals("SCATTERED")) {

            int range = Math.min(size + 2, 14);
            minX = baseOrigin.getX() - range; maxX = baseOrigin.getX() + range;
            minY = baseOrigin.getY() - range / 2; maxY = baseOrigin.getY() + range / 2;
            minZ = baseOrigin.getZ() - range; maxZ = baseOrigin.getZ() + range;
        } else if (config.shape.equals("BLOB")) {

            float piFactor = random.nextFloat() * (float) Math.PI;
            double d0 = (double) ((float) baseOrigin.getX() + net.minecraft.util.Mth.sin(piFactor) * f1);
            double d1 = (double) ((float) baseOrigin.getX() - net.minecraft.util.Mth.sin(piFactor) * f1);
            double d2 = (double) ((float) baseOrigin.getZ() + net.minecraft.util.Mth.cos(piFactor) * f1);
            double d3 = (double) ((float) baseOrigin.getZ() - net.minecraft.util.Mth.cos(piFactor) * f1);
            double d4 = (double) (baseOrigin.getY() + random.nextInt(3) - 2);
            double d5 = (double) (baseOrigin.getY() + random.nextInt(3) - 2);

            minX = baseOrigin.getX() - net.minecraft.util.Mth.ceil(f1) - i;
            minY = baseOrigin.getY() - 2 - i;
            minZ = baseOrigin.getZ() - net.minecraft.util.Mth.ceil(f1) - i;
            maxX = baseOrigin.getX() + net.minecraft.util.Mth.ceil(f1) + i;
            maxY = baseOrigin.getY() + 2 + i;
            maxZ = baseOrigin.getZ() + net.minecraft.util.Mth.ceil(f1) + i;

            for (int x = minX; x <= maxX; x++) {
                if (placedCount >= size) break;
                double d12 = ((double) x + 0.5D - d0) / (d1 - d0);
                if (d12 * d12 >= 1.0D) continue;
                for (int y = minY; y <= maxY; y++) {
                    if (placedCount >= size) break;
                    double d13 = ((double) y + 0.5D - d4) / (d5 - d4);
                    if (d12 * d12 + d13 * d13 >= 1.0D) continue;
                    for (int z = minZ; z <= maxZ; z++) {
                        if (placedCount >= size) break;
                        double d14 = ((double) z + 0.5D - d2) / (d3 - d2);
                        if (d12 * d12 + d13 * d13 + d14 * d14 >= 1.0D) continue;

                        if (y < config.minY || y > config.maxY) continue;

                        net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos(x, y, z);

                        if (size > 4 && random.nextFloat() > 0.85f) continue;

                        if (config.airExposureChance > 0.0f && isAdjacentToAir(level, pos) && random.nextFloat() < config.airExposureChance) continue;
                        String mat = config.getMaterialWeighted(random);
                        if (mat != null && placeOre(level, pos, mat, config.specifics, random)) placedCount++;
                    }
                }
            }
            return placedCount > 0;
        } else {

            minX = baseOrigin.getX() - 1; maxX = baseOrigin.getX() + 1;
            minY = baseOrigin.getY() - 1; maxY = baseOrigin.getY() + 1;
            minZ = baseOrigin.getZ() - 1; maxZ = baseOrigin.getZ() + 1;
        }

        for (int x = minX; x <= maxX; x++) {
            if (placedCount >= size) break;
            for (int y = minY; y <= maxY; y++) {
                if (placedCount >= size) break;
                for (int z = minZ; z <= maxZ; z++) {
                    if (placedCount >= size) break;
                    
                    if (y < config.minY || y > config.maxY) continue;
                    
                    net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos(x, y, z);

                    if (config.shape.equals("SCATTERED")) {
                        if (random.nextFloat() > 0.15f) continue;
                        String scatMat = config.getMaterialWeighted(random);
                        if (scatMat != null && !hasAdjacentOre(level, pos, scatMat)) {
                            if (config.airExposureChance > 0.0f && isAdjacentToAir(level, pos) && random.nextFloat() < config.airExposureChance) continue;
                            if (placeOre(level, pos, scatMat, config.specifics, random)) placedCount++;
                        }
                        continue; 
                    }

                    if (!config.shape.equals("BLOB")) {
                        double dx = (x - baseOrigin.getX()) / (double)((maxX - baseOrigin.getX() == 0 ? 1 : maxX - baseOrigin.getX()));
                        double dy = (y - baseOrigin.getY()) / (double)((maxY - baseOrigin.getY() == 0 ? 1 : maxY - baseOrigin.getY()));
                        double dz = (z - baseOrigin.getZ()) / (double)((maxZ - baseOrigin.getZ() == 0 ? 1 : maxZ - baseOrigin.getZ()));
                        if (dx*dx + dy*dy + dz*dz > 1.1) continue;

                        if (random.nextFloat() > 0.75f) continue;
                    }

                    if (config.airExposureChance > 0.0f && isAdjacentToAir(level, pos) && random.nextFloat() < config.airExposureChance) continue;
                    String mat = config.getMaterialWeighted(random);
                    if (mat != null && placeOre(level, pos, mat, config.specifics, random)) placedCount++;
                }
            }
        }

        return placedCount > 0;
    }
    private boolean isAdjacentToAir(net.minecraft.world.level.WorldGenLevel level, net.minecraft.core.BlockPos pos) {
        for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
            if (level.getBlockState(pos.relative(dir)).isAir()) return true;
        }
        return false;
    }

    private boolean isAdjacentToWater(net.minecraft.world.level.WorldGenLevel level, net.minecraft.core.BlockPos pos) {
        for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
            if (level.getFluidState(pos.relative(dir)).is(net.minecraft.tags.FluidTags.WATER)) return true;
        }
        return false;
    }

    private boolean isAdjacentToLava(net.minecraft.world.level.WorldGenLevel level, net.minecraft.core.BlockPos pos) {
        for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
            if (level.getFluidState(pos.relative(dir)).is(net.minecraft.tags.FluidTags.LAVA)) return true;
        }
        return false;
    }

    private boolean hasAdjacentOre(net.minecraft.world.level.WorldGenLevel level, net.minecraft.core.BlockPos pos, String materialName) {
        for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
            String blockId = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.relative(dir)).getBlock()).toString();
            if (blockId.contains(materialName) && blockId.contains("_ore")) return true;
        }
        return false;
    }

    private boolean placeOre(net.minecraft.world.level.WorldGenLevel level, net.minecraft.core.BlockPos pos, String materialName, String specifics, net.minecraft.util.RandomSource random) {
        net.minecraft.world.level.block.state.BlockState currentState = level.getBlockState(pos);

        if (specifics != null && !specifics.equals("NONE")) {
            switch (specifics) {
                case "AIR_ONLY":
                    if (!isAdjacentToAir(level, pos)) return false;
                    break;
                case "CAVE_ONLY":

                    if (!isAdjacentToAir(level, pos)) return false;
                    break;
                case "WATER_ONLY":
                    if (!isAdjacentToWater(level, pos)) return false;
                    break;
                case "LAVA_ONLY":
                    if (!isAdjacentToLava(level, pos)) return false;
                    break;
                case "UNIQUE":
                    if (hasAdjacentOre(level, pos, materialName)) return false;
                    break;
            }
        }

        net.minecraft.resources.Identifier blockId = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(currentState.getBlock());
        String currentBlockName = blockId.toString();

        if (ores.mathieu.registry.DiscoveryManager.REGISTRY.getStonesReplacement().containsKey(currentBlockName)) {
            String stonePath = currentBlockName.contains(":") ? currentBlockName.split(":")[1] : currentBlockName;
            String oreBlockId = "ores:" + stonePath + "_" + materialName + "_ore";
            java.util.Optional<net.minecraft.core.Holder.Reference<net.minecraft.world.level.block.Block>> holder = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.Identifier.tryParse(oreBlockId));
            net.minecraft.world.level.block.Block oreBlock = holder.map(net.minecraft.core.Holder.Reference::value).orElse(net.minecraft.world.level.block.Blocks.AIR);
            if (oreBlock != net.minecraft.world.level.block.Blocks.AIR) {
                level.setBlock(pos, oreBlock.defaultBlockState(), 2);
                return true;
            }
        }
        return false;
    }
}
