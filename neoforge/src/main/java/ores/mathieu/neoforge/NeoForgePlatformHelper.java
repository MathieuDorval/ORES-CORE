//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - NeoForge Module
//
// Description: NeoForge integration layer for cross-platform services.
// Provides access to FML configuration paths, mod discovery, and resource
// scanning using NeoForges mod hooks.
//
// Author: __mathieu
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.neoforge;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.language.IModInfo;
import ores.mathieu.platform.PlatformHelper;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class NeoForgePlatformHelper implements PlatformHelper {

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public List<String> getAllModIds() {
        return ModList.get().getMods().stream()
            .map(IModInfo::getModId)
            .collect(Collectors.toList());
    }

    @Override
    public InputStream getModResource(String namespace, String path) {
        try {

            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            if (is != null) return is;
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public void scanModResources(String resourcePath, BiConsumer<String, InputStream> handler) {

        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(resourcePath);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                try (InputStream is = url.openStream()) {

                    String modId = guessModIdFromUrl(url);
                    handler.accept(modId, is);
                }
            }
        } catch (Exception e) {

        }
    }

    private String guessModIdFromUrl(URL url) {

        String urlPath = url.toString().toLowerCase();
        for (var modInfo : ModList.get().getMods()) {
            String modId = modInfo.getModId();
            if (urlPath.contains(modId)) {
                return modId;
            }
        }
        return "unknown";
    }

    @Override
    public Set<String> findTrimPatterns() {
        Set<String> patterns = new HashSet<>();

        for (var modInfo : ModList.get().getMods()) {
            String namespace = modInfo.getModId();
            if (namespace.equals("minecraft") || namespace.equals("neoforge")) continue;

            try {
                String trimDir = "assets/" + namespace + "/textures/trims/entity/humanoid/";
                URL url = Thread.currentThread().getContextClassLoader().getResource(trimDir);
                if (url != null) {
                    Path trimPath = Path.of(url.toURI());
                    if (Files.exists(trimPath) && Files.isDirectory(trimPath)) {
                        try (var stream = Files.list(trimPath)) {
                            stream.forEach(p -> {
                                String filename = p.getFileName().toString();
                                if (filename.endsWith(".png")) {
                                    patterns.add(namespace + ":" + filename.substring(0, filename.length() - 4));
                                }
                            });
                        }
                    }
                }
            } catch (Exception e) {

            }
        }

        return patterns;
    }
}
