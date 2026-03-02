//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Fabric Module
//
// Description: Fabric-specific mod entrypoint.
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

import net.fabricmc.api.ModInitializer;
import ores.mathieu.OresCoreCommon;
import ores.mathieu.platform.Services;

public class OresCoreFabric implements ModInitializer {
    @Override
    public void onInitialize() {

        Services.setPlatformHelper(new FabricPlatformHelper());
        Services.setRegistrationHelper(new FabricRegistrationHelper());

        OresCoreCommon.init();

        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTING.register(server ->
            ores.mathieu.registry.PendingPatchManager.applyAll()
        );
    }
}
