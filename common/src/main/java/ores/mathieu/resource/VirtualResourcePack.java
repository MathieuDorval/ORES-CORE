//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: In-memory resource pack implementation for dynamic assets.
//
// Author: __mathieu
// Version: 26.1.001
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import ores.mathieu.OresCoreCommon;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("null")
public class VirtualResourcePack implements PackResources {
    public static final VirtualResourcePack CLIENT = new VirtualResourcePack("generated_resources", getPackFormat(PackType.CLIENT_RESOURCES, 81));
    public static final VirtualResourcePack SERVER = new VirtualResourcePack("generated_data", getPackFormat(PackType.SERVER_DATA, 99));

    private static int getPackFormat(PackType type, int fallback) {
        try {
            Object worldVersion = net.minecraft.SharedConstants.getCurrentVersion();

            try {
                java.lang.reflect.Method m = worldVersion.getClass().getMethod("getPackVersion", PackType.class);
                int result = (int) m.invoke(worldVersion, type);
                OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Dynamically detected pack format for {}: {}", type, result);
                return result;
            } catch (NoSuchMethodException e) {

                for (java.lang.reflect.Method m : worldVersion.getClass().getMethods()) {
                    if (m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(PackType.class) && m.getReturnType() == int.class) {
                        int result = (int) m.invoke(worldVersion, type);
                        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Dynamically detected pack format for {} (via fuzzy match): {}", type, result);
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            OresCoreCommon.LOGGER.warn("[ORES CORE WARNING] Could not dynamically detect pack format for {}, using fallback {}: {}", type, fallback, e.getMessage());
        }
        return fallback;
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final String name;
    private final int format;
    private final Map<String, byte[]> resources = new HashMap<>();

    public VirtualResourcePack(String name, int format) {
        this.name = name;
        this.format = format;
    }

    public void addJson(String path, JsonObject json) {
        byte[] data = GSON.toJson(json).getBytes(StandardCharsets.UTF_8);
        resources.put(path, data);
        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] VirtualPack added: {}", path);
    }

    public void addBytes(String path, byte[] data) {
        resources.put(path, data);
    }

    public byte[] getBytes(String path) {
        return resources.get(path);
    }

    @Override
    public IoSupplier<InputStream> getRootResource(String... elements) {
        String path = String.join("/", elements);
        byte[] data = resources.get(path);
        return data != null ? () -> new ByteArrayInputStream(data) : null;
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType packType, Identifier identifier) {
        String path = packType.getDirectory() + "/" + identifier.getNamespace() + "/" + identifier.getPath();
        byte[] data = resources.get(path);
        return data != null ? () -> new ByteArrayInputStream(data) : null;
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {

        String prefix = packType.getDirectory() + "/" + namespace + "/" + path + "/";
        for (Map.Entry<String, byte[]> entry : resources.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(prefix)) {
                String subPath = key.substring((packType.getDirectory() + "/" + namespace + "/").length());
                byte[] data = entry.getValue();
                resourceOutput.accept(Identifier.fromNamespaceAndPath(namespace, subPath), () -> new ByteArrayInputStream(data));
            }
        }
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {

        Set<String> namespaces = new HashSet<>();
        String dir = packType.getDirectory() + "/";
        for (String key : resources.keySet()) {
            if (key.startsWith(dir)) {
                String afterDir = key.substring(dir.length());
                int slashIdx = afterDir.indexOf('/');
                if (slashIdx > 0) {
                    namespaces.add(afterDir.substring(0, slashIdx));
                }
            }
        }
        return namespaces;
    }

    @Override
    public <T> T getMetadataSection(MetadataSectionType<T> type) {
        if (type.name().equals("pack")) {
            JsonObject pack = new JsonObject();
            pack.addProperty("pack_format", this.format);
            pack.addProperty("min_format", this.format);
            pack.addProperty("max_format", this.format);
            pack.addProperty("description", "Ores Core Dynamic Resources (" + this.name + ")");
            return type.codec().parse(JsonOps.INSTANCE, pack).getOrThrow();
        }
        return null;
    }

    @Override
    public PackLocationInfo location() {
        return new PackLocationInfo(this.name, net.minecraft.network.chat.Component.literal("Ores Core Dynamic " + this.name), PackSource.BUILT_IN, Optional.empty());
    }

    @Override
    public String packId() {
        return this.name;
    }

    @Override
    public void close() {
    }
}
