//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - NeoForge Module
//
// Description: NeoForge-specific mod entrypoint.
//
// Author: __mathieu
// Version: 26.1.001
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import ores.mathieu.OresCoreCommon;
import ores.mathieu.platform.Services;

@Mod(OresCoreCommon.MOD_ID)
public class OresCoreNeoForge {

    public OresCoreNeoForge(IEventBus modEventBus) {

        Services.setPlatformHelper(new NeoForgePlatformHelper());
        Services.setRegistrationHelper(new NeoForgeRegistrationHelper(modEventBus));

        OresCoreCommon.initData();
        
        modEventBus.addListener(this::onLoadComplete);
    }

    private void onLoadComplete(FMLLoadCompleteEvent event) {
        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] FMLLoadCompleteEvent fired — applying deferred patches...");
        ores.mathieu.registry.PendingPatchManager.applyAll();
    }
}
