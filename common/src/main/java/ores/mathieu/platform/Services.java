//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Cross-loader platform services locator.
//
// Author: __mathieu
// Version: 26.1.001
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.platform;

public final class Services {

    private static PlatformHelper platformHelper;
    private static RegistrationHelper registrationHelper;

    public static void setPlatformHelper(PlatformHelper helper) {
        platformHelper = helper;
    }

    public static PlatformHelper getPlatform() {
        if (platformHelper == null) {
            throw new IllegalStateException("PlatformHelper has not been initialized! Each loader must call Services.setPlatformHelper() before common init.");
        }
        return platformHelper;
    }

    public static void setRegistrationHelper(RegistrationHelper helper) {
        registrationHelper = helper;
    }

    public static RegistrationHelper getRegistration() {
        if (registrationHelper == null) {
            throw new IllegalStateException("RegistrationHelper has not been initialized!");
        }
        return registrationHelper;
    }
}
