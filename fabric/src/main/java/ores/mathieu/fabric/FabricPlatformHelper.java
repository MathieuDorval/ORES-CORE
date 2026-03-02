//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Fabric Module
//
// Description: Fabric implementation of platform-specific methods.
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

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import ores.mathieu.platform.PlatformHelper;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FabricPlatformHelper implements PlatformHelper {

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public List<String> getAllModIds() {
        return FabricLoader.getInstance().getAllMods().stream()
            .map(mod -> mod.getMetadata().getId())
            .collect(Collectors.toList());
    }

    @Override
    public InputStream getModResource(String namespace, String path) {
        try {

            Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer(namespace);
            if (mod.isPresent()) {
                Optional<Path> p = mod.get().findPath(path);
                if (p.isPresent() && Files.exists(p.get())) {
                    return Files.newInputStream(p.get());
                }
            }

            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            if (is != null) return is;
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public void scanModResources(String resourcePath, BiConsumer<String, InputStream> handler) {
        Set<String> alreadyHandled = new HashSet<>();

        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            String modId = mod.getMetadata().getId();
            try {
                Optional<Path> path = mod.findPath(resourcePath);
                if (path.isPresent() && Files.exists(path.get())) {
                    try (InputStream is = Files.newInputStream(path.get())) {
                        handler.accept(modId, is);
                        alreadyHandled.add(modId);
                    }
                }
            } catch (Exception e) {

            }
        }

        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(resourcePath);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();

                String urlStr = url.toString();
                boolean alreadyFound = false;
                for (String handledId : alreadyHandled) {
                    if (urlStr.contains(handledId)) {
                        alreadyFound = true;
                        break;
                    }
                }
                if (!alreadyFound) {
                    try (InputStream is = url.openStream()) {
                        handler.accept("ores", is);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public Set<String> findTrimPatterns() {
        Set<String> patterns = new HashSet<>();

        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            String namespace = mod.getMetadata().getId();
            if (namespace.equals("minecraft")) continue;

            mod.findPath("assets/" + namespace + "/textures/trims/entity/humanoid/").ifPresent(path -> {
                try (Stream<Path> stream = Files.list(path)) {
                    stream.forEach(p -> {
                        String filename = p.getFileName().toString();
                        if (filename.endsWith(".png")) {
                            patterns.add(namespace + ":" + filename.substring(0, filename.length() - 4));
                        }
                    });
                } catch (Exception e) {

                }
            });
        }

        return patterns;
    }
}
