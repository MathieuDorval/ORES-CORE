//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Interface for platform-specific helper methods.
//
// Author: __mathieu
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.platform;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;

public interface PlatformHelper {

    Path getConfigDir();

    boolean isModLoaded(String modId);

    List<String> getAllModIds();

    InputStream getModResource(String namespace, String path);

    void scanModResources(String resourcePath, BiConsumer<String, InputStream> handler);

    java.util.Set<String> findTrimPatterns();
}
