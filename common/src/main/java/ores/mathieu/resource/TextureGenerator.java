//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Dynamic pixel-level texture generation and manipulation.
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ores.mathieu.OresCoreCommon;
import ores.mathieu.platform.Services;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("null")
public class TextureGenerator {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static int[] BASE_COLORS = null;
    private static final Map<String, int[]> RAW_PALETTES = new HashMap<>();
    private static final Map<String, int[]> MAT_PALETTES = new HashMap<>();
    private static final Map<String, ModelInfo> MODEL_INFO_CACHE = new HashMap<>();
    private static final Map<String, BufferedImage> HOST_BLOCK_TEXTURE_CACHE = new HashMap<>();
    private static final Map<String, BufferedImage> OVERLAY_TEXTURE_CACHE = new HashMap<>();

    private static int[] readPaletteColors(String path, int maxH) {
        BufferedImage img = readImageFromJar(path);
        if (img == null) return new int[0];
        int h = Math.min(maxH, img.getHeight());
        int[] out = new int[h];
        for (int y = 0; y < h; y++) {
            out[y] = img.getRGB(0, y);
        }
        return out;
    }

    private static int[] getBaseColors() {
        if (BASE_COLORS == null) {
            BASE_COLORS = readPaletteColors("assets/ores/textures/dynamics/palettes/base_palette.png", 16);
        }
        return BASE_COLORS;
    }

    private static void createGradient16(int[] result, int colorHigh, Integer colorLow) {
        int rH = (colorHigh >> 16) & 0xFF;
        int gH = (colorHigh >> 8) & 0xFF;
        int bH = colorHigh & 0xFF;

        if (colorLow != null) {
            int rL = (colorLow >> 16) & 0xFF;
            int gL = (colorLow >> 8) & 0xFF;
            int bL = colorLow & 0xFF;

            for (int y = 0; y < 16; y++) {
                float factor = y / 15.0f;
                int r = (int) (rH * (1 - factor) + rL * factor);
                int g = (int) (gH * (1 - factor) + gL * factor);
                int b = (int) (bH * (1 - factor) + bL * factor);
                result[y] = (255 << 24) | (r << 16) | (g << 8) | b;
            }
        } else {
            for (int y = 0; y < 16; y++) {
                int r, g, b;
                if (y < 7) {
                    float factor = y / 7.0f;
                    r = (int) (255 * (1 - factor) + rH * factor);
                    g = (int) (255 * (1 - factor) + gH * factor);
                    b = (int) (255 * (1 - factor) + bH * factor);
                } else {
                    float factor = (y - 7) / 8.0f;
                    r = (int) (rH * (1 - factor));
                    g = (int) (gH * (1 - factor));
                    b = (int) (bH * (1 - factor));
                }
                r = Math.min(255, Math.max(0, r));
                g = Math.min(255, Math.max(0, g));
                b = Math.min(255, Math.max(0, b));
                result[y] = (255 << 24) | (r << 16) | (g << 8) | b;
            }
        }
    }

    private static int[] getMaterialPalette(ores.mathieu.material.Material mat, boolean raw) {
        String name = mat.getName() + (raw ? "_raw" : "");
        Map<String, int[]> cache = raw ? RAW_PALETTES : MAT_PALETTES;
        if (cache.containsKey(name)) return cache.get(name);

        int[] colors = new int[16];
        Integer high = raw ? mat.getRawColorHigh() : mat.getBaseColorHigh();
        Integer low = raw ? mat.getRawColorLow() : mat.getBaseColorLow();
        
        if (raw && high == null) {
            high = mat.getBaseColorHigh() != null ? mat.getBaseColorHigh() : 0xFFFFFF;
            low = mat.getBaseColorLow();
        } else if (!raw && high == null) {
            high = 0xFFFFFF;
        }

        createGradient16(colors, high, low);

        int[] fileColors = readPaletteColors("assets/ores/textures/dynamics/palettes/" + name + ".png", 16);
        if (fileColors.length > 0) {
            colors = fileColors;
        }
        
        cache.put(name, colors);
        return colors;
    }

    private static BufferedImage applyPalette(BufferedImage templateImg, int[] baseColors, int[] matColors) {
        int w = templateImg.getWidth();
        int h = templateImg.getHeight();
        BufferedImage outputImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int pixel = templateImg.getRGB(x, y);
                int a = (pixel >> 24) & 0xFF;
                if (a == 0) {
                    outputImg.setRGB(x, y, 0);
                    continue;
                }

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                int closestIndex = -1;
                int minDist = Integer.MAX_VALUE;
                for (int i = 0; i < baseColors.length; i++) {
                    int bc = baseColors[i];
                    int br = (bc >> 16) & 0xFF;
                    int bg = (bc >> 8) & 0xFF;
                    int bb = bc & 0xFF;
                    int dist = (r - br) * (r - br) + (g - bg) * (g - bg) + (b - bb) * (b - bb);
                    if (dist < minDist) {
                        minDist = dist;
                        closestIndex = i;
                    }
                }

                if (closestIndex != -1 && closestIndex < matColors.length) {
                    int mc = matColors[closestIndex];
                    int mr = (mc >> 16) & 0xFF;
                    int mg = (mc >> 8) & 0xFF;
                    int mb = mc & 0xFF;
                    outputImg.setRGB(x, y, (a << 24) | (mr << 16) | (mg << 8) | mb);
                } else {
                    outputImg.setRGB(x, y, pixel);
                }
            }
        }
        return outputImg;
    }

    private static void applyOverlay(BufferedImage targetImg, String overlayPath) {
        BufferedImage overlayImg = OVERLAY_TEXTURE_CACHE.get(overlayPath);
        if (overlayImg == null) {
            overlayImg = readImageFromJar(overlayPath);
            if (overlayImg != null) {
                OVERLAY_TEXTURE_CACHE.put(overlayPath, overlayImg);
            }
        }
        if (overlayImg == null) return;
        
        int w = targetImg.getWidth();
        int h = targetImg.getHeight();
        for (int x = 0; x < Math.min(w, overlayImg.getWidth()); x++) {
            for (int y = 0; y < Math.min(h, overlayImg.getHeight()); y++) {
                int overPixel = overlayImg.getRGB(x, y);
                int overA = (overPixel >> 24) & 0xFF;
                if (overA == 255) {
                    targetImg.setRGB(x, y, overPixel);
                } else if (overA > 0) {
                    int outPixel = targetImg.getRGB(x, y);
                    int outR = (outPixel >> 16) & 0xFF;
                    int outG = (outPixel >> 8) & 0xFF;
                    int outB = outPixel & 0xFF;
                    int outA = (outPixel >> 24) & 0xFF;

                    int overR = (overPixel >> 16) & 0xFF;
                    int overG = (overPixel >> 8) & 0xFF;
                    int overB = overPixel & 0xFF;

                    float alpha = overA / 255.0f;
                    int finalR = (int) (overR * alpha + outR * (1 - alpha));
                    int finalG = (int) (overG * alpha + outG * (1 - alpha));
                    int finalB = (int) (overB * alpha + outB * (1 - alpha));
                    int finalA = Math.max(outA, overA);

                    targetImg.setRGB(x, y, (finalA << 24) | (finalR << 16) | (finalG << 8) | finalB);
                }
            }
        }
    }

    public static void generateItemTexture(String itemName, ores.mathieu.material.Material material, ores.mathieu.material.ItemType type) {
        String staticPath = "assets/ores/textures/item/" + itemName + ".png";
        try (InputStream is = getResourceAsStream(staticPath)) {
            if (is != null) return; 
        } catch (Exception e) {}

        int[] baseColors = getBaseColors();
        if (baseColors == null || baseColors.length == 0) return;

        boolean useRaw = type.getDefaultOverride().getUseRawColor() != null && type.getDefaultOverride().getUseRawColor();
        int[] matColors = getMaterialPalette(material, useRaw);

        String templatePath = "assets/ores/textures/dynamics/items/" + type.name() + ".png";
        BufferedImage templateImg = readImageFromJar(templatePath);
        if (templateImg == null) return;

        BufferedImage outputImg = applyPalette(templateImg, baseColors, matColors);

        String overlayPath = "assets/ores/textures/dynamics/items/" + type.name() + "_overlay.png";
        applyOverlay(outputImg, overlayPath);

        byte[] pngData = encodePng(outputImg);
        if (pngData != null) {
            VirtualResourcePack.CLIENT.addBytes(staticPath, pngData);
        }
    }

    public static void generateBlockTexture(String blockName, ores.mathieu.material.Material material, ores.mathieu.material.BlockType type) {
        String staticPath = "assets/ores/textures/block/" + blockName + ".png";
        try (InputStream is = getResourceAsStream(staticPath)) {
            if (is != null) return; 
        } catch (Exception e) {}

        if (type == ores.mathieu.material.BlockType.ORE && material.getDebrisName() != null) {
            return;
        }

        int[] baseColors = getBaseColors();
        if (baseColors == null || baseColors.length == 0) return;

        boolean useRaw = type.getDefaultOverride().getUseRawColor() != null && type.getDefaultOverride().getUseRawColor();
        int[] matColors = getMaterialPalette(material, useRaw);

        ores.mathieu.material.BlockType baseType = type;
        while (baseType.getCompressionLevel() > 0 && baseType.getParentType() != null) {
            baseType = baseType.getParentType();
        }

        String templatePath = "assets/ores/textures/dynamics/blocks/" + baseType.name() + ".png";
        BufferedImage templateImg = readImageFromJar(templatePath);
        if (templateImg == null) return;

        BufferedImage outputImg = applyPalette(templateImg, baseColors, matColors);

        String baseOverlayPath = "assets/ores/textures/dynamics/blocks/" + baseType.name() + "_overlay.png";
        applyOverlay(outputImg, baseOverlayPath);

        if (type.getCompressionLevel() > 0) {
            String compOverlayPath = "assets/ores/textures/dynamics/blocks/COMPRESSED_layer_" + type.getCompressionLevel() + ".png";
            applyOverlay(outputImg, compOverlayPath);
        }

        byte[] pngData = encodePng(outputImg);
        if (pngData != null) {
            VirtualResourcePack.CLIENT.addBytes(staticPath, pngData);
        }
    }

    public static void generateOreOverlayTexture(String materialName, ores.mathieu.material.Material material) {
        if (material.getDebrisName() != null) return;

        String outName = materialName + ".png";
        String staticPath = "assets/ores/textures/block/ore_overlays/" + outName;
        try (InputStream is = getResourceAsStream(staticPath)) {
            if (is != null) return;
        } catch (Exception e) {}

        int[] baseColors = getBaseColors();
        if (baseColors == null || baseColors.length == 0) return;

        int[] matColors = getMaterialPalette(material, true);

        String templatePath = "assets/ores/textures/dynamics/ore_overlay/ORE.png";
        BufferedImage templateImg = readImageFromJar(templatePath);
        if (templateImg == null) return;

        BufferedImage outputImg = applyPalette(templateImg, baseColors, matColors);

        String overlayPath = "assets/ores/textures/dynamics/ore_overlay/ORE_overlay.png";
        applyOverlay(outputImg, overlayPath);

        byte[] pngData = encodePng(outputImg);
        if (pngData != null) {
            VirtualResourcePack.CLIENT.addBytes(staticPath, pngData);
        }
    }

    public static void generateOreAssets(String baseBlockId, String oreName, String materialName) {
        String namespace = baseBlockId.contains(":") ? baseBlockId.split(":")[0] : "minecraft";
        String blockName = baseBlockId.contains(":") ? baseBlockId.split(":")[1] : baseBlockId;

        ModelInfo modelInfo = resolveModelInfo(namespace, blockName);

        Map<String, String> generatedTextures = new HashMap<>();

        for (Map.Entry<String, String> entry : modelInfo.textures.entrySet()) {
            String slotKey = entry.getKey();
            String texRef = entry.getValue();

            if (texRef.startsWith("#")) {

                generatedTextures.put(slotKey, texRef);
                continue;
            }

            String compositeTexName = oreName + "_" + slotKey;
            String generatedPath = generateCompositeTexture(texRef, compositeTexName, materialName);

            if (generatedPath != null) {
                generatedTextures.put(slotKey, generatedPath);
            } else {

                generatedTextures.put(slotKey, texRef);
            }
        }

        generateAdvancedModels(baseBlockId, oreName, modelInfo.parent, generatedTextures);
    }

    public static void generateChemicalAssets(String chemicalName, ores.mathieu.material.Material mat, ores.mathieu.material.ChemicalType type) {
        if (mat == null) return;

        String baseName = switch (type) {
            case CLEAN -> "slurry_clean";
            case DIRTY -> "slurry_dirty";
            case SLURRY -> "slurry";
        };

        String baseTexturePath = "assets/ores/textures/chemical/bases/" + baseName + ".png";
        BufferedImage base = readImageFromJar(baseTexturePath);

        if (base == null && type == ores.mathieu.material.ChemicalType.SLURRY) {
            base = readImageFromJar("assets/ores/textures/chemical/bases/slurry_dirty.png");
        }

        if (base != null) {

            int color = -1;
            if (type == ores.mathieu.material.ChemicalType.CLEAN) color = mat.getChemicalCleanColor();
            else if (type == ores.mathieu.material.ChemicalType.DIRTY) color = mat.getChemicalDirtyColor();
            else if (type == ores.mathieu.material.ChemicalType.SLURRY) color = mat.getChemicalSlurryColor();

            if (color == -1) {
                color = mat.getMaterialColor();
            }

            if (color == -1) color = 0xFFFFFF;

            BufferedImage tinted = tintImage(base, color);
            byte[] pngData = encodePng(tinted);
            if (pngData != null) {

                String outPath = "assets/ores/textures/item/" + chemicalName + ".png";
                VirtualResourcePack.CLIENT.addBytes(outPath, pngData);
                OresCoreCommon.LOGGER.debug("[ORES CORE] Generated tinted slurry texture: {} (color: #{})", outPath, Integer.toHexString(color));

                byte[] mcmeta = readBytesFromJar(baseTexturePath + ".mcmeta");
                if (mcmeta != null) {
                    VirtualResourcePack.CLIENT.addBytes(outPath + ".mcmeta", mcmeta);
                }
            }
        } else {
            OresCoreCommon.LOGGER.error("[ORES CORE] Failed to load base slurry texture: {}", baseTexturePath);
        }

        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "ores:item/" + chemicalName);
        model.add("textures", textures);
        VirtualResourcePack.CLIENT.addJson("assets/ores/models/item/" + chemicalName + ".json", model);

        JsonObject itemDef = new JsonObject();
        JsonObject modelDef = new JsonObject();
        modelDef.addProperty("type", "minecraft:model");
        modelDef.addProperty("model", "ores:item/" + chemicalName);
        itemDef.add("model", modelDef);
        VirtualResourcePack.CLIENT.addJson("assets/ores/items/" + chemicalName + ".json", itemDef);
    }

    public static void generateTrimPalette(ores.mathieu.material.Material mat) {
        String name = mat.getName();
        String path = "assets/ores/textures/trims/color_palettes/" + name + ".png";

        try (InputStream is = getResourceAsStream(path)) {
            if (is != null) return;
        } catch (Exception e) {}

        int color = mat.getMaterialColor();
        if (color == -1) color = 0xFFFFFF;

        BufferedImage result = new BufferedImage(8, 1, BufferedImage.TYPE_INT_ARGB);

        float[] multipliers = {0.20f, 0.35f, 0.50f, 0.70f, 0.85f, 1.0f, 1.3f, 1.6f};

        int br = (color >> 16) & 0xFF;
        int bg = (color >> 8) & 0xFF;
        int bb = color & 0xFF;

        for (int x = 0; x < 8; x++) {
            int r = Math.min(255, Math.max(0, (int)(br * multipliers[x])));
            int g = Math.min(255, Math.max(0, (int)(bg * multipliers[x])));
            int b = Math.min(255, Math.max(0, (int)(bb * multipliers[x])));
            result.setRGB(x, 0, (255 << 24) | (r << 16) | (g << 8) | b);
        }

        byte[] pngData = encodePng(result);
        if (pngData != null) {
            VirtualResourcePack.CLIENT.addBytes(path, pngData);
            OresCoreCommon.LOGGER.debug("[ORES CORE] Generated virtual trim palette for: {}", name);
        }
    }

    private static BufferedImage tintImage(BufferedImage image, int color) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = image.getRGB(x, y);
                int a = (argb >> 24) & 0xFF;
                if (a == 0) continue;

                int br = (argb >> 16) & 0xFF;
                int bg = (argb >> 8) & 0xFF;
                int bb = argb & 0xFF;

                int nr = Math.min(255, (int) (br * r));
                int ng = Math.min(255, (int) (bg * g));
                int nb = Math.min(255, (int) (bb * b));

                result.setRGB(x, y, (a << 24) | (nr << 16) | (ng << 8) | nb);
            }
        }
        return result;
    }

    private static void generateAdvancedModels(String baseBlockId, String oreName, String parent, Map<String, String> texturesMap) {

        replicateBlockstate(baseBlockId, oreName);

        JsonObject model = new JsonObject();
        model.addProperty("parent", parent);
        JsonObject textures = new JsonObject();
        for (Map.Entry<String, String> entry : texturesMap.entrySet()) {
            textures.addProperty(entry.getKey(), entry.getValue());
        }
        model.add("textures", textures);
        VirtualResourcePack.CLIENT.addJson("assets/ores/models/block/" + oreName + ".json", model);
        OresCoreCommon.LOGGER.debug("[ORES CORE] Generated advanced block model for {}: parent={}, slots={}", oreName, parent, textures.keySet());

        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", "ores:block/" + oreName);
        VirtualResourcePack.CLIENT.addJson("assets/ores/models/item/" + oreName + ".json", itemModel);

        JsonObject itemDef = new JsonObject();
        JsonObject modelDef = new JsonObject();
        modelDef.addProperty("type", "minecraft:model");
        modelDef.addProperty("model", "ores:item/" + oreName);
        itemDef.add("model", modelDef);
        VirtualResourcePack.CLIENT.addJson("assets/ores/items/" + oreName + ".json", itemDef);
    }

    private static void replicateBlockstate(String baseBlockId, String oreName) {
        String namespace = baseBlockId.contains(":") ? baseBlockId.split(":")[0] : "minecraft";
        String path = baseBlockId.contains(":") ? baseBlockId.split(":")[1] : baseBlockId;
        String statePath = "assets/" + namespace + "/blockstates/" + path + ".json";

        try (InputStream is = getResourceAsStream(statePath)) {
            if (is != null) {
                JsonObject baseState = GSON.fromJson(new InputStreamReader(is), JsonObject.class);
                redirectModelsInJson(baseState, oreName);

                if (baseState.has("variants")) {
                    JsonObject variants = baseState.getAsJsonObject("variants");
                    com.google.gson.JsonArray multipart = new com.google.gson.JsonArray();

                    for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
                        String conditionString = entry.getKey();
                        JsonObject part = new JsonObject();

                        if (!conditionString.isEmpty()) {
                            JsonObject when = new JsonObject();
                            String[] conditions = conditionString.split(",");
                            for (String cond : conditions) {
                                String[] kv = cond.split("=");
                                if (kv.length == 2) when.addProperty(kv[0], kv[1]);
                            }
                            part.add("when", when);
                        }

                        part.add("apply", entry.getValue());
                        multipart.add(part);
                    }

                    baseState.remove("variants");
                    baseState.add("multipart", multipart);
                }

                VirtualResourcePack.CLIENT.addJson("assets/ores/blockstates/" + oreName + ".json", baseState);
                OresCoreCommon.LOGGER.debug("[ORES CORE] Replicated blockstate from {} for {}", statePath, oreName);
                return;
            }
        } catch (Exception e) {
            OresCoreCommon.LOGGER.error("[ORES CORE] Failed to replicate blockstate: " + statePath, e);
        }

        JsonObject fallback = new JsonObject();
        com.google.gson.JsonArray multipart = new com.google.gson.JsonArray();
        JsonObject applyBlock = new JsonObject();
        JsonObject modelRef = new JsonObject();
        modelRef.addProperty("model", "ores:block/" + oreName);
        applyBlock.add("apply", modelRef);
        multipart.add(applyBlock);
        fallback.add("multipart", multipart);
        VirtualResourcePack.CLIENT.addJson("assets/ores/blockstates/" + oreName + ".json", fallback);
        OresCoreCommon.LOGGER.debug("[ORES CORE] Generated multipart fallback blockstate for {}", oreName);
    }

    private static void redirectModelsInJson(JsonElement element, String oreName) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has("model")) {
                obj.addProperty("model", "ores:block/" + oreName);
            }
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                redirectModelsInJson(entry.getValue(), oreName);
            }
        } else if (element.isJsonArray()) {
            for (JsonElement e : element.getAsJsonArray()) {
                redirectModelsInJson(e, oreName);
            }
        }
    }

    private static String generateCompositeTexture(String textureId, String outputName, String materialName) {
        String namespace = textureId.contains(":") ? textureId.split(":")[0] : "minecraft";
        String path = textureId.contains(":") ? textureId.split(":")[1] : textureId;

        if (!path.contains("/")) {
            path = "block/" + path;
        }
        String fullPath = "assets/" + namespace + "/textures/" + path + ".png";

        BufferedImage baseTexture = HOST_BLOCK_TEXTURE_CACHE.get(fullPath);
        if (baseTexture == null) {
            baseTexture = readImageFromJar(fullPath);
            if (baseTexture != null) {
                HOST_BLOCK_TEXTURE_CACHE.put(fullPath, baseTexture);
            } else {
                return null;
            }
        }

        String overlayPath = "assets/ores/textures/block/ore_overlays/" + materialName + ".png";
        BufferedImage overlay = OVERLAY_TEXTURE_CACHE.get(overlayPath);
        if (overlay == null) {
            overlay = readImageFromJar(overlayPath);
            if (overlay != null) {
                OVERLAY_TEXTURE_CACHE.put(overlayPath, overlay);
            }
        }
        if (overlay == null) return null;

        int width = baseTexture.getWidth();
        int height = baseTexture.getHeight();

        int frameHeight = width;

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = result.createGraphics();
        g.drawImage(baseTexture, 0, 0, null);
        g.setComposite(java.awt.AlphaComposite.SrcOver);

        for (int frameY = 0; frameY < height; frameY += frameHeight) {
            int h = Math.min(frameHeight, height - frameY);

            g.drawImage(overlay, 0, frameY, width, frameY + h, 0, 0, overlay.getWidth(), overlay.getHeight(), null);
        }
        g.dispose();

        byte[] pngData = encodePng(result);
        if (pngData != null) {
            String outPath = "assets/ores/textures/block/" + outputName + ".png";
            VirtualResourcePack.CLIENT.addBytes(outPath, pngData);

            String mcmetaPath = fullPath + ".mcmeta";
            byte[] mcmetaData = readBytesFromJar(mcmetaPath);
            if (mcmetaData != null) {
                VirtualResourcePack.CLIENT.addBytes(outPath + ".mcmeta", mcmetaData);
            }

            return "ores:block/" + outputName;
        }
        return null;
    }

    private static ModelInfo resolveModelInfo(String namespace, String blockName) {
        String cacheKey = namespace + ":" + blockName;
        if (MODEL_INFO_CACHE.containsKey(cacheKey)) return MODEL_INFO_CACHE.get(cacheKey);

        Map<String, String> textures = new HashMap<>();
        String currentParent = "minecraft:block/cube_all";
        boolean isTinted = false;

        String nextModel = namespace + ":" + blockName;
        for (int i = 0; i < 5; i++) {
            String nextNs = "minecraft";
            String nextPath = nextModel;
            if (nextModel.contains(":")) {
                String[] parts = nextModel.split(":");
                nextNs = parts[0];
                nextPath = parts[1];
            }
            if (!nextPath.contains("/")) {
                nextPath = "block/" + nextPath;
            }
            String targetPath = "assets/" + nextNs + "/models/" + nextPath + ".json";
            try (InputStream is = getResourceAsStream(targetPath)) {
                if (is != null) {
                    JsonObject model = GSON.fromJson(new InputStreamReader(is), JsonObject.class);

                    if (model.has("textures")) {
                        JsonObject texturesObj = model.getAsJsonObject("textures");
                        for (Map.Entry<String, JsonElement> entry : texturesObj.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue().getAsString();

                            if (!textures.containsKey(key)) {
                                textures.put(key, value);
                            }
                        }
                    }

                    if (model.has("elements")) {
                        JsonArray elements = model.getAsJsonArray("elements");
                        for (JsonElement el : elements) {
                            if (el.isJsonObject()) {
                                JsonObject elObj = el.getAsJsonObject();
                                if (elObj.has("faces")) {
                                    JsonObject faces = elObj.getAsJsonObject("faces");
                                    for (Map.Entry<String, JsonElement> faceEntry : faces.entrySet()) {
                                        if (faceEntry.getValue().isJsonObject() && faceEntry.getValue().getAsJsonObject().has("tintindex")) {
                                            isTinted = true;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (model.has("parent")) {
                        nextModel = model.get("parent").getAsString();
                        if (i == 0) currentParent = nextModel;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            } catch (Exception e) {
                break;
            }
        }

        boolean changed = true;
        int maxResolveLoops = 10;
        while (changed && maxResolveLoops > 0) {
            changed = false;
            maxResolveLoops--;
            for (Map.Entry<String, String> entry : textures.entrySet()) {
                String value = entry.getValue();
                if (value.startsWith("#")) {
                    String refKey = value.substring(1);
                    if (textures.containsKey(refKey) && !textures.get(refKey).startsWith("#")) {
                        textures.put(entry.getKey(), textures.get(refKey));
                        changed = true;
                    }
                }
            }
        }

        if (textures.isEmpty()) {
            textures.put("all", namespace + ":block/" + blockName);
        }

        ModelInfo result = new ModelInfo(currentParent, textures, isTinted);
        MODEL_INFO_CACHE.put(cacheKey, result);
        return result;
    }

    public static boolean isBiomeTinted(String namespace, String blockName) {
        return resolveModelInfo(namespace, blockName).isTinted;
    }

    public static boolean canResolveBaseTexture(String namespace, String blockName) {
        ModelInfo info = resolveModelInfo(namespace, blockName);
        return info.textures.values().stream().anyMatch(v -> !v.startsWith("#"));
    }

    private static class ModelInfo {
        final String parent;
        final Map<String, String> textures;
        final boolean isTinted;

        ModelInfo(String parent, Map<String, String> textures, boolean isTinted) {
            this.parent = parent;
            this.textures = textures;
            this.isTinted = isTinted;
        }
    }

    public static InputStream getResourceAsStream(String path) {
        InputStream is = TextureGenerator.class.getClassLoader().getResourceAsStream(path);
        if (is != null) return is;

        try {
            String cleanPath = path.startsWith("/") ? path.substring(1) : path;
            if (cleanPath.startsWith("assets/")) {
                String[] parts = cleanPath.split("/", 3);
                if (parts.length >= 2) {
                    String namespace = parts[1];
                    InputStream modResource = Services.getPlatform().getModResource(namespace, cleanPath);
                    if (modResource != null) return modResource;
                }
            }
        } catch (Exception e) {
            OresCoreCommon.LOGGER.error("[ORES CORE] Failed to load resource: " + path, e);
        }

        byte[] virtualBytes = VirtualResourcePack.CLIENT.getBytes(path);
        if (virtualBytes != null) return new java.io.ByteArrayInputStream(virtualBytes);

        return null;
    }

    private static BufferedImage readImageFromJar(String path) {
        try (InputStream is = getResourceAsStream(path)) {
            if (is == null) return null;
            return ImageIO.read(is);
        } catch (IOException e) {
            return null;
        }
    }

    private static byte[] readBytesFromJar(String path) {
        try (InputStream is = getResourceAsStream(path)) {
            if (is == null) return null;
            return is.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

    private static byte[] encodePng(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
}
