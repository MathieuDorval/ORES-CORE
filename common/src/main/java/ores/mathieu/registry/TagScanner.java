//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Manages manual scanning of tags from resource packs.
//
// Author: __mathieu
// Version: 26.1.001
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.registry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ores.mathieu.OresCoreCommon;
import net.minecraft.resources.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@SuppressWarnings("null")
public class TagScanner {
    private static final Gson GSON = new Gson();

    private static final Map<Identifier, Set<Identifier>> TAG_CONTENTS = new HashMap<>();
    private static final Map<Identifier, Set<Identifier>> OBJECT_TO_TAGS = new HashMap<>();

    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;

        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] Starting manual tag scan for tool requirement and mining level tags...");
        long start = System.currentTimeMillis();

        Set<Identifier> toScan = new HashSet<>();
        toScan.add(Identifier.parse("minecraft:mineable/pickaxe"));
        toScan.add(Identifier.parse("minecraft:mineable/axe"));
        toScan.add(Identifier.parse("minecraft:mineable/shovel"));
        toScan.add(Identifier.parse("minecraft:mineable/hoe"));
        toScan.add(Identifier.parse("minecraft:needs_stone_tool"));
        toScan.add(Identifier.parse("minecraft:needs_iron_tool"));
        toScan.add(Identifier.parse("minecraft:needs_diamond_tool"));
        toScan.add(Identifier.parse("minecraft:needs_gold_tool"));
        toScan.add(Identifier.parse("fabric:needs_tool_level_4"));
        toScan.add(Identifier.parse("neoforge:needs_netherite_tool"));
        toScan.add(Identifier.parse("fabric:needs_tool_level_5"));
        toScan.add(Identifier.parse("neoforge:needs_tool_level_5"));

        Queue<Identifier> queue = new ArrayDeque<>(toScan);
        Set<Identifier> scanned = new HashSet<>();

        while (!queue.isEmpty()) {
            Identifier tagLoc = queue.poll();
            if (!scanned.add(tagLoc)) continue;

            scanTag(tagLoc, queue);
        }

        for (Map.Entry<Identifier, Set<Identifier>> entry : TAG_CONTENTS.entrySet()) {
            Identifier tag = entry.getKey();
            for (Identifier obj : entry.getValue()) {
                OBJECT_TO_TAGS.computeIfAbsent(obj, k -> new HashSet<>()).add(tag);
            }
        }

        resolveDigraph();

        OresCoreCommon.LOGGER.debug("[ORES CORE DEBUG] TagScanner scanned {} tags in {}ms. Found tags for {} blocks.", scanned.size(), System.currentTimeMillis() - start, OBJECT_TO_TAGS.size());

        initialized = true;
    }

    private static void resolveDigraph() {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Map.Entry<Identifier, Set<Identifier>> entry : TAG_CONTENTS.entrySet()) {
                Set<Identifier> contents = entry.getValue();

                Set<Identifier> additions = new HashSet<>();
                for (Identifier item : contents) {
                    if (TAG_CONTENTS.containsKey(item)) {
                        for (Identifier subItem : TAG_CONTENTS.get(item)) {
                            if (!contents.contains(subItem)) {
                                additions.add(subItem);
                            }
                        }
                    }
                }

                if (!additions.isEmpty()) {
                    contents.addAll(additions);
                    changed = true;
                }
            }
        }

        OBJECT_TO_TAGS.clear();
        for (Map.Entry<Identifier, Set<Identifier>> entry : TAG_CONTENTS.entrySet()) {
            Identifier tag = entry.getKey();
            for (Identifier obj : entry.getValue()) {
                OBJECT_TO_TAGS.computeIfAbsent(obj, k -> new HashSet<>()).add(tag);
            }
        }
    }

    private static void scanTag(Identifier tagLoc, Queue<Identifier> queue) {
        String path = "data/" + tagLoc.getNamespace() + "/tags/block/" + tagLoc.getPath() + ".json";

        try {
            InputStream stream = ores.mathieu.resource.TextureGenerator.getResourceAsStream(path);
            if (stream != null) {
                try (InputStreamReader reader = new InputStreamReader(stream)) {
                    JsonObject json = GSON.fromJson(reader, JsonObject.class);
                    if (json.has("values")) {
                        JsonArray values = json.getAsJsonArray("values");
                        for (JsonElement element : values) {
                            String val = element.getAsString();
                            boolean isTag = val.startsWith("#");
                            if (isTag) val = val.substring(1);

                            Identifier valLoc = Identifier.parse(val);

                            TAG_CONTENTS.computeIfAbsent(tagLoc, k -> new HashSet<>()).add(valLoc);

                            if (isTag) {
                                queue.add(valLoc);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            OresCoreCommon.LOGGER.warn("[ORES CORE WARNING] TagScanner failed to parse {}", path);
        }
    }

    public static boolean hasTag(Identifier blockLoc, Identifier tagLoc) {
        if (!initialized) init();
        Set<Identifier> tags = OBJECT_TO_TAGS.get(blockLoc);
        return tags != null && tags.contains(tagLoc);
    }
}
