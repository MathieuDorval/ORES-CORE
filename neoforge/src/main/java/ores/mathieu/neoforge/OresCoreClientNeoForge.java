//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - NeoForge Module
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

package ores.mathieu.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import ores.mathieu.OresCoreCommon;

@Mod(value = OresCoreCommon.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = OresCoreCommon.MOD_ID, value = Dist.CLIENT)
public class OresCoreClientNeoForge {

    public OresCoreClientNeoForge(ModContainer container) {

        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] NeoForge Client initialized.");
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Client setup complete.");
    }
}
