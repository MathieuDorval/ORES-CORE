//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Integration module for Mekanism mod. 
// Handles dynamic registration of slurry/gas chemicals and generates 
// the complex ore processing chains.
//
// Author: __mathieu
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.compat;

import ores.mathieu.OresCoreCommon;
import ores.mathieu.platform.Services;
import ores.mathieu.material.Material;
import ores.mathieu.material.ChemicalType;
import org.jspecify.annotations.Nullable;

public class MekanismCompat {

    private static @Nullable Boolean mekanismLoaded = null;

    public static boolean isMekanismLoaded() {
        Boolean loaded = mekanismLoaded;
        if (loaded == null) {
            loaded = Services.getPlatform().isModLoaded("mekanism");
            mekanismLoaded = loaded;
        }
        return loaded;
    }

    public static void registerChemical(String matName, Material material, ChemicalType type) {
        if (!isMekanismLoaded()) return;

        try {

            Class<?> builderClass = Class.forName("mekanism.api.chemical.ChemicalBuilder");

            String chemicalPath = java.util.Objects.requireNonNull(type.getDefaultOverride().getNamingPattern().replace("%s", matName));
            net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.fromNamespaceAndPath("ores", chemicalPath);

            java.lang.reflect.Method builderMethod = builderClass.getMethod("builder");
            Object builder = builderMethod.invoke(null);

            java.lang.reflect.Method tintMethod = builderClass.getMethod("tint", int.class);
            int tintColor = material.getChemicalSlurryColor() != -1 ? material.getChemicalSlurryColor() : material.getMaterialColor();
            builder = tintMethod.invoke(builder, tintColor);

            java.lang.reflect.Method buildMethod = builderClass.getMethod("build");
            Object chemical = buildMethod.invoke(builder);

            net.minecraft.resources.Identifier registryId = net.minecraft.resources.Identifier.fromNamespaceAndPath("mekanism", "chemical");

            java.util.Optional<?> registryHolder = net.minecraft.core.registries.BuiltInRegistries.REGISTRY.get(registryId);

            if (registryHolder.isPresent()) {
                Object holder = registryHolder.get();
                if (holder instanceof net.minecraft.core.Holder.Reference<?> ref) {
                    @SuppressWarnings("unchecked")
                    net.minecraft.core.Registry<Object> registry = (net.minecraft.core.Registry<Object>) java.util.Objects.requireNonNull(ref.value());

                    net.minecraft.core.Registry.register(registry, id, chemical);
                    OresCoreCommon.LOGGER.debug("[ORES CORE] Registered Mekanism chemical: {}", id);
                }
            } else {
                OresCoreCommon.LOGGER.debug("[ORES CORE] Mekanism chemical registry not found, skipping API registration for {}", id);
            }

        } catch (Exception e) {
            OresCoreCommon.LOGGER.error("[ORES CORE] Failed to register Mekanism chemical for {}: {}", matName, e.getMessage());
        }
    }
}
