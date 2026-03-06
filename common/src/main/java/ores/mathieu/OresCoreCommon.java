//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Main entrypoint orchestration class for Ores Core. 
// Responsible for sequencing the mod's initialization phases including 
// configuration loading, registry scanning, object registration, and 
// dynamic resource generation.
//
// Author: __mathieu
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 



package ores.mathieu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OresCoreCommon {
    public static final String MOD_ID = "ores";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void initData() {
        try {
            LOGGER.debug("[ORES CORE] Initializing data phase...");
            ores.mathieu.config.ConfigManager.preInitialize();
            ores.mathieu.registry.DiscoveryManager.scanRegistries();
            ores.mathieu.config.ConfigManager.initialize();
            LOGGER.debug("[ORES CORE] Data phase completed.");
        } catch (Exception e) {
            LOGGER.error("[ORES CORE] CRITICAL ERROR during data initialization!", e);
        }
    }

    public static void registerAll() {
        try {
            LOGGER.debug("[ORES CORE] Registration phase...");
            ores.mathieu.registry.RegistrationHandler.registerAll();
            LOGGER.debug("[ORES CORE] Registration phase completed.");
        } catch (Exception e) {
            LOGGER.error("[ORES CORE] CRITICAL ERROR during registration!", e);
        }
    }

    public static void generateResources() {
        try {
            LOGGER.debug("[ORES CORE] Resource generation phase...");
            ores.mathieu.resource.DynamicResourceGenerator.generateResources();
            LOGGER.debug("[ORES CORE] Initialized successfully!");
        } catch (Exception e) {
            LOGGER.error("[ORES CORE] CRITICAL ERROR during resource generation!", e);
        }
    }

    public static void init() {
        initData();
        registerAll();
        generateResources();
    }
}