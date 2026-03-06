//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Detailed editor UI screen for a specific vein configuration.
// Provides inputs for modifying rarity, density, distributions,
// dimensions and other generation parameters.
//
// Author: __mathieu
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ores.mathieu.config.ConfigManager;
import ores.mathieu.material.MaterialsDatabase;
import ores.mathieu.worldgen.VeinConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("null")
public class VeinEditScreen extends Screen {

    private static final String[] SHAPES = {"BLOB", "PLATE", "HORIZONTAL", "VERTICAL", "SCATTERED"};
    private static final String[] DISTRIBUTIONS = {"UNIFORM", "TRAPEZOID", "GAUSSIAN", "TRIANGLE_HIGH", "TRIANGLE_LOW"};
    private static final String[] SPECIFICS = {"NONE", "AIR_ONLY", "CAVE_ONLY", "WATER_ONLY", "LAVA_ONLY", "UNIQUE"};

    private final OreGenConfigScreen parent;
    private final int veinIndex;
    private final VeinConfig originalVein;
    private final VeinConfig vein;
    private final boolean isNew;

    private int scrollOffset = 0;
    private int maxScroll = 0;

    private Button doneButton;
    private EditBox materialsField;
    private final List<EditBox> ratioFields = new ArrayList<>();
    private final List<String> ratioMaterials = new ArrayList<>();
    
    private Button veinTypeBtn;
    private Button shapeBtn;
    private Button distributionBtn;
    private Button specificsBtn;
    private EditBox rarityField;
    private EditBox densityField;
    private EditBox minYField;
    private EditBox maxYField;
    private EditBox airExposureField;
    private EditBox associatedBlockField;
    private EditBox oreDensityField;
    private EditBox bonusBlockField;
    private EditBox bonusBlockChanceField;
    private EditBox dimWhitelistField;
    private EditBox dimBlacklistField;
    private EditBox biomeWhitelistField;
    private EditBox biomeBlacklistField;

    private String materialWarning = "";
    private int materialWarningColor = 0;

    public VeinEditScreen(OreGenConfigScreen parent, int veinIndex, boolean isNew) {
        super(Component.literal(isNew ? "Create Vein" : "Edit Vein #" + (veinIndex + 1)));
        this.parent = parent;
        this.veinIndex = veinIndex;
        this.originalVein = ConfigManager.LOADED_VEINS.get(veinIndex);
        this.vein = this.originalVein.copy();
        this.isNew = isNew;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int fieldW = 200;
        int leftField = centerX - 50;

        materialsField = new EditBox(this.font, leftField, 0, fieldW, 18, Component.literal("Materials"));
        materialsField.setMaxLength(500);
        materialsField.setValue(vein.materials != null ? String.join(", ", vein.materials) : "");
        materialsField.setResponder(this::validateMaterials);
        this.addRenderableWidget(materialsField);

        veinTypeBtn = Button.builder(Component.literal("Type: " + (vein.veinType != null ? vein.veinType : "CLASSIC")), btn -> {
            String current = vein.veinType != null ? vein.veinType : "CLASSIC";
            boolean toGiant = "CLASSIC".equalsIgnoreCase(current);
            vein.veinType = toGiant ? "GIANT" : "CLASSIC";
            btn.setMessage(Component.literal("Type: " + vein.veinType));

            int currentRarity = parseIntSafe(rarityField.getValue(), 10);
            if (toGiant && currentRarity < 50) {
                rarityField.setValue("200");
            } else if (!toGiant && currentRarity > 50) {
                rarityField.setValue("8");
            }
        }).bounds(leftField, 0, fieldW, 20).build();
        this.addRenderableWidget(veinTypeBtn);

        shapeBtn = Button.builder(Component.literal("Shape: " + vein.shape), btn -> {
            int idx = indexOf(SHAPES, vein.shape);
            vein.shape = SHAPES[(idx + 1) % SHAPES.length];
            btn.setMessage(Component.literal("Shape: " + vein.shape));
        }).bounds(leftField, 0, fieldW, 20).build();
        this.addRenderableWidget(shapeBtn);

        distributionBtn = Button.builder(Component.literal("Distribution: " + vein.distribution), btn -> {
            int idx = indexOf(DISTRIBUTIONS, vein.distribution);
            vein.distribution = DISTRIBUTIONS[(idx + 1) % DISTRIBUTIONS.length];
            btn.setMessage(Component.literal("Distribution: " + vein.distribution));
        }).bounds(leftField, 0, fieldW, 20).build();
        this.addRenderableWidget(distributionBtn);

        specificsBtn = Button.builder(Component.literal("Specifics: " + (vein.specifics != null ? vein.specifics : "NONE")), btn -> {
            int idx = indexOf(SPECIFICS, vein.specifics != null ? vein.specifics : "NONE");
            vein.specifics = SPECIFICS[(idx + 1) % SPECIFICS.length];
            btn.setMessage(Component.literal("Specifics: " + vein.specifics));
        }).bounds(leftField, 0, fieldW, 20).build();
        this.addRenderableWidget(specificsBtn);

        rarityField = createIntField("Rarity", String.valueOf(vein.rarity));
        densityField = createIntField("Density", String.valueOf(vein.density));
        minYField = createIntField("Min Y", String.valueOf(vein.minY));
        maxYField = createIntField("Max Y", String.valueOf(vein.maxY));
        
        airExposureField = createFloatField("Air Exposure", String.valueOf(vein.airExposureChance), true);

        associatedBlockField = createTextField("Associated Block", vein.associatedBlock != null ? vein.associatedBlock : "");
        oreDensityField = createFloatField("Ore Density", String.valueOf(vein.oreDensity), true);
        bonusBlockField = createTextField("Bonus Block", vein.bonusBlock != null ? vein.bonusBlock : "");
        bonusBlockChanceField = createFloatField("Bonus Chance", String.valueOf(vein.bonusBlockChance), true);

        dimWhitelistField = createTextField("Dim Whitelist", listToString(vein.dimensionsWhitelist));
        dimBlacklistField = createTextField("Dim Blacklist", listToString(vein.dimensionsBlacklist));
        biomeWhitelistField = createTextField("Biome Whitelist", listToString(vein.biomesWhitelist));
        biomeBlacklistField = createTextField("Biome Blacklist", listToString(vein.biomesBlacklist));

        validateMaterials(materialsField.getValue());

        doneButton = Button.builder(Component.literal("Done"), btn -> {
            applyValues();
            ConfigManager.LOADED_VEINS.set(this.veinIndex, this.vein);
            parent.applyFilters();
            this.minecraft.setScreen(parent);
        }).bounds(this.width / 2 - 50, this.height - 30, 100, 20).build();
        this.addRenderableWidget(doneButton);
    }

    private EditBox createIntField(String label, String value) {
        EditBox box = new EditBox(this.font, this.width / 2 - 50, 0, 200, 18, Component.literal(label));
        box.setMaxLength(20);
        box.setValue(value);
        final String[] lastValid = {value};
        box.setResponder(s -> {
            if (s.isEmpty() || s.equals("-") || s.matches("-?\\d+")) {
                lastValid[0] = s;
            } else if (!s.equals(lastValid[0])) {
                box.setValue(lastValid[0]);
            }
        });
        this.addRenderableWidget(box);
        return box;
    }

    private EditBox createFloatField(String label, String value, boolean clamped01) {
        EditBox box = new EditBox(this.font, this.width / 2 - 50, 0, 200, 18, Component.literal(label));
        box.setMaxLength(20);
        box.setValue(value);
        final String[] lastValid = {value};
        box.setResponder(s -> {
            boolean valid = true;
            if (!s.isEmpty() && !s.equals("-") && !s.equals(".")) {
                if (!s.matches("-?\\d*\\.?\\d*")) {
                    valid = false;
                } else if (clamped01) {
                    try {
                        float f = Float.parseFloat(s);
                        if (f < 0.0f || f > 1.0f) valid = false;
                    } catch (NumberFormatException e) {
                        valid = true;
                    }
                }
            }
            if (valid) {
                lastValid[0] = s;
            } else if (!s.equals(lastValid[0])) {
                box.setValue(lastValid[0]);
            }
        });
        this.addRenderableWidget(box);
        return box;
    }

    private EditBox createTextField(String label, String value) {
        EditBox box = new EditBox(this.font, this.width / 2 - 50, 0, 200, 18, Component.literal(label));
        box.setMaxLength(500);
        box.setValue(value);
        this.addRenderableWidget(box);
        return box;
    }

    private void validateMaterials(String input) {
        String[] matsStrings = input.split(",");
        List<String> validMats = new ArrayList<>();
        
        materialWarning = "";
        materialWarningColor = 0;

        for (String raw : matsStrings) {
            String mat = raw.trim();
            if (mat.isEmpty()) continue;
            validMats.add(mat);

            if (materialWarning.isEmpty()) {
                if (MaterialsDatabase.get(mat) == null) {
                    materialWarning = "✘ \"" + mat + "\" does not exist in Ores Core!";
                    materialWarningColor = 0xFFFF5555;
                } else {
                    boolean hasOre = ores.mathieu.api.OresCoreAPI.isOreMaterialAdded(mat);
                    if (!hasOre) {
                        materialWarning = "⚠ \"" + mat + "\" exists but has no ore yet.\nRestart required.";
                        materialWarningColor = 0xFFFFAA00;
                    }
                }
            }
        }

        if (validMats.size() <= 1) {
            for (EditBox rb : ratioFields) this.removeWidget(rb);
            ratioFields.clear();
            ratioMaterials.clear();
        } else if (!ratioMaterials.equals(validMats)) {
            for (EditBox rb : ratioFields) this.removeWidget(rb);
            ratioFields.clear();
            ratioMaterials.clear();
            ratioMaterials.addAll(validMats);

            for (int i = 0; i < ratioMaterials.size(); i++) {
                EditBox box = new EditBox(this.font, 0, 0, 40, 18, Component.literal("Ratio " + i));
                box.setMaxLength(5);
                
                String initialValue = "1";
                if (vein != null && vein.materialsRatio != null && i < vein.materialsRatio.size()) {
                    initialValue = String.valueOf(vein.materialsRatio.get(i));
                }
                box.setValue(initialValue);
                
                final String[] lastValid = {initialValue};
                box.setResponder(s -> {
                    if (s.isEmpty() || s.matches("\\d+")) {
                        lastValid[0] = s;
                    } else if (!s.equals(lastValid[0])) {
                        box.setValue(lastValid[0]);
                    }
                });
                
                ratioFields.add(box);
                this.addRenderableWidget(box);
            }
        }
    }

    private void applyValues() {
        String matsStr = materialsField.getValue().trim();
        if (!matsStr.isEmpty()) {
            vein.materials = Arrays.stream(matsStr.split(","))
                    .map(String::trim).filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        if (ratioFields.size() > 1) {
            vein.materialsRatio = ratioFields.stream()
                .map(EditBox::getValue)
                .map(s -> parseIntSafe(s, 1))
                .collect(Collectors.toList());
        } else {
            vein.materialsRatio = null;
        }

        vein.rarity = parseIntSafe(rarityField.getValue(), vein.rarity);
        vein.density = parseIntSafe(densityField.getValue(), vein.density);
        vein.minY = parseIntSafe(minYField.getValue(), vein.minY);
        vein.maxY = parseIntSafe(maxYField.getValue(), vein.maxY);
        vein.airExposureChance = parseFloatSafe(airExposureField.getValue(), vein.airExposureChance);

        vein.associatedBlock = associatedBlockField.getValue().trim().isEmpty() ? null : associatedBlockField.getValue().trim();
        vein.oreDensity = parseFloatSafe(oreDensityField.getValue(), vein.oreDensity);
        vein.bonusBlock = bonusBlockField.getValue().trim().isEmpty() ? null : bonusBlockField.getValue().trim();
        vein.bonusBlockChance = parseFloatSafe(bonusBlockChanceField.getValue(), vein.bonusBlockChance);

        vein.dimensionsWhitelist = stringToList(dimWhitelistField.getValue());
        vein.dimensionsBlacklist = stringToList(dimBlacklistField.getValue());
        vein.biomesWhitelist = stringToList(biomeWhitelistField.getValue());
        vein.biomesBlacklist = stringToList(biomeBlacklistField.getValue());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 0xFFFFFFFF);

        int leftLabel = this.width / 2 - 140;
        int leftField = this.width / 2 - 50;
        boolean isClassic = !"GIANT".equalsIgnoreCase(vein.veinType);

        int y = 28 - scrollOffset;
        int rowHeight = 24;

        boolean hasErrors = false;
        String matsStr = materialsField.getValue().trim();
        if (matsStr.isEmpty() || materialWarningColor == 0xFFFF5555) hasErrors = true;
        
        boolean validDensity = !densityField.getValue().isEmpty();
        boolean validRarity = !rarityField.getValue().isEmpty();
        boolean validMinY = !minYField.getValue().isEmpty();
        boolean validMaxY = !maxYField.getValue().isEmpty();
        boolean validOreDensity = !oreDensityField.getValue().isEmpty();

        if (isClassic) {
            if (!validDensity) hasErrors = true;
        } else {
            String assocBlock = associatedBlockField.getValue().trim();
            if (assocBlock.isEmpty() || !ores.mathieu.api.OresCoreAPI.isHostBlock(assocBlock)) hasErrors = true;
            if (!validOreDensity) hasErrors = true;
        }
        
        if (!validRarity || !validMinY || !validMaxY) hasErrors = true;

        doneButton.active = !hasErrors;

        int matsLabelColor = matsStr.isEmpty() || materialWarningColor == 0xFFFF5555 ? 0xFFFF5555 : 0xFFCCCCCC;
        y = drawRow(graphics, "Materials:", materialsField, leftLabel, leftField, y, rowHeight, matsLabelColor);
        
        if (!materialWarning.isEmpty() && y > 4 && y < this.height - 50) {
            List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(net.minecraft.network.chat.FormattedText.of(materialWarning), 340);
            for (net.minecraft.util.FormattedCharSequence line : lines) {
                graphics.drawString(this.font, line, leftField, y, materialWarningColor);
                y += 10;
            }
            y += 4;
        }

        if (!ratioFields.isEmpty()) {
            if (y > -20 && y < this.height) {
                graphics.drawString(this.font, "Ratios:", leftLabel, y + 14, 0xFFCCCCCC);
            }
            int rx = leftField;
            int itemsOnLine = 0;
            for (int i = 0; i < ratioFields.size(); i++) {
                if (itemsOnLine >= 4) {
                    y += rowHeight + 4;
                    rx = leftField;
                    itemsOnLine = 0;
                }
                EditBox rbox = ratioFields.get(i);
                if (y > -20 && y < this.height) {
                    String matPrefix = ratioMaterials.get(i);
                    if (matPrefix.length() > 6) matPrefix = matPrefix.substring(0, 5) + ".";
                    graphics.drawString(this.font, matPrefix, rx, y - 2, 0xFF888888);
                    rbox.setX(rx);
                    rbox.setY(y + 8);
                } else {
                    rbox.setY(-100);
                }
                rx += 50;
                itemsOnLine++;
            }
            y += rowHeight + 8;
        }

        y = drawButtonRow(graphics, "Vein Type:", veinTypeBtn, leftLabel, leftField, y, rowHeight);

        if (isClassic) {
            y = drawButtonRow(graphics, "Shape:", shapeBtn, leftLabel, leftField, y, rowHeight);
            y = drawButtonRow(graphics, "Distribution:", distributionBtn, leftLabel, leftField, y, rowHeight);
            y = drawRow(graphics, "Density:", densityField, leftLabel, leftField, y, rowHeight, validDensity ? 0xFFCCCCCC : 0xFFFF5555);
        } else {
            String assocBlock = associatedBlockField.getValue().trim();
            int assocLabelColor = assocBlock.isEmpty() ? 0xFFFF5555 : 0xFFCCCCCC;
            
            if (y > 4 && y < this.height - 50) {
                graphics.drawString(this.font, "Assoc. Block:", leftLabel, y + 4, assocLabelColor);
                associatedBlockField.setY(y);
                associatedBlockField.setX(leftField);
            } else {
                associatedBlockField.setY(-100);
            }
            y += rowHeight;
            
            if (!assocBlock.isEmpty() && !ores.mathieu.api.OresCoreAPI.isHostBlock(assocBlock)) {
                if (y > 4 && y < this.height - 50) {
                    graphics.drawString(this.font, "⚠ Not a registered host block.", leftField, y - rowHeight + 20, 0xFFFFAA00);
                }
                y += 10;
            }
            y = drawRow(graphics, "Ore Density:", oreDensityField, leftLabel, leftField, y, rowHeight, validOreDensity ? 0xFFCCCCCC : 0xFFFF5555);
        }

        y = drawRow(graphics, "Rarity:", rarityField, leftLabel, leftField, y, rowHeight, validRarity ? 0xFFCCCCCC : 0xFFFF5555);
        
        if (y > 4 && y < this.height - 50) {
            boolean validY = validMinY && validMaxY;
            graphics.drawString(this.font, "Min / Max Y:", leftLabel, y + 4, validY ? 0xFFCCCCCC : 0xFFFF5555);
            minYField.setWidth(95);
            minYField.setX(leftField);
            minYField.setY(y);
            maxYField.setWidth(95);
            maxYField.setX(leftField + 105);
            maxYField.setY(y);
        } else {
            minYField.setY(-100);
            maxYField.setY(-100);
        }
        y += rowHeight;

        if (isClassic) {
            y = drawRow(graphics, "Air Exposure:", airExposureField, leftLabel, leftField, y, rowHeight, 0xFFCCCCCC);
            y = drawButtonRow(graphics, "Specifics:", specificsBtn, leftLabel, leftField, y, rowHeight);
        } else {
            y = drawRow(graphics, "Bonus Block:", bonusBlockField, leftLabel, leftField, y, rowHeight, 0xFFCCCCCC);
            if (!bonusBlockField.getValue().trim().isEmpty()) {
                bonusBlockChanceField.visible = true;
                y = drawRow(graphics, "Bonus Chance:", bonusBlockChanceField, leftLabel, leftField, y, rowHeight, 0xFFCCCCCC);
            } else {
                bonusBlockChanceField.visible = false;
                bonusBlockChanceField.setY(-100);
            }
        }

        y += 6;
        if (y > 4 && y < this.height - 50) {
            graphics.drawString(this.font, "§e--- Filters ---", leftLabel, y, 0xFFFFFFAA);
        }
        y += 14;

        y = drawRow(graphics, "Dim Whitelist:", dimWhitelistField, leftLabel, leftField, y, rowHeight, 0xFFCCCCCC);
        y = drawRow(graphics, "Dim Blacklist:", dimBlacklistField, leftLabel, leftField, y, rowHeight, 0xFFCCCCCC);
        y = drawRow(graphics, "Biome Whitelist:", biomeWhitelistField, leftLabel, leftField, y, rowHeight, 0xFFCCCCCC);
        y = drawRow(graphics, "Biome Blacklist:", biomeBlacklistField, leftLabel, leftField, y, rowHeight, 0xFFCCCCCC);

        shapeBtn.visible = isClassic;
        distributionBtn.visible = isClassic;
        densityField.visible = isClassic;
        airExposureField.visible = isClassic;
        specificsBtn.visible = isClassic;

        associatedBlockField.visible = !isClassic;
        oreDensityField.visible = !isClassic;
        bonusBlockField.visible = !isClassic;
        bonusBlockChanceField.visible = !isClassic;

        int actualHeight = (y + scrollOffset) - 28;
        int visibleArea = this.height - 60; 
        this.maxScroll = Math.max(0, actualHeight - visibleArea);
    }

    private int drawRow(GuiGraphics graphics, String label, EditBox field, int labelX, int fieldX, int y, int rowH, int color) {
        if (y > 4 && y < this.height - 50) {
            graphics.drawString(this.font, label, labelX, y + 4, color);
            field.setY(y);
            field.setX(fieldX);
        } else {
            field.setY(-100);
        }
        return y + rowH;
    }

    private int drawButtonRow(GuiGraphics graphics, String label, Button btn, int labelX, int fieldX, int y, int rowH) {
        if (y > 4 && y < this.height - 50) {
            graphics.drawString(this.font, label, labelX, y + 5, 0xFFCCCCCC);
            btn.setY(y);
            btn.setX(fieldX);
        } else {
            btn.setY(-100);
        }
        return y + rowH;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scrollOffset -= (int) (scrollY * 16);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
        return true;
    }

    @Override
    public void onClose() {
        if (!doneButton.active) {
            if (isNew) {
                ConfigManager.LOADED_VEINS.remove(this.veinIndex);
            }
        } else {
            applyValues();
            ConfigManager.LOADED_VEINS.set(this.veinIndex, this.vein);
        }
        parent.applyFilters();
        this.minecraft.setScreen(parent);
    }

    private static int indexOf(String[] arr, String val) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equalsIgnoreCase(val)) return i;
        }
        return 0;
    }

    private static int parseIntSafe(String s, int fallback) {
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return fallback; }
    }

    private static float parseFloatSafe(String s, float fallback) {
        try { return Float.parseFloat(s.trim()); }
        catch (NumberFormatException e) { return fallback; }
    }

    private static String listToString(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return String.join(", ", list);
    }

    private static List<String> stringToList(String s) {
        if (s == null || s.trim().isEmpty()) return new ArrayList<>();
        return Arrays.stream(s.split(","))
                .map(String::trim).filter(str -> !str.isEmpty())
                .collect(Collectors.toList());
    }
}
