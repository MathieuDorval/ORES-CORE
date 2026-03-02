//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Handles compatibility and integration with the Create mod.
//
// Author: __mathieu
// Version: 26.1.003
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.compat;

import ores.mathieu.platform.Services;
import org.jspecify.annotations.Nullable;

public class CreateCompat {

    private static @Nullable Boolean createLoaded = null;

    public static boolean isCreateLoaded() {
        Boolean loaded = createLoaded;
        if (loaded == null) {
            loaded = Services.getPlatform().isModLoaded("create");
            createLoaded = loaded;
        }
        return loaded;
    }
}
