//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: UI Screen displaying the list of all configured ore veins.
// Allows users to add, edit, or remove vein generation configurations interactively.
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
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ores.mathieu.config.ConfigManager;
import ores.mathieu.worldgen.VeinConfig;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("null")
public class OreGenConfigScreen extends Screen {

    private final Screen parent;
    private int scrollOffset = 0;
    private static final int ROW_HEIGHT = 28;
    private static final int LIST_TOP = 64; 
    private static final int LIST_MARGIN = 20;

    private EditBox filterMaterial;
    private EditBox filterDimension;
    private AbstractSliderButton filterYMin;
    private AbstractSliderButton filterYMax;
    private int yMinFilter = -64;
    private int yMaxFilter = 320;
    private Button filterTypeBtn;
    private String currentTypeFilter = "ALL"; 

    private final List<Integer> filteredIndices = new ArrayList<>();

    public OreGenConfigScreen(Screen parent) {
        super(Component.literal("Ores Core - Ore Generation"));
        this.parent = parent;
    }

    private final List<Button> editButtons = new ArrayList<>();
    private final List<Button> deleteButtons = new ArrayList<>();

    @Override
    protected void init() {
        int filterW = (this.width - LIST_MARGIN * 2) / 4;
        filterMaterial = new EditBox(this.font, LIST_MARGIN, 24, filterW - 5, 18, Component.literal("Material"));
        filterMaterial.setHint(Component.literal("Filter Material..."));
        filterMaterial.setResponder(s -> this.applyFilters());
        this.addRenderableWidget(filterMaterial);

        filterDimension = new EditBox(this.font, LIST_MARGIN + filterW, 24, filterW - 5, 18, Component.literal("Dimension"));
        filterDimension.setHint(Component.literal("Filter Dimension..."));
        filterDimension.setResponder(s -> this.applyFilters());
        this.addRenderableWidget(filterDimension);

        filterYMin = new AbstractSliderButton(LIST_MARGIN + filterW * 2, 12, filterW - 5, 20, Component.literal("Min Y: -64"), 0.0) {
            @Override
            protected void updateMessage() {
                yMinFilter = -64 + (int) (this.value * (320 - (-64)));
                this.setMessage(Component.literal("Min Y: " + yMinFilter));
            }
            @Override
            protected void applyValue() {
                yMinFilter = -64 + (int) (this.value * (320 - (-64)));
                applyFilters();
            }
        };
        this.addRenderableWidget(filterYMin);

        filterYMax = new AbstractSliderButton(LIST_MARGIN + filterW * 2, 34, filterW - 5, 20, Component.literal("Max Y: 320"), 1.0) {
            @Override
            protected void updateMessage() {
                yMaxFilter = -64 + (int) (this.value * (320 - (-64)));
                this.setMessage(Component.literal("Max Y: " + yMaxFilter));
            }
            @Override
            protected void applyValue() {
                yMaxFilter = -64 + (int) (this.value * (320 - (-64)));
                applyFilters();
            }
        };
        this.addRenderableWidget(filterYMax);

        filterTypeBtn = Button.builder(Component.literal("Type: ALL"), btn -> {
            if ("ALL".equals(currentTypeFilter)) currentTypeFilter = "CLASSIC";
            else if ("CLASSIC".equals(currentTypeFilter)) currentTypeFilter = "GIANT";
            else currentTypeFilter = "ALL";
            btn.setMessage(Component.literal("Type: " + currentTypeFilter));
            this.applyFilters();
        }).bounds(LIST_MARGIN + filterW * 3, 23, filterW - 5, 20).build();
        this.addRenderableWidget(filterTypeBtn);

        this.addRenderableWidget(Button.builder(Component.literal("+ Add Vein"), btn -> {
            VeinConfig newVein = new VeinConfig();
            newVein.materials = new ArrayList<>();
            newVein.veinType = "CLASSIC";
            newVein.shape = "BLOB";
            newVein.distribution = "TRAPEZOID";
            newVein.rarity = 10;
            newVein.density = 8;
            newVein.minY = -64;
            newVein.maxY = 64;
            newVein.specifics = "NONE";
            newVein.airExposureChance = 0.5f;
            newVein.dimensionsWhitelist = new ArrayList<>();
            newVein.dimensionsBlacklist = new ArrayList<>();
            newVein.biomesWhitelist = new ArrayList<>();
            newVein.biomesBlacklist = new ArrayList<>();
            ConfigManager.LOADED_VEINS.add(newVein);
            
            this.applyFilters();
            this.minecraft.setScreen(new VeinEditScreen(this, ConfigManager.LOADED_VEINS.size() - 1, true));
        }).bounds(LIST_MARGIN, this.height - 28, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Done"), btn -> {
            ConfigManager.saveVeins();
            this.minecraft.setScreen(this.parent);
        }).bounds(this.width - LIST_MARGIN - 100, this.height - 28, 100, 20).build());

        this.applyFilters();
    }

    public void applyFilters() {
        filteredIndices.clear();
        String fMat = filterMaterial.getValue().toLowerCase().trim();
        String fDim = filterDimension.getValue().toLowerCase().trim();

        for (int i = 0; i < ConfigManager.LOADED_VEINS.size(); i++) {
            VeinConfig vein = ConfigManager.LOADED_VEINS.get(i);
            
            if (!fMat.isEmpty()) {
                boolean matchMat = false;
                if (vein.materials != null) {
                    for (String mat : vein.materials) {
                        if (mat.toLowerCase().contains(fMat)) matchMat = true;
                    }
                }
                if (!matchMat) continue;
            }

            if (!fDim.isEmpty()) {
                boolean matchDim = false;
                if (vein.dimensionsWhitelist != null && !vein.dimensionsWhitelist.isEmpty()) {
                    for (String d : vein.dimensionsWhitelist) {
                        if (d.toLowerCase().contains(fDim)) matchDim = true;
                    }
                } else {
                    matchDim = true;
                }
                if (!matchDim) continue;
            }

            if (vein.maxY < yMinFilter || vein.minY > yMaxFilter) continue;

            if (!"ALL".equals(currentTypeFilter)) {
                String type = vein.veinType != null ? vein.veinType.toUpperCase() : "CLASSIC";
                if (!currentTypeFilter.equals(type)) continue;
            }

            filteredIndices.add(i);
        }

        rebuildVeinButtons();
    }

    private void rebuildVeinButtons() {
        for (Button b : editButtons) this.removeWidget(b);
        for (Button b : deleteButtons) this.removeWidget(b);
        editButtons.clear();
        deleteButtons.clear();

        int rowRight = this.width - LIST_MARGIN;

        for (int i = 0; i < filteredIndices.size(); i++) {
            final int displayIndex = i;
            final int trueIndex = filteredIndices.get(displayIndex);

            Button editBtn = Button.builder(Component.literal("Edit"), btn -> {
                this.minecraft.setScreen(new VeinEditScreen(this, trueIndex, false));
            }).bounds(rowRight - 68, -100, 40, 20).build();
            editButtons.add(editBtn);
            this.addRenderableWidget(editBtn);

            Button delBtn = Button.builder(Component.literal("X"), btn -> {
                ConfigManager.LOADED_VEINS.remove(trueIndex);
                this.applyFilters();
            }).bounds(rowRight - 24, -100, 20, 20).build();
            deleteButtons.add(delBtn);
            this.addRenderableWidget(delBtn);
        }
    }

    private void updateButtonPositions() {
        int listBottom = this.height - 36;
        int rowRight = this.width - LIST_MARGIN;

        for (int i = 0; i < filteredIndices.size(); i++) {
            int rowY = LIST_TOP + i * ROW_HEIGHT - scrollOffset;

            if (rowY + 20 < LIST_TOP || rowY > listBottom - 20) {
                if (i < editButtons.size()) {
                    editButtons.get(i).setY(-100);
                    deleteButtons.get(i).setY(-100);
                }
            } else {
                if (i < editButtons.size()) {
                    editButtons.get(i).setY(rowY + 3);
                    editButtons.get(i).setX(rowRight - 68);
                    deleteButtons.get(i).setY(rowY + 3);
                    deleteButtons.get(i).setX(rowRight - 24);
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        updateButtonPositions();

        super.render(graphics, mouseX, mouseY, partialTick);

        int listBottom = this.height - 36;
        int rowRight = this.width - LIST_MARGIN;

        graphics.drawString(this.font, this.title, LIST_MARGIN, 8, 0xFFFFFFFF);
        
        String countStr = ConfigManager.LOADED_VEINS.size() + " vein(s)";
        graphics.drawString(this.font, countStr, rowRight - this.font.width(countStr), 8, 0xFF888888);

        graphics.enableScissor(LIST_MARGIN, LIST_TOP, rowRight, listBottom);

        for (int i = 0; i < filteredIndices.size(); i++) {
            int rowY = LIST_TOP + i * ROW_HEIGHT - scrollOffset;

            if (rowY + ROW_HEIGHT < LIST_TOP || rowY > listBottom) continue;

            VeinConfig vein = ConfigManager.LOADED_VEINS.get(filteredIndices.get(i));

            boolean hovered = mouseX >= LIST_MARGIN && mouseX <= rowRight
                    && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;
            int bgColor = hovered ? 0x44FFFFFF : ((i % 2 == 0) ? 0x22FFFFFF : 0x11FFFFFF);
            graphics.fill(LIST_MARGIN, rowY, rowRight, rowY + ROW_HEIGHT - 1, bgColor);

            String mats = vein.materials != null ? String.join(", ", vein.materials) : "?";
            if (mats.length() > 30) mats = mats.substring(0, 27) + "...";
            graphics.drawString(this.font, mats, LIST_MARGIN + 4, rowY + 4, 0xFFFFFFFF);

            String type = vein.veinType != null ? vein.veinType : "CLASSIC";
            int typeColor = "GIANT".equalsIgnoreCase(type) ? 0xFFFF8844 : 0xFF55AAFF;
            int typeX = LIST_MARGIN + 4 + this.font.width(mats) + 10;
            graphics.drawString(this.font, "[" + type + "]", typeX, rowY + 4, typeColor);

            String yRange = "Y: " + vein.minY + " to " + vein.maxY;
            graphics.drawString(this.font, yRange, LIST_MARGIN + 4, rowY + 16, 0xFF55FFFF);

            String dims;
            if (vein.dimensionsWhitelist != null && !vein.dimensionsWhitelist.isEmpty()) {
                dims = String.join(", ", vein.dimensionsWhitelist);
                if (dims.length() > 25) dims = dims.substring(0, 22) + "...";
            } else {
                dims = "All Dimensions";
            }
            int dimsX = LIST_MARGIN + 4 + this.font.width(yRange) + 12;
            graphics.drawString(this.font, dims, dimsX, rowY + 16, 0xFF888888);
        }

        graphics.disableScissor();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int listBottom = this.height - 36;
        int contentHeight = filteredIndices.size() * ROW_HEIGHT;
        int visibleHeight = listBottom - LIST_TOP;
        int maxScroll = Math.max(0, contentHeight - visibleHeight);

        scrollOffset -= (int) (scrollY * 16);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
        
        updateButtonPositions();
        return true;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
