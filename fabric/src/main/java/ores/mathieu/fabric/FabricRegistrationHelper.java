//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Fabric Module
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

package ores.mathieu.fabric;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import ores.mathieu.OresCoreCommon;
import ores.mathieu.platform.RegistrationHelper;
import ores.mathieu.worldgen.VeinConfig;

import java.util.Map;
import java.util.function.Predicate;

@SuppressWarnings("null")
public class FabricRegistrationHelper implements RegistrationHelper {

    @Override
    public void registerFlammable(Block block, int catchSpeed, int burnSpeed) {
        FlammableBlockRegistry.getDefaultInstance().add(block, catchSpeed, burnSpeed);
    }

    @Override
    public void registerFuels(Map<Item, Integer> fuels) {
        if (fuels.isEmpty()) return;

        FuelValueEvents.BUILD.register((builder, context) -> {
            for (Map.Entry<Item, Integer> entry : fuels.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        });
        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] {} fuels registered via Fabric API.", fuels.size());
    }

    @Override
    public void registerBiomeModification(int veinIndex, VeinConfig config) {

        Predicate<BiomeSelectionContext> selector = context -> {
            ResourceKey<net.minecraft.world.level.biome.Biome> biomeKey = context.getBiomeKey();
            if (biomeKey != null) {
                String biomeId = biomeKey.identifier().toString();
                if (config.biomesWhitelist != null && !config.biomesWhitelist.isEmpty() && !config.biomesWhitelist.contains(biomeId)) return false;
                if (config.biomesBlacklist != null && !config.biomesBlacklist.isEmpty() && config.biomesBlacklist.contains(biomeId)) return false;
            }

            if (config.dimensionsWhitelist != null && !config.dimensionsWhitelist.isEmpty()) {
                boolean matched = false;
                for (String dimId : config.dimensionsWhitelist) {
                    Identifier id = Identifier.tryParse(dimId);
                    if (id != null) {
                        ResourceKey<net.minecraft.world.level.dimension.LevelStem> stemKey =
                            ResourceKey.create(net.minecraft.core.registries.Registries.LEVEL_STEM, id);
                        if (context.canGenerateIn(stemKey)) { matched = true; break; }
                    }
                }
                if (!matched) return false;
            }

            if (config.dimensionsBlacklist != null && !config.dimensionsBlacklist.isEmpty()) {
                for (String dimId : config.dimensionsBlacklist) {
                    Identifier id = Identifier.tryParse(dimId);
                    if (id != null) {
                        ResourceKey<net.minecraft.world.level.dimension.LevelStem> stemKey =
                            ResourceKey.create(net.minecraft.core.registries.Registries.LEVEL_STEM, id);
                        if (context.canGenerateIn(stemKey)) return false;
                    }
                }
            }
            return true;
        };

        if (config.veinType.equals("GIANT")) {
            ResourceKey<net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver<?>> carverKey =
                ResourceKey.create(net.minecraft.core.registries.Registries.CONFIGURED_CARVER, Identifier.fromNamespaceAndPath("ores", "vein_" + veinIndex));
            BiomeModifications.addCarver(selector, carverKey);
        } else {
            ResourceKey<net.minecraft.world.level.levelgen.placement.PlacedFeature> featureKey =
                ResourceKey.create(net.minecraft.core.registries.Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("ores", "vein_" + veinIndex));
            BiomeModifications.addFeature(selector, GenerationStep.Decoration.UNDERGROUND_ORES, featureKey);
        }

        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Biome modification registered for vein {}.", veinIndex);
    }
}
