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
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.resource;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier;
import net.minecraft.server.packs.PackSelectionConfig;

import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("null")
public class VirtualPackSource implements RepositorySource {
    @Override
    public void loadPacks(Consumer<Pack> consumer) {
        PackLocationInfo info = new PackLocationInfo("ores_generated_resources", Component.literal("Ores Core Generated Resources"), PackSource.BUILT_IN, Optional.empty());
        ResourcesSupplier clientSupplier = new ResourcesSupplier() {
            @Override
            public PackResources openPrimary(PackLocationInfo info) {
                return VirtualResourcePack.CLIENT;
            }
            @Override
            public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
                return VirtualResourcePack.CLIENT;
            }
        };

        ResourcesSupplier serverSupplier = new ResourcesSupplier() {
            @Override
            public PackResources openPrimary(PackLocationInfo info) {
                return VirtualResourcePack.SERVER;
            }
            @Override
            public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
                return VirtualResourcePack.SERVER;
            }
        };

        PackSelectionConfig config = new PackSelectionConfig(true, Pack.Position.TOP, true);

        Pack pack = Pack.readMetaAndCreate(
            info,
            clientSupplier,
            PackType.CLIENT_RESOURCES,
            config
        );

        if (pack != null) {
            consumer.accept(pack);
        }

        Pack serverPack = Pack.readMetaAndCreate(
            new PackLocationInfo("ores_generated_data", Component.literal("Ores Core Generated Data"), PackSource.BUILT_IN, Optional.empty()),
            serverSupplier,
            PackType.SERVER_DATA,
            config
        );

        if (serverPack != null) {
            consumer.accept(serverPack);
        }
    }
}
