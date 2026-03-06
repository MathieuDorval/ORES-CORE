//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Configuration UI screen for defining and modifying
// overrides for materials, items, blocks, and chemicals properties.
// Edits are saved to properties.toml.
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import ores.mathieu.config.ConfigManager;
import ores.mathieu.material.*;

import java.util.*;

@SuppressWarnings("null")
public class PropertiesConfigScreen extends Screen {
    private enum Category {
        MATERIALS("Materials"),
        ITEMS("Items"),
        BLOCKS("Blocks"),
        CHEMICALS("Chemicals");

        private final String label;
        Category(String label) { this.label = label; }
    }

    private final Screen lastScreen;
    private Category currentCategory = Category.MATERIALS;
    private final List<String> entryIds = new ArrayList<>();
    private String selectedId;
    private boolean selectionMode = false;
    private boolean needsRestart = false;
    private boolean translationMode = false;
    
    private int scrollOffset = 0;
    private static final int ROW_HEIGHT = 20;
    private static final int LIST_WIDTH = 150;
    private static final int LIST_TOP = 50;

    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;
    private final List<PropertyWidget> propertyWidgets = new ArrayList<>();
    private int propertiesScrollOffset = 0;

    public PropertiesConfigScreen(Screen lastScreen) {
        super(Component.literal("Ores Core Properties Configuration"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        int buttonWidth = 80;
        int totalWidth = buttonWidth * 4 + 15;
        int startX = (this.width - totalWidth) / 2;

        for (Category cat : Category.values()) {
            this.addRenderableWidget(Button.builder(Component.literal(cat.label), b -> setCategory(cat))
                    .bounds(startX + cat.ordinal() * (buttonWidth + 5), 20, buttonWidth, 20)
                    .build());
        }

        panelX = 180;
        panelY = 50;
        panelWidth = this.width - 200;
        panelHeight = this.height - 100;

        this.addRenderableWidget(Button.builder(Component.literal("Done"), b -> this.onClose())
                .bounds(this.width / 2 - 105, this.height - 25, 100, 20)
                .build());

        this.addRenderableWidget(Button.builder(Component.literal("Reset Entry"), b -> resetSelectedEntry())
                .bounds(this.width / 2 + 5, this.height - 25, 100, 20)
                .build());

        refreshList();
        checkNeedsRestart();
    }

    private void setCategory(Category cat) {
        this.currentCategory = cat;
        this.selectedId = null;
        this.selectionMode = false;
        this.translationMode = false;
        this.propertyWidgets.forEach(w -> this.removeWidget(w.widget));
        this.propertyWidgets.clear();
        this.scrollOffset = 0;
        this.propertiesScrollOffset = 0;
        refreshList();
    }

    private final List<Button> entryButtons = new ArrayList<>();

    private void refreshList() {
        entryIds.clear();
        for (Button b : entryButtons) this.removeWidget(b);
        entryButtons.clear();

        Set<String> ids = new TreeSet<>();
        if (selectionMode && selectedId != null) {
            ids.addAll(getAvailableKeys());
        } else {
            if (currentCategory == Category.MATERIALS) {
                for (String matName : MaterialsDatabase.MATERIALS.keySet()) {
                    if (isMaterialPresent(matName)) ids.add(matName);
                }
            } else if (currentCategory == Category.ITEMS) {
                for (ItemType type : ItemType.values()) {
                    if (isItemTypePresent(type)) ids.add(type.getSuffix());
                }
            } else if (currentCategory == Category.BLOCKS) {
                for (BlockType type : BlockType.values()) {
                    if (type == BlockType.ORE) continue;
                    if (isBlockTypePresent(type)) ids.add(type.getSuffix());
                }
            } else if (currentCategory == Category.CHEMICALS) {
                for (ChemicalType type : ChemicalType.values()) {
                    if (isChemicalTypePresent(type)) ids.add(type.getSuffix());
                }
            }
        }
        entryIds.addAll(ids);

        for (String id : entryIds) {
            Button b = Button.builder(Component.literal(id), btn -> {
                if (selectionMode) {
                    addSelectedProperty(id);
                } else {
                    selectEntry(id);
                }
            }).bounds(20, -100, LIST_WIDTH, ROW_HEIGHT).build();
            entryButtons.add(b);
            this.addRenderableWidget(b);
        }
    }

    private boolean isMaterialPresent(String matName) {
        Material mat = MaterialsDatabase.get(matName);
        if (mat == null) return false;
        
        for (ItemType type : ItemType.values()) {
            if (isObjectPresent(mat, type)) return true;
        }
        for (BlockType type : BlockType.values()) {
            if (isObjectPresent(mat, type)) return true;
        }
        return false;
    }

    private boolean isObjectPresent(Material mat, Enum<?> type) {
        if (type instanceof ItemType it) {
            String baseName = it.name().equalsIgnoreCase(mat.getBaseType()) ? mat.getBaseItemName() : mat.getName();
            String id = String.format(it.getDefaultOverride().getNamingPattern(), baseName);
            return BuiltInRegistries.ITEM.containsKey(Identifier.fromNamespaceAndPath("ores", id)) || 
                   BuiltInRegistries.ITEM.containsKey(Identifier.fromNamespaceAndPath("minecraft", id));
        } else if (type instanceof BlockType bt) {
            if (bt == BlockType.ORE) {
                String prefix = mat.getName() + "_ore_";
                for (Identifier id : BuiltInRegistries.BLOCK.keySet()) {
                    if ((id.getNamespace().equals("ores") || id.getNamespace().equals("minecraft")) && id.getPath().startsWith(prefix)) return true;
                }
                String simpleOre = mat.getName() + "_ore";
                return BuiltInRegistries.BLOCK.containsKey(Identifier.fromNamespaceAndPath("ores", simpleOre)) || 
                       BuiltInRegistries.BLOCK.containsKey(Identifier.fromNamespaceAndPath("minecraft", simpleOre));
            }
            String id = String.format(bt.getDefaultOverride().getNamingPattern(), mat.getName());
            return BuiltInRegistries.BLOCK.containsKey(Identifier.fromNamespaceAndPath("ores", id)) || 
                   BuiltInRegistries.BLOCK.containsKey(Identifier.fromNamespaceAndPath("minecraft", id));
        } else if (type instanceof ChemicalType) {
            return isMaterialPresent(mat.getName());
        }
        return false;
    }

    private boolean isItemTypePresent(ItemType type) {
        for (Material mat : MaterialsDatabase.MATERIALS.values()) {
            if (isObjectPresent(mat, type)) return true;
        }
        return false;
    }

    private boolean isBlockTypePresent(BlockType type) {
        for (Material mat : MaterialsDatabase.MATERIALS.values()) {
            if (isObjectPresent(mat, type)) return true;
        }
        return false;
    }

    private boolean isChemicalTypePresent(ChemicalType type) {
        for (Material mat : MaterialsDatabase.MATERIALS.values()) {
            if (isMaterialPresent(mat.getName())) return true;
        }
        return false;
    }

    private void addSelectedProperty(String key) {
        updateProperty(key, "");
        this.selectionMode = false;
        refreshList();
        rebuildProperties();
    }

    private List<String> getAvailableKeys() {
        List<String> all;
        if (currentCategory == Category.MATERIALS) {
            all = List.of("maxStackSize", "fuelTime", "blockHardnessFactor", "blockResistanceFactor", "miningLevel", "oreHardnessFactor", "oreResistanceFactor", "fireproof", "beacon", "trimmable", "rawColorHigh", "baseColorHigh");
        } else if (currentCategory == Category.ITEMS) {
            all = List.of("maxStackSize", "fuelFactor", "smeltingMultiplier", "xpMultiplier", "rarity", "canBeFireproof", "canBeBeaconPayment", "canBePiglinLoved", "canBeTrimmable", "useRawColor");
        } else if (currentCategory == Category.BLOCKS) {
            all = List.of("hardness", "resistance", "lightLevel", "lightMode", "redstoneFactor", "redstonePower", "slipperiness", "speedFactor", "jumpFactor", "isFlammable", "isReplaceable", "noCollision");
        } else {
            all = List.of("chemicalCleanColor", "chemicalDirtyColor", "chemicalSlurryColor");
        }
        
        Map<String, String> current = new HashMap<>(getDefaultProperties(selectedId));
        current.putAll(getCurrentOverrides(selectedId));
        
        List<String> available = new ArrayList<>();
        for (String s : all) if (!current.containsKey(s)) available.add(s);
        return available;
    }

    private void selectEntry(String id) {
        this.selectedId = id;
        this.propertiesScrollOffset = 0;
        this.translationMode = false;
        rebuildProperties();
    }

    private void updateEntryButtonPositions() {
        int y = LIST_TOP - scrollOffset;
        for (Button b : entryButtons) {
            if (y + ROW_HEIGHT > LIST_TOP && y < this.height - 40) {
                b.setY(y);
                b.visible = true;
            } else {
                b.setY(-100);
                b.visible = false;
            }
            y += ROW_HEIGHT;
        }
    }

    private void rebuildProperties() {
        this.propertyWidgets.forEach(w -> this.removeWidget(w.widget));
        this.propertyWidgets.clear();
        if (selectedId == null) return;

        Map<String, String> defaultProps = getDefaultProperties(selectedId);
        Map<String, String> currentOverrides = getCurrentOverrides(selectedId);
        
        List<String> sortedKeys = new ArrayList<>(defaultProps.keySet());
        for (String k : currentOverrides.keySet()) {
            if (!sortedKeys.contains(k)) sortedKeys.add(k);
        }

        sortedKeys.sort((a, b) -> {
            boolean aLang = a.startsWith("name");
            boolean bLang = b.startsWith("name");
            if (aLang && !bLang) return 1;
            if (!aLang && bLang) return -1;
            return 0;
        });

        int y = panelY + 10;
        boolean hasLanguages = sortedKeys.stream().anyMatch(k -> k.startsWith("name"));

        if (translationMode) {
            Button backBtn = Button.builder(Component.literal("<- Back to Properties"), b -> {
                this.translationMode = false;
                this.propertiesScrollOffset = 0;
                rebuildProperties();
            }).bounds(panelX, y, panelWidth - 20, 16).build();
            this.propertyWidgets.add(new PropertyWidget("__BACK__", backBtn, false));
            this.addRenderableWidget(backBtn);
            y += 24;
        }

        for (String key : sortedKeys) {
            boolean isLang = key.startsWith("name");
            if (translationMode != isLang) continue;

            String defaultValue = defaultProps.getOrDefault(key, "");
            String currentValue = currentOverrides.getOrDefault(key, defaultValue);
            boolean isOverridden = currentOverrides.containsKey(key);

            net.minecraft.client.gui.components.AbstractWidget widget;

            if (defaultValue.equals("true") || defaultValue.equals("false")) {
                Button btn = Button.builder(Component.literal(currentValue), b -> {
                    String newVal = b.getMessage().getString().equals("true") ? "false" : "true";
                    b.setMessage(Component.literal(newVal));
                    updateProperty(key, newVal);
                }).bounds(panelX + 120, y, 60, 16).build();
                widget = btn;
            } else if (key.equals("rarity")) {
                List<String> rarities = List.of("COMMON", "UNCOMMON", "RARE", "EPIC");
                String current = currentValue.toUpperCase();
                Button btn = Button.builder(Component.literal(current), b -> {
                    String next = rarities.get((rarities.indexOf(b.getMessage().getString().toUpperCase()) + 1) % rarities.size());
                    b.setMessage(Component.literal(next));
                    updateProperty(key, next);
                }).bounds(panelX + 120, y, 80, 16).build();
                widget = btn;
            } else {
                EditBox editBox = new EditBox(this.minecraft.font, panelX + 120, y, panelWidth - 140, 16, Component.literal(key));
                editBox.setValue(currentValue);
                editBox.setResponder(s -> {
                    if (isNumberProp(key)) {
                        if (key.equals("maxStackSize")) {
                            try {
                                if (!s.isEmpty()) {
                                    int val = Integer.parseInt(s);
                                    if (val < 1) editBox.setValue("1");
                                    else if (val > 64) editBox.setValue("64");
                                }
                            } catch (NumberFormatException e) {
                                String cleaned = s.replaceAll("[^0-9]", "");
                                if (!cleaned.equals(s)) editBox.setValue(cleaned);
                            }
                        } else {
                            String pattern = key.contains("Factor") || key.contains("hardness") || key.contains("resistance") ? "[^0-9.]" : "[^0-9-]";
                            String cleaned = s.replaceAll(pattern, "");
                            if (!cleaned.equals(s)) editBox.setValue(cleaned);
                        }
                    }
                    updateProperty(key, editBox.getValue());
                });
                widget = editBox;
            }
            
            this.propertyWidgets.add(new PropertyWidget(key, widget, isOverridden));
            this.addRenderableWidget(widget);
            y += 20;
        }

        if (!translationMode && hasLanguages) {
            y += 5;
            Button langBtn = Button.builder(Component.literal("Manage Translations..."), b -> {
                this.translationMode = true;
                this.propertiesScrollOffset = 0;
                rebuildProperties();
            }).bounds(panelX, y, panelWidth - 20, 16).build();
            this.propertyWidgets.add(new PropertyWidget("__LANG__", langBtn, false));
            this.addRenderableWidget(langBtn);
            y += 20;
        }

        List<String> available = getAvailableKeys();
        if (!translationMode && !available.isEmpty()) {
            y += 5;
            Button addBtn = Button.builder(Component.literal("+ Add Property"), b -> {
                this.selectionMode = !this.selectionMode;
                this.scrollOffset = 0;
                refreshList();
            }).bounds(panelX, y, panelWidth - 20, 16).build();
            
            this.propertyWidgets.add(new PropertyWidget("__ADD_BTN__", addBtn, false));
            this.addRenderableWidget(addBtn);
        }
    }

    private boolean isNumberProp(String key) {
        return key.toLowerCase().contains("size") || key.toLowerCase().contains("level") || key.toLowerCase().contains("time") || 
               key.toLowerCase().contains("factor") || key.toLowerCase().contains("hardness") || key.toLowerCase().contains("resistance") ||
               key.toLowerCase().contains("rarity") || key.toLowerCase().contains("color");
    }

    private void updateProperty(String key, String value) {
        if (selectedId == null) return;
        Map<String, String> defaultProps = getDefaultProperties(selectedId);

        Map<String, Map<String, String>> targetMap = getTargetOverrideMap();
        Map<String, String> entryOverrides = targetMap.computeIfAbsent(selectedId, k -> new HashMap<>());

        if (defaultProps.containsKey(key)) {
            String def = defaultProps.get(key);
            if (value.equals(def)) {
                entryOverrides.remove(key);
                if (entryOverrides.isEmpty()) targetMap.remove(selectedId);
            } else {
                entryOverrides.put(key, value);
            }
        } else {
            entryOverrides.put(key, value);
        }
        
        checkNeedsRestart();
        
        for (PropertyWidget w : propertyWidgets) {
            if (w.key.equals(key)) {
                w.isOverridden = entryOverrides.containsKey(key);
                break;
            }
        }
    }

    private void checkNeedsRestart() {
        this.needsRestart = scanMapForRestart(ConfigManager.getOverrideMaterials()) ||
                            scanMapForRestart(ConfigManager.getOverrideItems()) ||
                            scanMapForRestart(ConfigManager.getOverrideBlocks()) ||
                            scanMapForRestart(ConfigManager.getOverrideChemicals());
    }

    private boolean scanMapForRestart(java.util.Map<String, java.util.Map<String, String>> map) {
        for (java.util.Map<String, String> entry : map.values()) {
            for (String key : entry.keySet()) {
                if (isRestartRequiredProp(key)) return true;
            }
        }
        return false;
    }

    private boolean isRestartRequiredProp(String key) {
        if (key.startsWith("name")) return false;
        return true;
    }

    private void resetSelectedEntry() {
        if (selectedId == null) return;
        getTargetOverrideMap().remove(selectedId);
        checkNeedsRestart();
        rebuildProperties();
    }

    private Map<String, Map<String, String>> getTargetOverrideMap() {
        return switch (currentCategory) {
            case MATERIALS -> ConfigManager.getOverrideMaterials();
            case ITEMS -> ConfigManager.getOverrideItems();
            case BLOCKS -> ConfigManager.getOverrideBlocks();
            case CHEMICALS -> ConfigManager.getOverrideChemicals();
        };
    }

    private Map<String, String> getCurrentOverrides(String id) {
        return getTargetOverrideMap().getOrDefault(id, Collections.emptyMap());
    }

    private Map<String, String> getDefaultProperties(String id) {
        Map<String, String> props = new java.util.LinkedHashMap<>();
        if (currentCategory == Category.MATERIALS) {
            Material mat = MaterialsDatabase.MATERIALS.get(id);
            if (mat != null) {
                addMatProp(props, "maxStackSize", mat.getMaxStackSize());
                addMatProp(props, "fuelTime", mat.getFuelTime());
                addMatProp(props, "blockHardnessFactor", mat.getBlockHardnessFactor());
                addMatProp(props, "blockResistanceFactor", mat.getBlockResistanceFactor());
                addMatProp(props, "miningLevel", mat.getMiningLevel());
                addMatProp(props, "oreHardnessFactor", mat.getOreHardnessFactor());
                addMatProp(props, "oreResistanceFactor", mat.getOreResistanceFactor());
                addMatProp(props, "fireproof", mat.isFireproof());
                addMatProp(props, "beacon", mat.isBeacon());
                addMatProp(props, "trimmable", mat.isTrimmable());
                addMatProp(props, "nameEN", mat.getNameEN());
                addMatProp(props, "nameFR", mat.getNameFR());
                addMatProp(props, "nameES", mat.getNameES());
                addMatProp(props, "nameIT", mat.getNameIT());
                addMatProp(props, "nameDE", mat.getNameDE());
                addMatProp(props, "namePT", mat.getNamePT());
                addMatProp(props, "nameRU", mat.getNameRU());
                addMatProp(props, "nameZH", mat.getNameZH());
                addMatProp(props, "nameJP", mat.getNameJP());
                if (mat.getRawColorHigh() != null) addMatProp(props, "rawColorHigh", String.format("0x%06X", mat.getRawColorHigh() & 0xFFFFFF));
                if (mat.getBaseColorHigh() != null) addMatProp(props, "baseColorHigh", String.format("0x%06X", mat.getBaseColorHigh() & 0xFFFFFF));
            }
        } else if (currentCategory == Category.ITEMS) {
              ItemType type = ItemType.forName(id);
              if (type != null) addOverrideProps(props, type.getDefaultOverride());
        } else if (currentCategory == Category.BLOCKS) {
              BlockType type = BlockType.forName(id);
              if (type != null) addOverrideProps(props, type.getDefaultOverride());
        } else if (currentCategory == Category.CHEMICALS) {
              ChemicalType type = ChemicalType.forName(id);
              if (type != null) addOverrideProps(props, type.getDefaultOverride());
        }
        return props;
    }

    private void addMatProp(Map<String, String> props, String key, Object val) {
        if (val != null) props.put(key, String.valueOf(val));
    }

    private void addOverrideProps(Map<String, String> props, Object override) {
        if (override instanceof ItemOverride io) {
            addMatProp(props, "maxStackSize", io.getMaxStackSize());
            addMatProp(props, "fuelFactor", io.getFuelFactor());
            addMatProp(props, "smeltingMultiplier", io.getSmeltingMultiplier());
            addMatProp(props, "xpMultiplier", io.getXpMultiplier());
            addMatProp(props, "rarity", io.getRarity());
            addMatProp(props, "canBeFireproof", io.getCanBeFireproof());
            addMatProp(props, "canBeBeaconPayment", io.getCanBeBeaconPayment());
            addMatProp(props, "canBePiglinLoved", io.getCanBePiglinLoved());
            addMatProp(props, "canBeTrimmable", io.getCanBeTrimmable());
            addMatProp(props, "useRawColor", io.getUseRawColor());
            addMatProp(props, "nameEN", io.getNameEN());
            addMatProp(props, "nameFR", io.getNameFR());
            addMatProp(props, "nameES", io.getNameES());
            addMatProp(props, "nameIT", io.getNameIT());
            addMatProp(props, "nameDE", io.getNameDE());
            addMatProp(props, "namePT", io.getNamePT());
            addMatProp(props, "nameRU", io.getNameRU());
            addMatProp(props, "nameZH", io.getNameZH());
            addMatProp(props, "nameJP", io.getNameJP());
        } else if (override instanceof BlockOverride bo) {
            addMatProp(props, "hardness", bo.getHardness());
            addMatProp(props, "resistance", bo.getResistance());
            addMatProp(props, "lightLevel", bo.getLightLevel());
            addMatProp(props, "lightMode", bo.getLightMode());
            addMatProp(props, "redstoneFactor", bo.getRedstoneFactor());
            addMatProp(props, "redstonePower", bo.getRedstonePower());
            addMatProp(props, "slipperiness", bo.getSlipperiness());
            addMatProp(props, "speedFactor", bo.getSpeedFactor());
            addMatProp(props, "jumpFactor", bo.getJumpFactor());
            addMatProp(props, "isFlammable", bo.getIsFlammable());
            addMatProp(props, "isReplaceable", bo.getIsReplaceable());
            addMatProp(props, "noCollision", bo.getNoCollision());
            addMatProp(props, "nameEN", bo.getNameEN());
            addMatProp(props, "nameFR", bo.getNameFR());
            addMatProp(props, "nameES", bo.getNameES());
            addMatProp(props, "nameIT", bo.getNameIT());
            addMatProp(props, "nameDE", bo.getNameDE());
            addMatProp(props, "namePT", bo.getNamePT());
            addMatProp(props, "nameRU", bo.getNameRU());
            addMatProp(props, "nameZH", bo.getNameZH());
            addMatProp(props, "nameJP", bo.getNameJP());
        } else if (override instanceof ChemicalOverride co) {
            addMatProp(props, "nameEN", co.getNameEN());
            addMatProp(props, "nameFR", co.getNameFR());
            addMatProp(props, "nameES", co.getNameES());
            addMatProp(props, "nameIT", co.getNameIT());
            addMatProp(props, "nameDE", co.getNameDE());
            addMatProp(props, "namePT", co.getNamePT());
            addMatProp(props, "nameRU", co.getNameRU());
            addMatProp(props, "nameZH", co.getNameZH());
            addMatProp(props, "nameJP", co.getNameJP());
            if (co.getChemicalCleanColor() != null) addMatProp(props, "chemicalCleanColor", String.format("0x%06X", co.getChemicalCleanColor() & 0xFFFFFF));
            if (co.getChemicalDirtyColor() != null) addMatProp(props, "chemicalDirtyColor", String.format("0x%06X", co.getChemicalDirtyColor() & 0xFFFFFF));
            if (co.getChemicalSlurryColor() != null) addMatProp(props, "chemicalSlurryColor", String.format("0x%06X", co.getChemicalSlurryColor() & 0xFFFFFF));
        }
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        updateEntryButtonPositions();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.minecraft.font, "Ores Core Properties Configuration", this.width / 2, 8, 0xFFFFFF);
        
        guiGraphics.fill(20, LIST_TOP, 20 + LIST_WIDTH, this.height - 40, selectionMode ? 0x33FFDD00 : 0x22000000);
        if (selectionMode) {
             guiGraphics.drawString(this.minecraft.font, "§6§lSelect Property:", 20, LIST_TOP - 12, 0xFFFFFF);
             guiGraphics.renderOutline(20, LIST_TOP, LIST_WIDTH, this.height - 40 - LIST_TOP, 0xFFFFDD00);
        }

        if (selectedId != null) {
            guiGraphics.drawString(this.minecraft.font, "Properties for " + selectedId, panelX, panelY - 15, 0xAAAAAA);
            
            guiGraphics.enableScissor(panelX, panelY, panelX + panelWidth, panelY + panelHeight);
            int py = panelY + 14 - propertiesScrollOffset;
            for (PropertyWidget w : propertyWidgets) {
                if (py + 16 > panelY && py < panelY + panelHeight) {
                    int color = w.isOverridden ? 0xFFFFAA00 : 0xFFFFFFFF; 
                    if (!w.key.startsWith("__")) {
                        guiGraphics.drawString(this.minecraft.font, (w.isOverridden ? "§o" : "") + w.key, panelX, py, color);
                    }
                    w.widget.setY(py - 2);
                    w.widget.visible = true;
                } else {
                    w.widget.visible = false;
                }
                py += 20;
            }
            guiGraphics.disableScissor();
        } else {
            guiGraphics.drawCenteredString(this.minecraft.font, "Select an entry to edit its properties", panelX + panelWidth / 2, panelY + panelHeight / 2, 0x666666);
        }

        if (needsRestart) {
            guiGraphics.drawCenteredString(this.minecraft.font, "⚠ A restart is required for changes to take effect.", this.width / 2, this.height - 30, 0xFFFF5555);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX > 20 && mouseX < 20 + LIST_WIDTH) {
            int maxScroll = Math.max(0, entryIds.size() * ROW_HEIGHT - (this.height - 40 - LIST_TOP));
            scrollOffset = Math.max(0, Math.min(scrollOffset - (int) (scrollY * 20), maxScroll));
            return true;
        }
        if (mouseX > panelX && mouseX < panelX + panelWidth) {
            int maxPropsScroll = Math.max(0, propertyWidgets.size() * 20 - panelHeight + 40);
            propertiesScrollOffset = Math.max(0, Math.min(propertiesScrollOffset - (int) (scrollY * 20), maxPropsScroll));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void onClose() {
        ConfigManager.saveProperties(); 
        if (this.minecraft != null) this.minecraft.setScreen(this.lastScreen);
    }

    private static class PropertyWidget {
        final String key;
        final net.minecraft.client.gui.components.AbstractWidget widget;
        boolean isOverridden;

        PropertyWidget(String key, net.minecraft.client.gui.components.AbstractWidget widget, boolean isOverridden) {
            this.key = key;
            this.widget = widget;
            this.isOverridden = isOverridden;
        }
    }
}
