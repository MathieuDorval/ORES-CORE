//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Central orchestrator mapping active ores and combinatorial 
// items into dynamically generated virtual textures, blockstates, 
// and JSON models seamlessly via VirtualResourcePack.
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ores.mathieu.OresCoreCommon;
import ores.mathieu.registry.DiscoveryManager;

@SuppressWarnings("null")
public class DynamicResourceGenerator {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String LANG_PATH = "assets/ores/lang/";
    private static int tagsAdded = 0;
    private static final java.util.Map<String, java.util.Set<String>> tagBuffer = new java.util.HashMap<>();

    public static void generateResources() {
        long startTime = System.currentTimeMillis();
        OresCoreCommon.LOGGER.info("[ORES CORE] Starting dynamic resource generation...");
        tagsAdded = 0;

        try {
            loadVanillaLang();

            java.util.Map<String, JsonObject> langJsons = new java.util.HashMap<>();
            for (String lang : SUPPORTED_LANGS) {
                langJsons.put(lang, new JsonObject());
            }

            langJsons.get("en_us").addProperty("itemGroup.ores.items",  "Ores Items");
            langJsons.get("en_us").addProperty("itemGroup.ores.blocks", "Ores Blocks");
            langJsons.get("en_us").addProperty("itemGroup.ores.ores",   "Ores");

            langJsons.get("fr_fr").addProperty("itemGroup.ores.items",  "Ores Items");
            langJsons.get("fr_fr").addProperty("itemGroup.ores.blocks", "Ores Blocs");
            langJsons.get("fr_fr").addProperty("itemGroup.ores.ores",   "Minerais");

            langJsons.get("es_es").addProperty("itemGroup.ores.items",  "Ores Items");
            langJsons.get("es_es").addProperty("itemGroup.ores.blocks", "Ores Bloques");
            langJsons.get("es_es").addProperty("itemGroup.ores.ores",   "Minerales");

            langJsons.get("it_it").addProperty("itemGroup.ores.items",  "Ores Items");
            langJsons.get("it_it").addProperty("itemGroup.ores.blocks", "Ores Blocchi");
            langJsons.get("it_it").addProperty("itemGroup.ores.ores",   "Minerali");

            langJsons.get("de_de").addProperty("itemGroup.ores.items",  "Ores Items");
            langJsons.get("de_de").addProperty("itemGroup.ores.blocks", "Ores Blöcke");
            langJsons.get("de_de").addProperty("itemGroup.ores.ores",   "Erze");

            long objectsStartTime = System.currentTimeMillis();
            for (ores.mathieu.registry.RegistryDecoder.ResolvedObject obj : DiscoveryManager.DECODED_DATA.objects) {
                if (isVanillaObject(obj)) continue;

                String material = obj.registryName;
                net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, material);
                if (obj.type instanceof ores.mathieu.material.BlockType) {
                    if (!ores.mathieu.registry.RegistrationHandler.QUEUED_BLOCKS.contains(id) && !net.minecraft.core.registries.BuiltInRegistries.BLOCK.containsKey(id)) continue;
                } else {
                    if (!ores.mathieu.registry.RegistrationHandler.QUEUED_ITEMS.contains(id) && !net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(id)) continue;
                }
                if (obj.type instanceof ores.mathieu.material.ItemType) {
                    TextureGenerator.generateItemTexture(material, obj.material, (ores.mathieu.material.ItemType) obj.type);
                    generateItemModel(material, obj.material);
                    addLangEntries(langJsons, material, obj.type);
                    generateTags(material, false, obj.material, obj.type);
                } else if (obj.type instanceof ores.mathieu.material.BlockType) {
                    if (obj.type != ores.mathieu.material.BlockType.ORE) {
                        TextureGenerator.generateBlockTexture(material, obj.material, (ores.mathieu.material.BlockType) obj.type);
                    }

                    if (obj.type == ores.mathieu.material.BlockType.ORE) continue;
                    boolean isTranslucent = ((ores.mathieu.material.BlockType) obj.type).getDefaultOverride().getTranslucent() == Boolean.TRUE;
                    generateBlockStateAndModel(material, obj.material, isTranslucent);
                    addLangEntries(langJsons, material, obj.type);
                    generateBlockLootTable(material);
                    generateTags(material, true, obj.material, obj.type);
                } else if (obj.type instanceof ores.mathieu.material.ChemicalType) {
                    addLangEntries(langJsons, material, obj.type);
                    TextureGenerator.generateChemicalAssets(material, obj.material, (ores.mathieu.material.ChemicalType) obj.type);
                    generateTags(material, false, obj.material, obj.type);
                }
            }
            long objectsEndTime = System.currentTimeMillis();
            
            long oresStartTime = System.currentTimeMillis();
            for (ores.mathieu.registry.RegistryDecoder.ResolvedOre ore : DiscoveryManager.DECODED_DATA.ores) {
                if (ore.material.getName().equals("netherite")) continue;
                String stoneName = ore.stoneReplacement;

                if (stoneName.equals("minecraft:ice")) continue;

                String stonePath = stoneName.contains(":") ? stoneName.split(":")[1] : stoneName;
                String stoneNamespace = stoneName.contains(":") ? stoneName.split(":")[0] : "minecraft";
                String oreName = stonePath + "_" + ore.material.getName() + "_ore";

                net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, oreName);
                if (!ores.mathieu.registry.RegistrationHandler.QUEUED_BLOCKS.contains(id) && !net.minecraft.core.registries.BuiltInRegistries.BLOCK.containsKey(id)) {
                    continue;
                }

                TextureGenerator.generateOreOverlayTexture(ore.material.getName(), ore.material);
                TextureGenerator.generateOreAssets(stoneName, oreName, ore.material.getName());
                addOreLangEntries(langJsons, oreName, stoneNamespace, stonePath, ore.material.getName());
                generateOreLootTable(oreName, ore.material);
                generateOreTags(oreName, ore.material, stoneName);
            }
            long oresEndTime = System.currentTimeMillis();

            long trimsStartTime = System.currentTimeMillis();
            java.util.Set<String> processedTrimMaterials = new java.util.HashSet<>();
            java.util.List<String> moddedTrimSuffixes = new java.util.ArrayList<>();
            java.util.Set<String> vanillaTrimNames = java.util.Set.of(
                "iron", "gold", "copper", "diamond", "emerald", "lapis", "redstone", "quartz", "netherite",
                "amethyst", "resin"
            );
            
            for (ores.mathieu.registry.RegistryDecoder.ResolvedObject trimObj : DiscoveryManager.DECODED_DATA.objects) {
                ores.mathieu.material.Material mat = trimObj.material;
                if (mat == null) continue;

                boolean shouldRegisterTrimResource = false;
                if (mat.isTrimmable()) {
                    if (trimObj.type instanceof ores.mathieu.material.ItemType itemType) {
                        Boolean canBeTrimmable = itemType.getDefaultOverride().getCanBeTrimmable();
                        if (Boolean.TRUE.equals(canBeTrimmable)) {
                            shouldRegisterTrimResource = true;
                        }
                    }
                }

                if (shouldRegisterTrimResource) {
                    if (!processedTrimMaterials.contains(mat.getName())) {
                        processedTrimMaterials.add(mat.getName());

                        if (!vanillaTrimNames.contains(mat.getName())) {
                            generateTrimMaterialResource(mat, langJsons);
                            moddedTrimSuffixes.add(mat.getName());
                            TextureGenerator.generateTrimPalette(mat);
                        }
                    }

                    String itemId = (isVanillaObject(trimObj) ? "minecraft:" : "ores:") + trimObj.registryName;
                    createTagFile("items", "c", "trim_materials/" + mat.getName(), itemId);
                    createTagFile("items", "minecraft", "trim_materials", "#c:trim_materials/" + mat.getName());
                }
            }

            if (!moddedTrimSuffixes.isEmpty()) {
                java.util.Set<String> allPatterns = findAllTrimPatterns();
                generateTrimAtlas(moddedTrimSuffixes, allPatterns);
            }
            long trimsEndTime = System.currentTimeMillis();

            
            addTooltipTranslations(langJsons);

            long featuresStartTime = System.currentTimeMillis();
            generateWorldGenFeatures();
            long featuresEndTime = System.currentTimeMillis();
            
            long recipesStartTime = System.currentTimeMillis();
            generateRecipes();
            long recipesEndTime = System.currentTimeMillis();

            for (String lang : SUPPORTED_LANGS) {
                VirtualResourcePack.CLIENT.addJson(LANG_PATH + lang + ".json", langJsons.get(lang));
            }

            long tagsStartTime = System.currentTimeMillis();
            finalizeTags();
            long tagsEndTime = System.currentTimeMillis();

            long totalEndTime = System.currentTimeMillis();

            OresCoreCommon.LOGGER.info("[ORES CORE] --- Dynamic Resource Generation Summary ---");
            OresCoreCommon.LOGGER.info("[ORES CORE] Standard Objects (Textures/Models/Tags): {} ms", (objectsEndTime - objectsStartTime));
            OresCoreCommon.LOGGER.info("[ORES CORE] Combinatorial Ores (Textures/Models/Tags): {} ms", (oresEndTime - oresStartTime));
            OresCoreCommon.LOGGER.info("[ORES CORE] Trim Assets: {} ms", (trimsEndTime - trimsStartTime));
            OresCoreCommon.LOGGER.info("[ORES CORE] WorldGen Features: {} ms", (featuresEndTime - featuresStartTime));
            OresCoreCommon.LOGGER.info("[ORES CORE] Recipes: {} ms", (recipesEndTime - recipesStartTime));
            OresCoreCommon.LOGGER.info("[ORES CORE] Tags Finalization: {} ms", (tagsEndTime - tagsStartTime));
            OresCoreCommon.LOGGER.info("[ORES CORE] Total Generation Time: {} ms", (totalEndTime - startTime));
            OresCoreCommon.LOGGER.info("[ORES CORE] ---------------------------------------------");

            OresCoreCommon.LOGGER.debug("[ORES CORE] Successfully populated VirtualResourcePack in-memory.");
        } catch (Exception e) {
            OresCoreCommon.LOGGER.error("[ORES CORE] Failed to generate virtual resources", e);
        }
    }


    private static boolean isVanillaObject(ores.mathieu.registry.RegistryDecoder.ResolvedObject obj) {
        return ores.mathieu.registry.RegistrationHandler.VANILLA_ITEMS.contains(obj.registryName);
    }

    private static void generateOreTags(String oreName, ores.mathieu.material.Material matProps, String baseBlockId) {
        String matName = matProps.getName();

        createTagFile("blocks", "c", "ores/" + matName, "ores:" + oreName);
        createTagFile("items", "c", "ores/" + matName, "ores:" + oreName);

        createTagFile("blocks", "c", "ores", "#c:ores/" + matName);
        createTagFile("items", "c", "ores", "#c:ores/" + matName);

        applyMiningTags(oreName, matProps, baseBlockId);
    }

    private static void applyMiningTags(String name, ores.mathieu.material.Material matProps, String baseBlockId) {
        String toolTag = "mineable/pickaxe";
        int baseBlockLevel = 0;

        if (baseBlockId != null) {
            net.minecraft.resources.Identifier baseId = net.minecraft.resources.Identifier.tryParse(baseBlockId);

            if (baseId != null) {
                if (ores.mathieu.registry.TagScanner.hasTag(baseId, net.minecraft.resources.Identifier.parse("minecraft:mineable/shovel"))) {
                    toolTag = "mineable/shovel";
                } else if (ores.mathieu.registry.TagScanner.hasTag(baseId, net.minecraft.resources.Identifier.parse("minecraft:mineable/axe"))) {
                    toolTag = "mineable/axe";
                } else if (ores.mathieu.registry.TagScanner.hasTag(baseId, net.minecraft.resources.Identifier.parse("minecraft:mineable/hoe"))) {
                    toolTag = "mineable/hoe";
                } else if (ores.mathieu.registry.TagScanner.hasTag(baseId, net.minecraft.resources.Identifier.parse("minecraft:mineable/pickaxe"))) {
                    toolTag = "mineable/pickaxe";
                } else {
                    String blockPath = baseBlockId.contains(":") ? baseBlockId.split(":")[1] : baseBlockId;
                    if (blockPath.contains("sand") || blockPath.contains("gravel") || blockPath.contains("dirt") || blockPath.contains("clay")) {
                        toolTag = "mineable/shovel";
                    } else if (blockPath.contains("wood") || blockPath.contains("log") || blockPath.contains("planks")) {
                        toolTag = "mineable/axe";
                    }
                }

                if (ores.mathieu.registry.TagScanner.hasTag(baseId, net.minecraft.resources.Identifier.parse("minecraft:needs_diamond_tool"))) {
                    baseBlockLevel = 3;
                } else if (ores.mathieu.registry.TagScanner.hasTag(baseId, net.minecraft.resources.Identifier.parse("minecraft:needs_iron_tool"))) {
                    baseBlockLevel = 2;
                } else if (ores.mathieu.registry.TagScanner.hasTag(baseId, net.minecraft.resources.Identifier.parse("minecraft:needs_stone_tool"))) {
                    baseBlockLevel = 1;
                }
            }
        } else {
            if (name.contains("dirt") || name.contains("sand") || name.contains("gravel") || name.contains("clay")) {
                toolTag = "mineable/shovel";
            } else if (name.contains("wood") || name.contains("log") || name.contains("planks")) {
                toolTag = "mineable/axe";
            }
        }
        createTagFile("block", "minecraft", toolTag, "ores:" + name);

        int level = Math.max(baseBlockLevel, matProps.getMiningLevel());
        if (level >= 1) createTagFile("block", "minecraft", "needs_stone_tool", "ores:" + name);
        if (level >= 2) createTagFile("block", "minecraft", "needs_iron_tool", "ores:" + name);
        if (level >= 3) createTagFile("block", "minecraft", "needs_diamond_tool", "ores:" + name);
        if (level >= 4) {
            createTagFile("block", "fabric", "needs_tool_level_4", "ores:" + name);
            createTagFile("block", "neoforge", "needs_netherite_tool", "ores:" + name);
        }
        if (level >= 5) {
            createTagFile("block", "fabric", "needs_tool_level_5", "ores:" + name);
            createTagFile("block", "neoforge", "needs_tool_level_5", "ores:" + name);
        }
    }

    private static void generateTags(String name, boolean isBlock, ores.mathieu.material.Material matProps, Enum<?> itemTypeHint) {
        if (matProps == null) return;
        String matName = matProps.getName();

        String tagGroup = null;
        if (itemTypeHint instanceof ores.mathieu.material.ItemType) {
            tagGroup = ((ores.mathieu.material.ItemType) itemTypeHint).getDefaultOverride().getTagCategory();
        } else if (itemTypeHint instanceof ores.mathieu.material.BlockType) {
            tagGroup = ((ores.mathieu.material.BlockType) itemTypeHint).getDefaultOverride().getTagCategory();
        } else if (itemTypeHint instanceof ores.mathieu.material.ChemicalType) {
            tagGroup = ((ores.mathieu.material.ChemicalType) itemTypeHint).getDefaultOverride().getTagCategory();
        }

        if (tagGroup == null) {
            if (name.endsWith("_bucket")) tagGroup = "buckets";
            else if (name.endsWith("_ore")) tagGroup = "ores";
            else tagGroup = name.contains("_") ? name.substring(name.lastIndexOf('_') + 1) + "s" : "misc";
        }

        boolean isVanilla = ores.mathieu.registry.RegistrationHandler.VANILLA_ITEMS.contains(name);
        String fullId = (isVanilla ? "minecraft:" : "ores:") + name;

        String tPattern = "%s";
        if (itemTypeHint instanceof ores.mathieu.material.ItemType it) tPattern = it.getDefaultOverride().getTagPattern();
        else if (itemTypeHint instanceof ores.mathieu.material.BlockType bt) tPattern = bt.getDefaultOverride().getTagPattern();
        else if (itemTypeHint instanceof ores.mathieu.material.ChemicalType ct) tPattern = ct.getDefaultOverride().getTagPattern();
        
        String tagSuffix = tPattern.replace("%s", matName);

        createTagFile("items", "c", tagGroup + "/" + tagSuffix, fullId);
        createTagFile("items", "c", tagGroup, "#c:" + tagGroup + "/" + tagSuffix);

        if (tagGroup.equals("crushed_raw_materials") && ores.mathieu.compat.CreateCompat.isCreateLoaded()) {
            createTagFile("items", "create", tagGroup + "/" + tagSuffix, fullId);
            createTagFile("items", "create", tagGroup, "#create:" + tagGroup + "/" + tagSuffix);
        }

        if (isBlock) {
            createTagFile("blocks", "c", tagGroup + "/" + tagSuffix, fullId);
            createTagFile("blocks", "c", tagGroup, "#c:" + tagGroup + "/" + tagSuffix);
            applyMiningTags(name, matProps, null);

            if (itemTypeHint instanceof ores.mathieu.material.BlockType blockType) {
                Boolean isBeaconBase = blockType.getDefaultOverride().getCanBeBeaconBase();
                if (isBeaconBase != null && isBeaconBase) {
                    createTagFile("blocks", "minecraft", "beacon_base_blocks", fullId);
                }
            }
        }

        boolean isBeacon = false;
        if (matProps.isBeacon()) isBeacon = true;
        if (itemTypeHint instanceof ores.mathieu.material.ItemType it && Boolean.TRUE.equals(it.getDefaultOverride().getCanBeBeaconPayment())) isBeacon = true;

        if (!isBlock && isBeacon && (itemTypeHint == ores.mathieu.material.ItemType.INGOT || itemTypeHint == ores.mathieu.material.ItemType.GEM)) {
            createTagFile("items", "minecraft", "beacon_payment_items", fullId);
        }
    }

    private static void createTagFile(String type, String namespace, String path, String entry) {
        String singularType = type.trim();
        if (singularType.endsWith("s")) singularType = singularType.substring(0, singularType.length() - 1);
        

        String tagKey = String.format("%s:%s/%s", namespace, singularType, path);
        tagBuffer.computeIfAbsent(tagKey, k -> new java.util.LinkedHashSet<>()).add(entry);
        tagsAdded++;
    }

    private static void finalizeTags() {
        OresCoreCommon.LOGGER.debug("[ORES CORE] Finalizing {} cached tag entries...", tagsAdded);
        for (java.util.Map.Entry<String, java.util.Set<String>> entry : tagBuffer.entrySet()) {
            String tagKey = entry.getKey();
            java.util.Set<String> entriesToAdd = entry.getValue();

            String[] parts = tagKey.split(":", 2);
            String namespace = parts[0];
            String fullPath = parts[1];
            String type = fullPath.substring(0, fullPath.indexOf('/'));
            String tagSubPath = fullPath.substring(fullPath.indexOf('/') + 1);

            String tagPath = String.format("data/%s/tags/%s/%s.json", namespace, type, tagSubPath);
            JsonObject json = new JsonObject();
            json.addProperty("replace", false);
            JsonArray values = new JsonArray();

            try {
                net.minecraft.server.packs.resources.IoSupplier<java.io.InputStream> stream = VirtualResourcePack.SERVER.getResource(net.minecraft.server.packs.PackType.SERVER_DATA, net.minecraft.resources.Identifier.fromNamespaceAndPath(namespace, "tags/" + type + "/" + tagSubPath + ".json"));
                if (stream != null) {
                    try (java.io.InputStreamReader reader = new java.io.InputStreamReader(stream.get())) {
                        JsonObject existing = GSON.fromJson(reader, JsonObject.class);
                        if (existing != null && existing.has("values")) {
                            JsonArray existingValues = existing.getAsJsonArray("values");
                            java.util.Set<String> uniqueEntries = new java.util.LinkedHashSet<>();
                            for (int i = 0; i < existingValues.size(); i++) uniqueEntries.add(existingValues.get(i).getAsString());
                            uniqueEntries.addAll(entriesToAdd);
                            for (String val : uniqueEntries) values.add(val);
                        } else {
                            for (String val : entriesToAdd) values.add(val);
                        }
                    }
                } else {
                    for (String val : entriesToAdd) values.add(val);
                }
            } catch (Exception e) {
                for (String val : entriesToAdd) values.add(val);
            }

            json.add("values", values);
            VirtualResourcePack.SERVER.addJson(tagPath, json);
        }
        tagBuffer.clear();
    }

    private static void generateTrimMaterialResource(ores.mathieu.material.Material mat, java.util.Map<String, JsonObject> langJsons) {
        String matName = mat.getName();

        String colorHex = String.format("#%06X", mat.getMaterialColor() & 0xFFFFFF);

        JsonObject trimJson = new JsonObject();
        trimJson.addProperty("asset_name", matName);

        JsonObject description = new JsonObject();
        description.addProperty("color", colorHex);
        description.addProperty("translate", "trim_material.ores." + matName);
        trimJson.add("description", description);

        String trimPath = "data/ores/trim_material/" + matName + ".json";
        VirtualResourcePack.SERVER.addJson(trimPath, trimJson);

        java.util.Set<String> vanillaTrimMaterials = java.util.Set.of(
            "iron", "gold", "copper", "diamond", "emerald", "lapis", "redstone", "quartz", "netherite", "amethyst", "resin"
        );
        if (!vanillaTrimMaterials.contains(matName)) {
            for (String lang : SUPPORTED_LANGS) {
                String matNameInLang = getMaterialName(mat, lang);
                description = new JsonObject();
                String suffix;
                if (lang.equals("fr_fr")) suffix = " Matériau";
                else if (lang.equals("es_es")) suffix = " Material";
                else if (lang.equals("it_it")) suffix = " Materiale";
                else if (lang.equals("de_de")) suffix = " Material";
                else suffix = " Material";

                if (langJsons.containsKey(lang)) {
                    langJsons.get(lang).addProperty("trim_material.ores." + matName, matNameInLang + suffix);
                }
            }
        }

        OresCoreCommon.LOGGER.debug("[ORES CORE] Generated trim material resource: {}", matName);
    }

    private static void generateItemModel(String name, ores.mathieu.material.Material matProps) {

        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "ores:item/" + name);
        json.add("textures", textures);
        VirtualResourcePack.CLIENT.addJson("assets/ores/models/item/" + name + ".json", json);

        JsonObject itemDef = new JsonObject();
        JsonObject modelDef = new JsonObject();
        modelDef.addProperty("type", "minecraft:model");
        modelDef.addProperty("model", "ores:item/" + name);
        itemDef.add("model", modelDef);
        VirtualResourcePack.CLIENT.addJson("assets/ores/items/" + name + ".json", itemDef);
    }

    private static void generateBlockStateAndModel(String name, ores.mathieu.material.Material matProps, boolean isTranslucent) {

        JsonObject stateJson = new JsonObject();
        JsonObject variants = new JsonObject();
        JsonObject modelObj = new JsonObject();
        modelObj.addProperty("model", "ores:block/" + name);
        variants.add("", modelObj);
        stateJson.add("variants", variants);
        VirtualResourcePack.CLIENT.addJson("assets/ores/blockstates/" + name + ".json", stateJson);

        JsonObject modelJson = new JsonObject();
        modelJson.addProperty("parent", "minecraft:block/cube_all");
        if (isTranslucent) {
            modelJson.addProperty("render_type", "minecraft:translucent");
        }
        JsonObject textures = new JsonObject();
        textures.addProperty("all", "ores:block/" + name);
        modelJson.add("textures", textures);
        VirtualResourcePack.CLIENT.addJson("assets/ores/models/block/" + name + ".json", modelJson);

        JsonObject itemModelJson = new JsonObject();
        itemModelJson.addProperty("parent", "ores:block/" + name);
        VirtualResourcePack.CLIENT.addJson("assets/ores/models/item/" + name + ".json", itemModelJson);

        JsonObject itemDef = new JsonObject();
        JsonObject modelDef = new JsonObject();
        modelDef.addProperty("type", "minecraft:model");
        modelDef.addProperty("model", "ores:item/" + name);
        itemDef.add("model", modelDef);
        VirtualResourcePack.CLIENT.addJson("assets/ores/items/" + name + ".json", itemDef);
    }

    private static void generateBlockLootTable(String name) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:block");
        JsonArray pools = new JsonArray();
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1);
        JsonArray entries = new JsonArray();
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", "ores:" + name);
        entries.add(entry);
        pool.add("entries", entries);
        JsonArray conditions = new JsonArray();
        JsonObject condition = new JsonObject();
        condition.addProperty("condition", "minecraft:survives_explosion");
        conditions.add(condition);
        pool.add("conditions", conditions);
        pools.add(pool);
        json.add("pools", pools);

        VirtualResourcePack.SERVER.addJson("data/ores/loot_table/blocks/" + name + ".json", json);
    }

    private static void generateOreLootTable(String oreName, ores.mathieu.material.Material matProps) {
        String dropItem = matProps.getOreDropItem();
        String baseType = matProps.getBaseType();
        String matName = matProps.getName();
        if (dropItem == null || dropItem.isEmpty()) {

            if (baseType.equals("ingot")) dropItem = getEffectiveId(matName, "raw");
            else if (baseType.equals("gem")) dropItem = getEffectiveId(matName, "gem");
            else if (baseType.equals("dust")) dropItem = getEffectiveId(matName, "dust");
            else if (baseType.equals("self")) dropItem = getEffectiveId(matName, "self");
            else dropItem = "ores:" + oreName;
        } else if (!dropItem.contains(":")) {

            String dItem = dropItem;
            if (dItem.equals("raw_" + matName)) dropItem = getEffectiveId(matName, "raw");
            else if (dItem.equals(matName + "_ingot")) dropItem = getEffectiveId(matName, "ingot");
            else if (dItem.equals(matName + "_nugget")) dropItem = getEffectiveId(matName, "nugget");
            else if (dItem.equals(matName + "_block")) dropItem = getEffectiveId(matName, "block");
            else dropItem = "ores:" + dItem;
        }

        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:block");
        JsonArray pools = new JsonArray();

        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1);
        JsonArray entries = new JsonArray();

        JsonObject silkTouchEntry = new JsonObject();
        silkTouchEntry.addProperty("type", "minecraft:item");
        silkTouchEntry.addProperty("name", "ores:" + oreName);

        JsonArray silkConditions = new JsonArray();
        JsonObject matchTool = new JsonObject();
        matchTool.addProperty("condition", "minecraft:match_tool");
        JsonObject predicate = new JsonObject();
        JsonObject predicates = new JsonObject();
        JsonArray enchantments = new JsonArray();
        JsonObject silkEnchant = new JsonObject();
        silkEnchant.addProperty("enchantment", "minecraft:silk_touch");
        JsonObject levels = new JsonObject();
        levels.addProperty("min", 1);
        silkEnchant.add("levels", levels);
        enchantments.add(silkEnchant);
        predicates.add("minecraft:enchantments", enchantments);
        predicate.add("predicates", predicates);
        matchTool.add("predicate", predicate);
        silkConditions.add(matchTool);
        silkTouchEntry.add("conditions", silkConditions);

        JsonObject normalEntry = new JsonObject();
        normalEntry.addProperty("type", "minecraft:item");
        normalEntry.addProperty("name", dropItem);

        JsonArray functions = new JsonArray();

        JsonObject setCount = new JsonObject();
        setCount.addProperty("function", "minecraft:set_count");
        JsonObject count = new JsonObject();
        count.addProperty("type", "minecraft:uniform");
        count.addProperty("min", matProps.getOreDropMin());
        count.addProperty("max", matProps.getOreDropMax());
        setCount.add("count", count);
        setCount.addProperty("add", false);
        functions.add(setCount);

        JsonObject applyBonus = new JsonObject();
        applyBonus.addProperty("function", "minecraft:apply_bonus");
        applyBonus.addProperty("enchantment", "minecraft:fortune");
        applyBonus.addProperty("formula", "minecraft:ore_drops");
        functions.add(applyBonus);

        JsonObject explosionDecay = new JsonObject();
        explosionDecay.addProperty("function", "minecraft:explosion_decay");
        functions.add(explosionDecay);

        normalEntry.add("functions", functions);

        JsonObject groupEntry = new JsonObject();
        groupEntry.addProperty("type", "minecraft:alternatives");
        JsonArray children = new JsonArray();
        children.add(silkTouchEntry);
        children.add(normalEntry);
        groupEntry.add("children", children);

        entries.add(groupEntry);
        pool.add("entries", entries);
        pools.add(pool);
        json.add("pools", pools);

        VirtualResourcePack.SERVER.addJson("data/ores/loot_table/blocks/" + oreName + ".json", json);
    }

    private static final java.util.Map<String, JsonObject> VANILLA_LANGS = new java.util.HashMap<>();
    private static final String[] SUPPORTED_LANGS = {"en_us", "fr_fr", "es_es", "it_it", "de_de", "pt_br", "ru_ru", "zh_cn", "ja_jp"};

    private static void loadVanillaLang() {
        VANILLA_LANGS.clear();
        try {
            Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
            Object mcInstance = mcClass.getMethod("getInstance").invoke(null);
            if (mcInstance == null) return;

            Object rm = mcClass.getMethod("getResourceManager").invoke(mcInstance);
            if (rm == null) return;

            for (String lang : SUPPORTED_LANGS) {
                VANILLA_LANGS.put(lang, loadLangJson((net.minecraft.server.packs.resources.ResourceManager) rm, "minecraft", lang));
            }
        } catch (Throwable e) {

        }
    }


    private static JsonObject loadLangJson(net.minecraft.server.packs.resources.ResourceManager rm,
                                            String namespace, String lang) {
        try {
            net.minecraft.resources.Identifier loc = net.minecraft.resources.Identifier.fromNamespaceAndPath(namespace, "lang/" + lang + ".json");
            java.util.Optional<net.minecraft.server.packs.resources.Resource> opt = rm.getResource(loc);
            if (opt.isPresent()) {
                try (java.io.InputStream is = opt.get().open();
                     java.io.InputStreamReader reader = new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8)) {
                    return GSON.fromJson(reader, JsonObject.class);
                }
            }
        } catch (Exception e) {
            OresCoreCommon.LOGGER.warn("[ORES CORE] Failed to load lang {}/{}: {}", namespace, lang, e.getMessage());
        }
        return new JsonObject();
    }

    private static String lookupLang(String lang, String key, String fallback) {
        JsonObject json = VANILLA_LANGS.get(lang);
        if (json != null && json.has(key)) return json.get(key).getAsString();
        return fallback;
    }

    private static void addLangEntries(java.util.Map<String, JsonObject> langJsons, String name, Enum<?> typeHint) {
        java.util.Map<String, String> resolved = resolveNameTranslation(name);
        for (String lang : SUPPORTED_LANGS) {
            String key = "item.ores." + name;
            if (langJsons.containsKey(lang)) {
                langJsons.get(lang).addProperty(key, resolved.getOrDefault(lang, resolved.get("en_us")));
            }
        }
    }

    private static void addOreLangEntries(java.util.Map<String, JsonObject> langJsons, String oreName,
                                           String stoneNamespace, String stonePath, String matName) {
        String key = "item.ores." + oreName;
        ores.mathieu.material.Material mat = ores.mathieu.material.MaterialsDatabase.get(matName);
        String stoneKey = "block." + stoneNamespace + "." + stonePath;

        for (String lang : SUPPORTED_LANGS) {
            String matNameInLang = getMaterialName(mat, lang);
            String stoneEn = lookupLang("en_us", stoneKey, titleCase(stonePath));
            String stoneInLang = lookupLang(lang, stoneKey, stoneEn);

            String nameInLang;
            if (lang.equals("fr_fr")) {
                String elision = startsWithVowel(matNameInLang) ? "d'" : "de ";
                nameInLang = "Minerai " + elision + matNameInLang + " en " + stoneInLang;
            } else if (lang.equals("es_es")) {
                nameInLang = "Mineral de " + matNameInLang + " en " + stoneInLang;
            } else if (lang.equals("it_it")) {
                nameInLang = "Minerale di " + matNameInLang + " in " + stoneInLang;
            } else if (lang.equals("de_de")) {
                nameInLang = matNameInLang + "-Erz in " + stoneInLang;
            } else {
                nameInLang = stoneInLang + " " + matNameInLang + " Ore";
            }

            if (langJsons.containsKey(lang)) {
                langJsons.get(lang).addProperty(key, smartTitleCase(nameInLang));
            }
        }
    }

    private static String getMaterialName(ores.mathieu.material.Material mat, String lang) {
        if (mat == null) return "Unknown";
        return switch (lang) {
            case "fr_fr" -> mat.getNameFR();
            case "es_es" -> mat.getNameES();
            case "it_it" -> mat.getNameIT();
            case "de_de" -> mat.getNameDE();
            case "pt_br" -> mat.getNamePT();
            case "ru_ru" -> mat.getNameRU();
            case "zh_cn" -> mat.getNameZH();
            case "ja_jp" -> mat.getNameJP();
            default -> mat.getNameEN();
        };
    }

    private static java.util.Map<String, String> resolveNameTranslation(String name) {
        for (ores.mathieu.material.Material mat : ores.mathieu.material.MaterialsDatabase.MATERIALS.values()) {
            String matKey = mat.getName();

            for (ores.mathieu.material.ItemType it : ores.mathieu.material.ItemType.values()) {
                ores.mathieu.material.ItemOverride ov = it.getDefaultOverride();
                if (name.equals(ov.getNamingPattern().replace("%s", matKey))) {
                    return composeTranslation(mat, ov);
                }
            }

            for (ores.mathieu.material.ChemicalType ct : ores.mathieu.material.ChemicalType.values()) {
                ores.mathieu.material.ChemicalOverride ov = ct.getDefaultOverride();
                if (name.equals(ov.getNamingPattern().replace("%s", matKey))) {
                    return composeTranslation(mat, ov);
                }
            }

            for (ores.mathieu.material.BlockType bt : ores.mathieu.material.BlockType.values()) {
                if (bt == ores.mathieu.material.BlockType.ORE) continue;
                ores.mathieu.material.BlockOverride ov = bt.getDefaultOverride();
                if (name.equals(ov.getNamingPattern().replace("%s", matKey))) {
                    return composeTranslation(mat, ov);
                }
            }
        }

        java.util.Map<String, String> fallbacks = new java.util.HashMap<>();
        for (String lang : SUPPORTED_LANGS) fallbacks.put(lang, titleCase(name));
        return fallbacks;
    }

    private static java.util.Map<String, String> composeTranslation(ores.mathieu.material.Material mat, Object override) {
        java.util.Map<String, String> results = new java.util.HashMap<>();
        
        for (String lang : SUPPORTED_LANGS) {
            String matName = getMaterialName(mat, lang);
            String transPattern = getOverrideName(override, lang);
            
            if (transPattern == null) transPattern = "%s";
            
            String result = String.format(transPattern, matName);
            if (lang.equals("fr_fr") && startsWithVowel(matName)) {
                result = String.format(transPattern.replace("de %s", "d'%s"), matName);
            }
            results.put(lang, smartTitleCase(result));
        }
        
        return results;
    }

    private static String getOverrideName(Object override, String lang) {
        if (override instanceof ores.mathieu.material.ItemOverride it) {
            return switch (lang) {
                case "fr_fr" -> it.getNameFR();
                case "es_es" -> it.getNameES();
                case "it_it" -> it.getNameIT();
                case "de_de" -> it.getNameDE();
                case "pt_br" -> it.getNamePT();
                case "ru_ru" -> it.getNameRU();
                case "zh_cn" -> it.getNameZH();
                case "ja_jp" -> it.getNameJP();
                default -> it.getNameEN();
            };
        } else if (override instanceof ores.mathieu.material.ChemicalOverride co) {
            return switch (lang) {
                case "fr_fr" -> co.getNameFR();
                case "es_es" -> co.getNameES();
                case "it_it" -> co.getNameIT();
                case "de_de" -> co.getNameDE();
                case "pt_br" -> co.getNamePT();
                case "ru_ru" -> co.getNameRU();
                case "zh_cn" -> co.getNameZH();
                case "ja_jp" -> co.getNameJP();
                default -> co.getNameEN();
            };
        }
        return null;
    }


    private static String smartTitleCase(String s) {
        if (s == null || s.isEmpty()) return s;
        java.util.Set<String> LOWERCASE_WORDS = java.util.Set.of(
            "of", "de", "du", "des", "en", "a", "an", "the", "et", "with", "avec"
        );
        String[] words = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            if (w.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');

            String lower = w.toLowerCase();

            if (lower.startsWith("d'") && lower.length() > 2) {
                sb.append("d'").append(Character.toUpperCase(lower.charAt(2))).append(lower.substring(3));
            } else if (i > 0 && LOWERCASE_WORDS.contains(lower)) {
                sb.append(lower);
            } else {
                sb.append(Character.toUpperCase(lower.charAt(0))).append(lower.substring(1));
            }
        }
        return sb.toString();
    }


    private static boolean startsWithVowel(String word) {
        if (word == null || word.isEmpty()) return false;
        char c = Character.toLowerCase(word.charAt(0));
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'y';
    }

    private static String titleCase(String name) {
        StringBuilder sb = new StringBuilder();
        for (String part : name.split("_")) {
            if (part.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }

    private static String generateMekanismRecipe(String type, JsonObject input, JsonObject output) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "mekanism:" + type);
        json.add("input", input);
        json.add("output", output);
        return GSON.toJson(json);
    }

    private static String generateMekanismChemicalRecipe(String type, JsonObject itemInput, JsonObject chemicalInput, JsonObject output) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "mekanism:" + type);
        if (itemInput != null) json.add("item_input", itemInput);
        if (chemicalInput != null) json.add("chemical_input", chemicalInput);
        json.add("output", output);
        if (type.equals("purifying") || type.equals("injecting") || type.equals("dissolution")) {
            json.addProperty("per_tick_usage", true);
        }
        return GSON.toJson(json);
    }

    private static String generateMekanismSlurryRecipe(String type, String inputChemical, int inputAmt, String outputId, int outputAmt) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "mekanism:" + type);

        JsonObject chemicalInput = new JsonObject();
        chemicalInput.addProperty("amount", inputAmt);
        chemicalInput.addProperty("chemical", inputChemical);
        json.add("chemical_input", chemicalInput);

        if (type.equals("washing")) {
            JsonObject fluidInput = new JsonObject();
            fluidInput.addProperty("amount", 5);
            fluidInput.addProperty("tag", "minecraft:water");
            json.add("fluid_input", fluidInput);
        }

        JsonObject output = new JsonObject();
        output.addProperty("amount", outputAmt);
        output.addProperty("id", outputId);
        json.add("output", output);

        return GSON.toJson(json);
    }

    private static String generateMekanismCombiningRecipe(JsonObject mainInput, JsonObject extraInput, JsonObject output) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "mekanism:combining");
        json.add("main_input", mainInput);
        json.add("extra_input", extraInput);
        json.add("output", output);
        return GSON.toJson(json);
    }

    private static String generateSmeltingRecipe(String input, String output, float xp, int time, String type) {
        String recipeType = type.equals("blasting") ? "minecraft:blasting" : "minecraft:smelting";
        return "{\"type\":\"" + recipeType + "\",\"category\":\"misc\",\"ingredient\":\"" + input + "\",\"result\":{\"id\":\"" + output + "\"},\"experience\":" + xp + ",\"cookingtime\":" + time + "}";
    }

    private static String generateCreateRecipe(String type, JsonObject input, JsonArray results, Integer processingTime) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "create:" + type);
        JsonArray ingredients = new JsonArray();
        ingredients.add(input);
        json.add("ingredients", ingredients);
        if (processingTime != null) {
            json.addProperty("processing_time", processingTime);
        }
        json.add("results", results);
        return GSON.toJson(json);
    }

    private static String getCrushedHostBlock(String host) {
        if (host.equals("minecraft:stone")) return "minecraft:cobblestone";
        if (host.equals("minecraft:deepslate")) return "minecraft:cobbled_deepslate";
        
        String path = host.contains(":") ? host.split(":")[1] : host;
        String namespace = host.contains(":") ? host.split(":")[0] : "minecraft";

        net.minecraft.resources.Identifier cobbledId1 = net.minecraft.resources.Identifier.fromNamespaceAndPath(namespace, "cobbled_" + path);
        net.minecraft.resources.Identifier cobbledId2 = net.minecraft.resources.Identifier.fromNamespaceAndPath(namespace, "cobble" + path);
        if (net.minecraft.core.registries.BuiltInRegistries.BLOCK.containsKey(cobbledId1)) {
            return cobbledId1.toString();
        } else if (net.minecraft.core.registries.BuiltInRegistries.BLOCK.containsKey(cobbledId2)) {
            return cobbledId2.toString();
        }

        return host;
    }

    private static String generateCompressionRecipe(String input, String output, int count) {
        String pattern = count == 4 ? "[\"##\",\"##\"]" : "[\"###\",\"###\",\"###\"]";
        return "{\"type\":\"minecraft:crafting_shaped\",\"category\":\"misc\",\"pattern\":" + pattern + ",\"key\":{\"#\":\"" + input + "\"},\"result\":{\"id\":\"" + output + "\"}}";
    }

    private static String generateDecompressionRecipe(String input, String output, int count) {
        return "{\"type\":\"minecraft:crafting_shapeless\",\"category\":\"misc\",\"ingredients\":[\"" + input + "\"],\"result\":{\"id\":\"" + output + "\",\"count\":" + count + "}}";
    }

    private static void generateRecipes() {
        java.util.Set<String> generatedRecipes = new java.util.HashSet<>();

        
        java.util.Map<ores.mathieu.material.Material, java.util.Set<Enum<?>>> matToTypes = new java.util.HashMap<>();
        for (ores.mathieu.registry.RegistryDecoder.ResolvedObject obj : DiscoveryManager.DECODED_DATA.objects) {
            matToTypes.computeIfAbsent(obj.material, k -> new java.util.HashSet<>()).add(obj.type);
        }
        java.util.Set<ores.mathieu.material.Material> matWithOres = new java.util.HashSet<>();
        for (ores.mathieu.registry.RegistryDecoder.ResolvedOre ore : DiscoveryManager.DECODED_DATA.ores) {
            matWithOres.add(ore.material);
        }

        for (ores.mathieu.material.Material material : ores.mathieu.material.MaterialsDatabase.MATERIALS.values()) {
            java.util.Set<Enum<?>> activeTypes = matToTypes.getOrDefault(material, java.util.Collections.emptySet());
            
            String matName = material.getName();
            String baseType = material.getBaseType();

            boolean hasBaseItem = activeTypes.contains(ores.mathieu.material.ItemType.forName(baseType))
                || DiscoveryManager.DECODED_DATA.objects.stream().filter(o -> o.material == material && isVanillaObject(o)).anyMatch(o -> getVanillaBaseTypeMatch(o, baseType));

            boolean hasRawItem = activeTypes.contains(ores.mathieu.material.ItemType.RAW);
            boolean hasNugget = activeTypes.contains(ores.mathieu.material.ItemType.NUGGET);
            boolean hasBlock = activeTypes.contains(ores.mathieu.material.BlockType.BLOCK);
            boolean hasRawBlock = activeTypes.contains(ores.mathieu.material.BlockType.RAW_BLOCK);
            boolean hasDust = activeTypes.contains(ores.mathieu.material.ItemType.DUST);
            boolean hasDustBlock = activeTypes.contains(ores.mathieu.material.BlockType.DUST_BLOCK);
            boolean hasDirtyDust = activeTypes.contains(ores.mathieu.material.ItemType.DIRTY_DUST);
            boolean hasClump = activeTypes.contains(ores.mathieu.material.ItemType.CLUMP);
            boolean hasShard = activeTypes.contains(ores.mathieu.material.ItemType.SHARD);
            boolean hasCrystal = activeTypes.contains(ores.mathieu.material.ItemType.CRYSTAL);
            boolean hasCrushedRaw = activeTypes.contains(ores.mathieu.material.ItemType.CRUSHED_RAW);
            boolean hasOreBlock = matWithOres.contains(material);

            boolean hasCleanSlurry = activeTypes.contains(ores.mathieu.material.ChemicalType.CLEAN);
            boolean hasDirtySlurry = activeTypes.contains(ores.mathieu.material.ChemicalType.DIRTY);

            String oreTag = "#c:ores/" + matName;
            String baseItemId = getEffectiveId(matName, baseType);
            String rawTag = "#c:raw_materials/" + matName;
            String dustTag = "#c:dusts/" + matName;
            String dustId = getEffectiveId(matName, "dust");
            String blockId = getEffectiveId(matName, "block");

            String baseTagCategory = "ingots";
            if (baseType.equals("gem") || baseType.equals("self")) baseTagCategory = "gems";
            else if (baseType.equals("dust")) baseTagCategory = "dusts";
            
            String baseTag = "#c:" + baseTagCategory + "/" + matName;
            String rawBlockTag = "#c:storage_blocks/raw_" + matName;

            if (baseItemId.startsWith("minecraft:")) {
                createTagFile("items", "c", baseTagCategory + "/" + matName, baseItemId);
                createTagFile("items", "c", baseTagCategory, baseTag);
            }
            if (blockId.startsWith("minecraft:")) {
                createTagFile("blocks", "c", "storage_blocks/" + matName, blockId);
                createTagFile("blocks", "c", "storage_blocks", "#c:storage_blocks/" + matName);
                createTagFile("items", "c", "storage_blocks/" + matName, blockId);
                createTagFile("items", "c", "storage_blocks", "#c:storage_blocks/" + matName);
            }

            if (hasOreBlock && hasBaseItem && !matName.equals("netherite")) {
                int smeltTime = material.getOreSmeltingTime();
                float xp = material.getOreSmeltingXP();

                addRecipeFile(matName + "_from_ores_smelting", generateSmeltingRecipe(oreTag, baseItemId, xp, smeltTime, "smelting"), generatedRecipes);
                addRecipeFile(matName + "_from_ores_blasting", generateSmeltingRecipe(oreTag, baseItemId, xp, smeltTime / 2, "blasting"), generatedRecipes);
            }

            if (hasRawItem && hasBaseItem) {
                int smeltTime = material.getOreSmeltingTime();
                float xp = material.getOreSmeltingXP();

                addRecipeFile(matName + "_from_raw_smelting", generateSmeltingRecipe(rawTag, baseItemId, xp, smeltTime, "smelting"), generatedRecipes);
                addRecipeFile(matName + "_from_raw_blasting", generateSmeltingRecipe(rawTag, baseItemId, xp, smeltTime / 2, "blasting"), generatedRecipes);
            }

            if (hasDust && hasBaseItem) {
                int smeltTime = material.getOreSmeltingTime();
                float xp = material.getOreSmeltingXP();
                addRecipeFile(matName + "_from_dust_smelting", generateSmeltingRecipe(dustTag, baseItemId, xp, smeltTime, "smelting"), generatedRecipes);
                addRecipeFile(matName + "_from_dust_blasting", generateSmeltingRecipe(dustTag, baseItemId, xp, smeltTime / 2, "blasting"), generatedRecipes);
            }

            if (hasDustBlock && hasBlock) {
                String dustBlockId = getEffectiveId(matName, "dust_block");
                int smeltTime = material.getOreSmeltingTime() * 9;
                float xp = material.getOreSmeltingXP() * 9;
                addRecipeFile(matName + "_block_from_dust_block_smelting", generateSmeltingRecipe(dustBlockId, blockId, xp, smeltTime, "smelting"), generatedRecipes);
                addRecipeFile(matName + "_block_from_dust_block_blasting", generateSmeltingRecipe(dustBlockId, blockId, xp, smeltTime / 2, "blasting"), generatedRecipes);
            }

            if (hasBaseItem && hasBlock) {
                int ratio = material.getCompressionRatio();
                addRecipeFile(matName + "_block", generateCompressionRecipe(baseTag, blockId, ratio), generatedRecipes);
                addRecipeFile(matName + "_from_block", generateDecompressionRecipe(blockId, baseItemId, ratio), generatedRecipes);
            }

            if (hasCrushedRaw && hasBaseItem && !matName.equals("netherite")) {
                String crushedId = getEffectiveId(matName, "crushed_raw");
                String cId = crushedId.contains(":") ? crushedId : "ores:" + crushedId;
                int smeltTime = material.getOreSmeltingTime();
                float xp = material.getOreSmeltingXP();
                addRecipeFile(matName + "_from_crushed_smelting", generateSmeltingRecipe(cId, baseItemId, xp, smeltTime, "smelting"), generatedRecipes);
                addRecipeFile(matName + "_from_crushed_blasting", generateSmeltingRecipe(cId, baseItemId, xp, smeltTime / 2, "blasting"), generatedRecipes);
            }

            String rawBlockId = getEffectiveId(matName, "raw_block");
            if (hasRawItem && hasRawBlock) {
                String rawItemId = getEffectiveId(matName, "raw");
                int ratio = material.getCompressionRatio();

                if (rawBlockId.startsWith("minecraft:")) {
                    createTagFile("blocks", "c", "storage_blocks/raw_" + matName, rawBlockId);
                    createTagFile("blocks", "c", "storage_blocks", rawBlockTag);
                    createTagFile("items", "c", "storage_blocks/raw_" + matName, rawBlockId);
                    createTagFile("items", "c", "storage_blocks", rawBlockTag);
                }
                if (rawItemId.startsWith("minecraft:")) {
                    createTagFile("items", "c", "raw_materials/" + matName, rawItemId);
                    createTagFile("items", "c", "raw_materials", rawTag);
                }

                addRecipeFile(matName + "_raw_block", generateCompressionRecipe(rawTag, rawBlockId, ratio), generatedRecipes);
                addRecipeFile(matName + "_raw_from_block", generateDecompressionRecipe(rawBlockId, rawItemId, ratio), generatedRecipes);

                if (hasBlock) {
                    int smeltTime = material.getOreSmeltingTime() * 9;
                    float xp = material.getOreSmeltingXP() * 9;

                    addRecipeFile(matName + "_block_from_raw_block_smelting", generateSmeltingRecipe(rawBlockTag, blockId, xp, smeltTime, "smelting"), generatedRecipes);
                    addRecipeFile(matName + "_block_from_raw_block_blasting", generateSmeltingRecipe(rawBlockTag, blockId, xp, smeltTime / 2, "blasting"), generatedRecipes);
                }
            }
            if (ores.mathieu.compat.MekanismCompat.isMekanismLoaded()) {

                if (hasDust) {
                    JsonObject dustOut = new JsonObject(); dustOut.addProperty("count", 2); dustOut.addProperty("id", dustId);
                    if (hasOreBlock) {
                        JsonObject oreIn = new JsonObject(); oreIn.addProperty("count", 1); oreIn.addProperty("tag", "c:ores/" + matName);
                        addRecipeFile("mekanism/enriching/" + matName + "_from_ore", generateMekanismRecipe("enriching", oreIn, dustOut), generatedRecipes);
                    }

                    if (hasRawItem) {
                        JsonObject dustOut3 = new JsonObject(); dustOut3.addProperty("count", 3); dustOut3.addProperty("id", dustId);
                        JsonObject rawIn = new JsonObject(); rawIn.addProperty("count", 1); rawIn.addProperty("tag", "c:raw_materials/" + matName);
                        addRecipeFile("mekanism/enriching/" + matName + "_from_raw", generateMekanismRecipe("enriching", rawIn, dustOut3), generatedRecipes);
                    }

                    if (hasBaseItem) {
                        JsonObject ingotIn = new JsonObject(); ingotIn.addProperty("count", 1); ingotIn.addProperty("tag", "c:ingots/" + matName);
                        JsonObject dustOut1 = new JsonObject(); dustOut1.addProperty("count", 1); dustOut1.addProperty("id", dustId);
                        addRecipeFile("mekanism/crushing/" + matName + "_from_ingot", generateMekanismRecipe("crushing", ingotIn, dustOut1), generatedRecipes);
                    }
                }

                if (hasClump) {
                    String clumpId = getEffectiveId(matName, "clump");
                    JsonObject oxygen = new JsonObject(); oxygen.addProperty("amount", 1); oxygen.addProperty("chemical", "mekanism:oxygen");

                    JsonObject clumpOut3 = new JsonObject(); clumpOut3.addProperty("count", 3); clumpOut3.addProperty("id", clumpId);
                    if (hasOreBlock) {
                        JsonObject oreIn = new JsonObject(); oreIn.addProperty("count", 1); oreIn.addProperty("tag", "c:ores/" + matName);
                        addRecipeFile("mekanism/purifying/" + matName + "_from_ore", generateMekanismChemicalRecipe("purifying", oreIn, oxygen, clumpOut3), generatedRecipes);
                    }

                    if (hasRawItem) {
                        JsonObject clumpOut4 = new JsonObject(); clumpOut4.addProperty("count", 4); clumpOut4.addProperty("id", clumpId);
                        JsonObject rawIn = new JsonObject(); rawIn.addProperty("count", 1); rawIn.addProperty("tag", "c:raw_materials/" + matName);
                        addRecipeFile("mekanism/purifying/" + matName + "_from_raw", generateMekanismChemicalRecipe("purifying", rawIn, oxygen, clumpOut4), generatedRecipes);
                    }

                    if (hasDirtyDust) {
                        String dirtyDustId = getEffectiveId(matName, "dirty_dust");
                        JsonObject clumpIn = new JsonObject(); clumpIn.addProperty("count", 1); clumpIn.addProperty("tag", "c:clumps/" + matName);
                        JsonObject dirtyDustOut = new JsonObject(); dirtyDustOut.addProperty("count", 1); dirtyDustOut.addProperty("id", dirtyDustId);
                        addRecipeFile("mekanism/crushing/" + matName + "_dirty_dust_from_clump", generateMekanismRecipe("crushing", clumpIn, dirtyDustOut), generatedRecipes);

                        if (hasDust) {
                            JsonObject dirtyDustIn = new JsonObject(); dirtyDustIn.addProperty("count", 1); dirtyDustIn.addProperty("tag", "c:dusts/" + matName);
                            JsonObject dustOut1 = new JsonObject(); dustOut1.addProperty("count", 1); dustOut1.addProperty("id", dustId);
                            addRecipeFile("mekanism/enriching/" + matName + "_dust_from_dirty_dust", generateMekanismRecipe("enriching", dirtyDustIn, dustOut1), generatedRecipes);
                        }
                    }
                }

                if (hasShard) {
                    String shardId = getEffectiveId(matName, "shard");
                    JsonObject hcl = new JsonObject(); hcl.addProperty("amount", 1); hcl.addProperty("chemical", "mekanism:hydrogen_chloride");

                    JsonObject shardOut4 = new JsonObject(); shardOut4.addProperty("count", 4); shardOut4.addProperty("id", shardId);
                    if (hasOreBlock) {
                        JsonObject oreIn = new JsonObject(); oreIn.addProperty("count", 1); oreIn.addProperty("tag", "c:ores/" + matName);
                        addRecipeFile("mekanism/injecting/" + matName + "_from_ore", generateMekanismChemicalRecipe("injecting", oreIn, hcl, shardOut4), generatedRecipes);
                    }

                    if (hasRawItem) {
                        JsonObject shardOut5 = new JsonObject(); shardOut5.addProperty("count", 5); shardOut5.addProperty("id", shardId);
                        JsonObject rawIn = new JsonObject(); rawIn.addProperty("count", 1); rawIn.addProperty("tag", "c:raw_materials/" + matName);
                        addRecipeFile("mekanism/injecting/" + matName + "_from_raw", generateMekanismChemicalRecipe("injecting", rawIn, hcl, shardOut5), generatedRecipes);
                    }

                    if (hasClump) {
                        String clumpId = getEffectiveId(matName, "clump");
                        JsonObject oxygen = new JsonObject(); oxygen.addProperty("amount", 1); oxygen.addProperty("chemical", "mekanism:oxygen");
                        JsonObject shardIn = new JsonObject(); shardIn.addProperty("count", 1); shardIn.addProperty("tag", "c:shards/" + matName);
                        JsonObject clumpOut1 = new JsonObject(); clumpOut1.addProperty("count", 1); clumpOut1.addProperty("id", clumpId);
                        addRecipeFile("mekanism/purifying/" + matName + "_clump_from_shard", generateMekanismChemicalRecipe("purifying", shardIn, oxygen, clumpOut1), generatedRecipes);
                    }
                }

                if (hasDirtySlurry && hasCleanSlurry) {
                    String dirtySlurryId = "ores:dirty_" + matName + "_slurry";
                    String cleanSlurryId = "ores:clean_" + matName + "_slurry";

                    JsonObject sulfuric = new JsonObject(); sulfuric.addProperty("amount", 1); sulfuric.addProperty("chemical", "mekanism:sulfuric_acid");

                    JsonObject slurryOut1000 = new JsonObject(); slurryOut1000.addProperty("amount", 1000); slurryOut1000.addProperty("id", dirtySlurryId);
                    if (hasOreBlock) {
                        JsonObject oreIn = new JsonObject(); oreIn.addProperty("count", 1); oreIn.addProperty("tag", "c:ores/" + matName);
                        addRecipeFile("mekanism/dissolution/" + matName + "_from_ore", generateMekanismChemicalRecipe("dissolution", oreIn, sulfuric, slurryOut1000), generatedRecipes);
                    }

                    if (hasRawItem) {
                        JsonObject slurryOut2000 = new JsonObject(); slurryOut2000.addProperty("amount", 2000); slurryOut2000.addProperty("id", dirtySlurryId);
                        JsonObject rawIn = new JsonObject(); rawIn.addProperty("count", 1); rawIn.addProperty("tag", "c:raw_materials/" + matName);
                        addRecipeFile("mekanism/dissolution/" + matName + "_from_raw", generateMekanismChemicalRecipe("dissolution", rawIn, sulfuric, slurryOut2000), generatedRecipes);
                    }

                    addRecipeFile("mekanism/washing/" + matName, generateMekanismSlurryRecipe("washing", dirtySlurryId, 1, cleanSlurryId, 1), generatedRecipes);

                    if (hasCrystal) {
                        String crystalId = getEffectiveId(matName, "crystal");
                        JsonObject cleanIn = new JsonObject(); cleanIn.addProperty("amount", 200); cleanIn.addProperty("chemical", cleanSlurryId);
                        JsonObject crystalOut = new JsonObject(); crystalOut.addProperty("count", 1); crystalOut.addProperty("id", crystalId);
                        addRecipeFile("mekanism/crystallizing/" + matName, generateMekanismChemicalRecipe("crystallizing", null, cleanIn, crystalOut), generatedRecipes);

                        if (hasShard) {
                            String shardId = getEffectiveId(matName, "shard");
                            JsonObject hcl = new JsonObject(); hcl.addProperty("amount", 1); hcl.addProperty("chemical", "mekanism:hydrogen_chloride");
                            JsonObject crystalIn = new JsonObject(); crystalIn.addProperty("count", 1); crystalIn.addProperty("tag", "c:crystals/" + matName);
                            JsonObject shardOut1 = new JsonObject(); shardOut1.addProperty("count", 1); shardOut1.addProperty("id", shardId);
                            addRecipeFile("mekanism/injecting/" + matName + "_shard_from_crystal", generateMekanismChemicalRecipe("injecting", crystalIn, hcl, shardOut1), generatedRecipes);
                        }
                    }
                if (hasOreBlock) {
                    java.util.List<ores.mathieu.registry.RegistryDecoder.ResolvedOre> oresForMat = DiscoveryManager.DECODED_DATA.ores.stream().filter(o -> o.material == material).toList();
                    for (ores.mathieu.registry.RegistryDecoder.ResolvedOre ore : oresForMat) {
                        String stoneName = ore.stoneReplacement;
                        String hostName = getCrushedHostBlock(stoneName);
                        String stonePath = stoneName.contains(":") ? stoneName.split(":")[1] : stoneName;
                        String oreName = stonePath + "_" + matName + "_ore";

                        float avgDrops = (material.getOreDropMin() + material.getOreDropMax()) / 2.0f;
                        int rawAmount = (int) Math.ceil(avgDrops * 3);
                        int dustAmount = (int) Math.ceil(avgDrops * 8);

                        String dropTypeStr = material.getOreDropItem();
                        if (dropTypeStr == null || dropTypeStr.isEmpty()) {
                            dropTypeStr = "gem".equals(material.getBaseType()) ? "gem" : "raw";
                        }

                        String dropTagCategory = "raw_materials";
                        for (ores.mathieu.material.ItemType t : ores.mathieu.material.ItemType.values()) {
                            if (t.name().equalsIgnoreCase(dropTypeStr) || t.getSuffix().equalsIgnoreCase(dropTypeStr)) {
                                dropTagCategory = t.getDefaultOverride().getTagCategory();
                                break;
                            }
                        }
                        String dropTag = "c:" + dropTagCategory + "/" + matName;

                        net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, oreName);
                        if (!net.minecraft.core.registries.BuiltInRegistries.BLOCK.containsKey(id)) {
                            continue;
                        }

                        if (hasRawItem || "gem".equals(material.getBaseType()) || material.getOreDropItem() != null) {
                            JsonObject rawIn = new JsonObject(); rawIn.addProperty("amount", Math.min(64, rawAmount)); rawIn.addProperty("tag", dropTag);
                            JsonObject cobbleIn = new JsonObject(); cobbleIn.addProperty("item", hostName);
                            JsonObject oreOut = new JsonObject(); oreOut.addProperty("id", "ores:" + oreName);
                            addRecipeFile("mekanism/combining/" + oreName + "_from_raw", generateMekanismCombiningRecipe(rawIn, cobbleIn, oreOut), generatedRecipes);
                        }
                        if (hasDust) {
                            JsonObject dustIn = new JsonObject(); dustIn.addProperty("amount", Math.min(64, dustAmount)); dustIn.addProperty("tag", "c:dusts/" + matName);
                            JsonObject cobbleIn = new JsonObject(); cobbleIn.addProperty("item", hostName);
                            JsonObject oreOut = new JsonObject(); oreOut.addProperty("id", "ores:" + oreName);
                            addRecipeFile("mekanism/combining/" + oreName + "_from_dust", generateMekanismCombiningRecipe(dustIn, cobbleIn, oreOut), generatedRecipes);
                        }
                        }
                    }
                }
            }

            if (ores.mathieu.compat.CreateCompat.isCreateLoaded()) {
                boolean hasPlate = DiscoveryManager.DECODED_DATA.objects.stream().anyMatch(o -> o.material == material && o.type == ores.mathieu.material.ItemType.PLATE);

                if (hasPlate && hasBaseItem) {
                    JsonObject plateOut = new JsonObject(); plateOut.addProperty("id", getEffectiveId(matName, "plate"));
                    JsonArray results = new JsonArray(); results.add(plateOut);

                    JsonObject ingotIn = new JsonObject(); ingotIn.addProperty("tag", "c:ingots/" + matName);
                    addRecipeFile("create/pressing/" + matName + "_ingot", generateCreateRecipe("pressing", ingotIn, results, null), generatedRecipes);
                }

                if (hasCrushedRaw) {
                    String crushedId = getEffectiveId(matName, "crushed_raw");

                    if (hasRawItem) {
                        JsonObject out1 = new JsonObject(); out1.addProperty("id", crushedId);
                        JsonObject out2 = new JsonObject(); out2.addProperty("chance", 0.75); out2.addProperty("id", "create:experience_nugget");
                        JsonArray results = new JsonArray(); results.add(out1); results.add(out2);

                        JsonObject rawIn = new JsonObject(); rawIn.addProperty("tag", "c:raw_materials/" + matName);
                        addRecipeFile("create/crushing/raw_" + matName, generateCreateRecipe("crushing", rawIn, results, 400), generatedRecipes);
                    }

                    if (hasOreBlock) {
                        java.util.List<ores.mathieu.registry.RegistryDecoder.ResolvedOre> oresForMat = DiscoveryManager.DECODED_DATA.ores.stream().filter(o -> o.material == material).toList();
                        for (ores.mathieu.registry.RegistryDecoder.ResolvedOre ore : oresForMat) {
                            String stoneName = ore.stoneReplacement;
                            if (stoneName.equals("minecraft:ice")) continue;

                            String stonePath = stoneName.contains(":") ? stoneName.split(":")[1] : stoneName;
                            String oreName = stonePath + "_" + matName + "_ore";

                            net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, oreName);
                            if (!ores.mathieu.registry.RegistrationHandler.QUEUED_BLOCKS.contains(id) && !net.minecraft.core.registries.BuiltInRegistries.BLOCK.containsKey(id)) {
                                continue;
                            }

                            JsonObject out1 = new JsonObject(); out1.addProperty("id", crushedId);
                            JsonObject out2 = new JsonObject(); out2.addProperty("chance", 0.75); out2.addProperty("id", crushedId);
                            JsonObject out3 = new JsonObject(); out3.addProperty("chance", 0.75); out3.addProperty("id", "create:experience_nugget");
                            JsonObject out4 = new JsonObject(); out4.addProperty("chance", 0.125); out4.addProperty("id", getCrushedHostBlock(stoneName));
                            
                            JsonArray crushingResults = new JsonArray(); crushingResults.add(out1); crushingResults.add(out2); crushingResults.add(out3); crushingResults.add(out4);
                            JsonObject oreIn = new JsonObject(); oreIn.addProperty("item", "ores:" + oreName);
                            addRecipeFile("create/crushing/" + oreName, generateCreateRecipe("crushing", oreIn, crushingResults, 250), generatedRecipes);

                            JsonArray millingResults = new JsonArray(); millingResults.add(out1);
                            addRecipeFile("create/milling/" + oreName, generateCreateRecipe("milling", oreIn, millingResults, 250), generatedRecipes);
                        }
                    }

                    if (hasNugget) {
                        JsonObject nuggetOut = new JsonObject(); nuggetOut.addProperty("count", 9); nuggetOut.addProperty("id", getEffectiveId(matName, "nugget"));
                        JsonArray results = new JsonArray(); results.add(nuggetOut);

                        JsonObject crushedIn = new JsonObject(); crushedIn.addProperty("item", crushedId.contains(":") ? crushedId : "ores:" + crushedId);
                        addRecipeFile("create/splashing/crushed_" + matName, generateCreateRecipe("splashing", crushedIn, results, null), generatedRecipes);
                    }
                }
            }

            if (hasNugget && hasBaseItem) {
                baseItemId = getEffectiveId(matName, baseType);
                String nuggetId = getEffectiveId(matName, "nugget");

                addRecipeFile(matName + "_from_nugget", generateCompressionRecipe(nuggetId, baseItemId, 9), generatedRecipes);
                addRecipeFile(matName + "_nugget_from_base", generateDecompressionRecipe(baseItemId, nuggetId, 9), generatedRecipes);
            }

            for (ores.mathieu.registry.RegistryDecoder.ResolvedObject obj : DiscoveryManager.DECODED_DATA.objects) {
                if (obj.material != material || !(obj.type instanceof ores.mathieu.material.BlockType)) continue;

                net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.fromNamespaceAndPath(OresCoreCommon.MOD_ID, obj.registryName);
                if (!ores.mathieu.registry.RegistrationHandler.QUEUED_BLOCKS.contains(id) && !net.minecraft.core.registries.BuiltInRegistries.BLOCK.containsKey(id)) continue;

                ores.mathieu.material.BlockType bt = (ores.mathieu.material.BlockType) obj.type;
                ores.mathieu.material.BlockType parent = bt.getParentType();
                if (parent == null) continue;

                String parentRegistryName = null;
                for (ores.mathieu.registry.RegistryDecoder.ResolvedObject pObj : DiscoveryManager.DECODED_DATA.objects) {
                    if (pObj.material == material && pObj.type == parent) {
                        parentRegistryName = pObj.registryName;
                        break;
                    }
                }

                if (parentRegistryName != null) {
                    boolean isChildVanilla = ores.mathieu.registry.RegistrationHandler.VANILLA_ITEMS.contains(obj.registryName);
                    boolean isParentVanilla = ores.mathieu.registry.RegistrationHandler.VANILLA_ITEMS.contains(parentRegistryName);

                    String childId = (isChildVanilla ? "minecraft:" : "ores:") + obj.registryName;
                    String parentId = (isParentVanilla ? "minecraft:" : "ores:") + parentRegistryName;

                    addRecipeFile(matName + "_" + bt.getSuffix(), generateCompressionRecipe(parentId, childId, 9), generatedRecipes);
                    addRecipeFile(matName + "_from_" + bt.getSuffix(), generateDecompressionRecipe(childId, parentId, 9), generatedRecipes);
                }
            }
        }
        ores.mathieu.OresCoreCommon.LOGGER.debug("[ORES CORE] {} recipes added", generatedRecipes.size());
    }

    private static void addRecipeFile(String name, String content, java.util.Set<String> generatedSet) {

        if (!generatedSet.add(content)) return;

        JsonObject json = GSON.fromJson(content, JsonObject.class);
        VirtualResourcePack.SERVER.addJson("data/ores/recipe/" + name + ".json", json);
    }

    public static String getEffectiveId(String matName, String typeOrName) {
        if (matName.equals("iron") || matName.equals("gold") || matName.equals("copper")) {
            if (typeOrName.equals("raw")) return "minecraft:raw_" + matName;
            if (typeOrName.equals("raw_block")) return "minecraft:raw_" + matName + "_block";
            if (typeOrName.equals("nugget")) return "minecraft:" + matName + "_nugget";
            if (typeOrName.equals("ingot") || typeOrName.equals("gem") || typeOrName.equals("dust")) return "minecraft:" + matName + "_ingot";
            if (typeOrName.equals("block")) return "minecraft:" + matName + "_block";
        } else if (matName.equals("coal") || matName.equals("diamond") || matName.equals("emerald") || matName.equals("lapis") || matName.equals("redstone")) {
            String id = matName;
            if (matName.equals("lapis")) id = "lapis_lazuli";
            if (typeOrName.equals("gem") || typeOrName.equals("dust") || typeOrName.equals("self")) return "minecraft:" + id;
            if (typeOrName.equals("block")) return "minecraft:" + (matName.equals("lapis") ? "lapis_block" : id + "_block");
        } else if (matName.equals("quartz")) {
            if (typeOrName.equals("gem") || typeOrName.equals("dust") || typeOrName.equals("self")) return "minecraft:quartz";
            if (typeOrName.equals("block")) return "minecraft:quartz_block";
        } else if (matName.equals("netherite")) {
            if (typeOrName.equals("ingot")) return "minecraft:netherite_ingot";
            if (typeOrName.equals("scrap")) return "minecraft:netherite_scrap";
            if (typeOrName.equals("block")) return "minecraft:netherite_block";
        }

        ores.mathieu.material.Material matInstance = ores.mathieu.material.MaterialsDatabase.get(matName);
        if (matInstance != null && matInstance.getBaseItemName() != null && (typeOrName.equals("gem") || typeOrName.equals("dust") || typeOrName.equals("self"))) {
            return "ores:" + matInstance.getBaseItemName();
        }

        if (typeOrName.equals("raw_block")) return "ores:raw_" + matName + "_block";
        if (typeOrName.equals("raw")) return "ores:raw_" + matName;
        if (typeOrName.equals("block")) return "ores:" + matName + "_block";
        if (typeOrName.equals("nugget")) return "ores:" + matName + "_nugget";
        if (typeOrName.equals("self")) return "ores:" + matName;

        return "ores:" + matName + "_" + typeOrName;
    }

    public static boolean getVanillaBaseTypeMatch(ores.mathieu.registry.RegistryDecoder.ResolvedObject obj, String baseType) {
        String n = obj.registryName;
        if (baseType.equals("ingot") && n.endsWith("_ingot")) return true;

        if (baseType.equals("self") && (
            n.equals("coal") || n.equals("diamond") || n.equals("emerald") ||
            n.equals("redstone") || n.equals("quartz") || n.equals("lapis_lazuli")
        )) return true;

        if (baseType.equals("gem") && (n.equals("diamond") || n.equals("emerald") || n.equals("lapis_lazuli") || n.equals("quartz"))) return true;
        if (baseType.equals("dust") && n.equals("redstone")) return true;
        return false;
    }

    private static java.util.Set<String> findAllTrimPatterns() {
        java.util.Set<String> patterns = new java.util.HashSet<>();

        for (String p : java.util.List.of("sentry", "dune", "coast", "wild", "ward", "eye", "vex", "tide",
                                         "snout", "rib", "spire", "wayfinder", "shaper", "silence", "raiser", "host", "flow", "bolt")) {
            patterns.add("minecraft:" + p);
        }

        patterns.addAll(ores.mathieu.platform.Services.getPlatform().findTrimPatterns());

        OresCoreCommon.LOGGER.debug("[ORES CORE] Discovered {} armor trim patterns.", patterns.size());
        return patterns;
    }

    private static void generateTrimAtlas(java.util.List<String> moddedSuffixes, java.util.Set<String> patterns) {
        JsonObject atlas = new JsonObject();
        JsonArray sources = new JsonArray();

        JsonObject source = new JsonObject();
        source.addProperty("type", "minecraft:paletted_permutations");
        source.addProperty("palette_key", "minecraft:trims/color_palettes/trim_palette");

        JsonObject permutations = new JsonObject();
        for (String suffix : moddedSuffixes) {
            permutations.addProperty(suffix, "ores:trims/color_palettes/" + suffix);
        }
        source.add("permutations", permutations);

        JsonArray textures = new JsonArray();
        for (String patternId : patterns) {
            String ns = patternId.contains(":") ? patternId.split(":")[0] : "minecraft";
            String path = patternId.contains(":") ? patternId.split(":")[1] : patternId;

            textures.add(ns + ":trims/entity/humanoid/" + path);
            textures.add(ns + ":trims/entity/humanoid_leggings/" + path);
        }
        source.add("textures", textures);

        sources.add(source);
        atlas.add("sources", sources);

        VirtualResourcePack.CLIENT.addJson("assets/minecraft/atlases/armor_trims.json", atlas);
    }

    private static void generateWorldGenFeatures() {
        int index = 0;
        for (ores.mathieu.worldgen.VeinConfig vein : ores.mathieu.config.ConfigManager.LOADED_VEINS) {

            if (vein.veinType.equals("GIANT")) {

                JsonObject configuredCarver = new JsonObject();
                configuredCarver.addProperty("type", "ores:giant_vein");

                JsonObject config = new JsonObject();
                config.addProperty("vein_index", index);
                configuredCarver.add("config", config);

                VirtualResourcePack.SERVER.addJson("data/ores/worldgen/configured_carver/vein_" + index + ".json", configuredCarver);
            } else {

                JsonObject configuredFeature = new JsonObject();
                configuredFeature.addProperty("type", "ores:dynamic_ore");

                JsonObject config = new JsonObject();
                config.addProperty("vein_index", index);
                configuredFeature.add("config", config);

                VirtualResourcePack.SERVER.addJson("data/ores/worldgen/configured_feature/vein_" + index + ".json", configuredFeature);

                JsonObject placedFeature = new JsonObject();
                placedFeature.addProperty("feature", "ores:vein_" + index);

                JsonArray placement = new JsonArray();

                JsonObject countModifier = new JsonObject();
                countModifier.addProperty("type", "minecraft:count");
                countModifier.addProperty("count", vein.rarity);
                placement.add(countModifier);

                JsonObject inSquare = new JsonObject();
                inSquare.addProperty("type", "minecraft:in_square");
                placement.add(inSquare);

                JsonObject heightModifier = new JsonObject();
                heightModifier.addProperty("type", "minecraft:height_range");

                JsonObject heightData = new JsonObject();
                heightData.addProperty("type", "minecraft:uniform");
                
                JsonObject minInc = new JsonObject();
                minInc.addProperty("absolute", vein.minY);
                heightData.add("min_inclusive", minInc);

                JsonObject maxInc = new JsonObject();
                maxInc.addProperty("absolute", vein.maxY);
                heightData.add("max_inclusive", maxInc);

                heightModifier.add("height", heightData);
                placement.add(heightModifier);

                JsonObject biomeFilter = new JsonObject();
                biomeFilter.addProperty("type", "minecraft:biome");
                placement.add(biomeFilter);

                placedFeature.add("placement", placement);

                VirtualResourcePack.SERVER.addJson("data/ores/worldgen/placed_feature/vein_" + index + ".json", placedFeature);
            }

            JsonObject biomeModifier = new JsonObject();
            if (vein.veinType.equals("GIANT")) {
                biomeModifier.addProperty("type", "neoforge:add_carvers");
                JsonArray carvers = new JsonArray();
                carvers.add("ores:vein_" + index);
                biomeModifier.add("carvers", carvers);
            } else {
                biomeModifier.addProperty("type", "neoforge:add_features");
                JsonArray features = new JsonArray();
                features.add("ores:vein_" + index);
                biomeModifier.add("features", features);
                biomeModifier.addProperty("step", "underground_ores");
            }

            if (vein.biomesWhitelist != null && !vein.biomesWhitelist.isEmpty()) {
                JsonArray biomes = new JsonArray();
                for (String b : vein.biomesWhitelist) biomes.add(b);
                biomeModifier.add("biomes", biomes);
            } else if (vein.biomesBlacklist != null && !vein.biomesBlacklist.isEmpty()) {
                JsonObject biomes = new JsonObject();
                biomes.addProperty("type", "neoforge:none_of");
                JsonArray list = new JsonArray();
                for (String b : vein.biomesBlacklist) list.add(b);
                biomes.add("value", list);
                biomeModifier.add("biomes", biomes);
            } else {

                String dim = (vein.dimensionsWhitelist != null && !vein.dimensionsWhitelist.isEmpty())
                    ? vein.dimensionsWhitelist.get(0) : "minecraft:overworld";

                if (dim.contains("nether")) {
                    biomeModifier.addProperty("biomes", "#minecraft:is_nether");
                } else if (dim.contains("the_end")) {
                    biomeModifier.addProperty("biomes", "#minecraft:is_end");
                } else {
                    biomeModifier.addProperty("biomes", "#minecraft:is_overworld");
                }
            }

            VirtualResourcePack.SERVER.addJson("data/ores/neoforge/biome_modifier/vein_" + index + ".json", biomeModifier);

            index++;
        }
    }

    private static void addTooltipTranslations(java.util.Map<String, JsonObject> langJsons) {
        
        addLang(langJsons, "tooltip.ores.tool.pickaxe",  "Pickaxe", "Pioche", "Pico", "Piccone", "Spitzhacke", "Picareta", "Кирка", "镐", "つるはし");
        addLang(langJsons, "tooltip.ores.tool.shovel",   "Shovel", "Pelle", "Pala", "Pala", "Schaufel", "Pá", "Лопата", "铲", "シャベル");
        addLang(langJsons, "tooltip.ores.tool.axe",      "Axe", "Hache", "Hacha", "Ascia", "Axt", "Machado", "Топор", "斧", "斧");
        addLang(langJsons, "tooltip.ores.tool.hoe",      "Hoe", "Houe", "Azada", "Zappa", "Hacke", "Enxada", "Мотыга", "锄", "クワ");

        
        addLang(langJsons, "tooltip.ores.level.wood",      "Wooden", "en Bois", "de Madera", "di Legno", "Holz-", "de Madeira", "Деревянная", "木", "木の");
        addLang(langJsons, "tooltip.ores.level.stone",     "Stone", "en Pierre", "de Piedra", "di Pietra", "Stein-", "de Pedra", "Каменная", "石", "石の");
        addLang(langJsons, "tooltip.ores.level.iron",      "Iron", "en Fer", "de Hierro", "di Ferro", "Eisen-", "de Ferro", "Железная", "铁", "鉄の");
        addLang(langJsons, "tooltip.ores.level.diamond",   "Diamond", "en Diamant", "de Diamante", "di Diamante", "Diamant-", "de Diamante", "Алмазная", "钻石", "ダイヤの");
        addLang(langJsons, "tooltip.ores.level.netherite",  "Netherite", "en Netherite", "de Netherita", "di Netherite", "Netherit-", "de Netherita", "Незеритовая", "下界合金", "ネザライトの");

        
        addLang(langJsons, "tooltip.ores.dim.overworld", "Overworld", "Surface", "Superficie", "Superficie", "Oberwelt", "Superfície", "Верхний мир", "主世界", "オーバーワールド");
        addLang(langJsons, "tooltip.ores.dim.nether",    "Nether", "Nether", "Nether", "Nether", "Nether", "Nether", "Незер", "下界", "ネザー");
        addLang(langJsons, "tooltip.ores.dim.end",        "End", "End", "End", "End", "Ende", "End", "Край", "末地", "エンド");
        addLang(langJsons, "tooltip.ores.dim.all",        "All Dimensions", "Toutes Dimensions", "Todas Dimensiones", "Tutte le Dimensioni", "Alle Dimensionen", "Todas Dimensões", "Все измерения", "所有维度", "全ディメンション");

        
        addLang(langJsons, "tooltip.ores.dim.aether",          "The Aether", "L'Aether", "El Aether", "L'Aether", "Der Aether", "O Aether", "Эфир", "天境", "天空");
        addLang(langJsons, "tooltip.ores.dim.twilight_forest",  "Twilight Forest", "Forêt du Crépuscule", "Bosque Crepuscular", "Foresta del Crepuscolo", "Zwielichtwald", "Floresta Crepuscular", "Сумеречный лес", "暮色森林", "トワイライトの森");
        addLang(langJsons, "tooltip.ores.dim.moon",             "Moon", "Lune", "Luna", "Luna", "Mond", "Lua", "Луна", "月球", "月");
        addLang(langJsons, "tooltip.ores.dim.mars",             "Mars", "Mars", "Marte", "Marte", "Mars", "Marte", "Марс", "火星", "火星");
        addLang(langJsons, "tooltip.ores.dim.venus",            "Venus", "Vénus", "Venus", "Venere", "Venus", "Vênus", "Венера", "金星", "金星");
        addLang(langJsons, "tooltip.ores.dim.mercury",          "Mercury", "Mercure", "Mercurio", "Mercurio", "Merkur", "Mercúrio", "Меркурий", "水星", "水星");
        addLang(langJsons, "tooltip.ores.dim.glacio",           "Glacio", "Glacio", "Glacio", "Glacio", "Glacio", "Glacio", "Глацио", "冰霜星", "グラシオ");
        addLang(langJsons, "tooltip.ores.dim.undergarden",      "Undergarden", "Sous-Jardin", "Subjardín", "Sottogiardino", "Untergarten", "Subjardim", "Подсад", "地下花园", "アンダーガーデン");
        addLang(langJsons, "tooltip.ores.dim.otherside",        "The Otherside", "L'Autre Côté", "El Otro Lado", "L'Altro Lato", "Die Andere Seite", "O Outro Lado", "Другая сторона", "彼岸", "アザーサイド");
        addLang(langJsons, "tooltip.ores.dim.tropics",          "Tropics", "Tropiques", "Trópicos", "Tropici", "Tropen", "Trópicos", "Тропики", "热带", "トロピクス");
        addLang(langJsons, "tooltip.ores.dim.mining",           "Mining Dimension", "Dimension Minière", "Dimensión Minera", "Dimensione Mineraria", "Bergbaudimension", "Dimensão de Mineração", "Шахтёрское измерение", "矿工维度", "マイニング次元");
        addLang(langJsons, "tooltip.ores.dim.the_other",        "The Other", "L'Autre", "El Otro", "L'Altro", "Das Andere", "O Outro", "Другое", "异界", "ジ・アザー");
        addLang(langJsons, "tooltip.ores.dim.the_beyond",       "The Beyond", "L'Au-Delà", "El Más Allá", "L'Aldilà", "Das Jenseits", "O Além", "За гранью", "彼方", "ビヨンド");
        addLang(langJsons, "tooltip.ores.dim.everbright",       "Everbright", "Éternelle Lumière", "Luz Eterna", "Luce Eterna", "Immerlicht", "Sempre Brilhante", "Вечный свет", "永明之地", "エバーブライト");
        addLang(langJsons, "tooltip.ores.dim.everdawn",         "Everdawn", "Aube Éternelle", "Amanecer Eterno", "Alba Eterna", "Immerdämmerung", "Sempre Alvorada", "Вечный рассвет", "永晓之地", "エバードーン");
        addLang(langJsons, "tooltip.ores.dim.lostcity",         "Lost City", "Cité Perdue", "Ciudad Perdida", "Città Perduta", "Verlorene Stadt", "Cidade Perdida", "Затерянный город", "失落之城", "ロストシティ");
        addLang(langJsons, "tooltip.ores.dim.void",             "The Void", "Le Vide", "El Vacío", "Il Vuoto", "Die Leere", "O Vazio", "Пустота", "虚空", "ヴォイド");

        
        addLang(langJsons, "tooltip.ores.no_generation",  "No generation configured", "Pas de génération configurée", "Sin generación configurada", "Nessuna generazione configurata", "Keine Generierung konfiguriert", "Sem geração configurada", "Генерация не настроена", "未配置生成", "生成未設定");
    }

    private static void addLang(java.util.Map<String, JsonObject> langJsons, String key,
                                String en, String fr, String es, String it, String de, String pt, String ru, String zh, String jp) {
        langJsons.get("en_us").addProperty(key, en);
        langJsons.get("fr_fr").addProperty(key, fr);
        langJsons.get("es_es").addProperty(key, es);
        langJsons.get("it_it").addProperty(key, it);
        langJsons.get("de_de").addProperty(key, de);
        langJsons.get("pt_br").addProperty(key, pt);
        langJsons.get("ru_ru").addProperty(key, ru);
        langJsons.get("zh_cn").addProperty(key, zh);
        langJsons.get("ja_jp").addProperty(key, jp);
    }
}
