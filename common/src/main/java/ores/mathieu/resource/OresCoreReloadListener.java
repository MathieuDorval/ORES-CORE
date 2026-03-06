//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Reload listener that intercepts Minecraft resource pack 
// reload events (usually triggered by the /reload command). It ensures 
// that all world generation configurations for ore veins are re-parsed 
// and re-applied without requiring a full game restart.
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

import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import ores.mathieu.config.ConfigManager;
import ores.mathieu.OresCoreCommon;

public class OresCoreReloadListener implements ResourceManagerReloadListener {

    public static final OresCoreReloadListener INSTANCE = new OresCoreReloadListener();

    @Override
    public void onResourceManagerReload(@org.jspecify.annotations.NonNull ResourceManager resourceManager) {
        OresCoreCommon.LOGGER.info("[ORES CORE] Reloading ore generation configurations from /reload...");
        ConfigManager.loadVeins();
    }
}

