//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Configuration UI screen for managing custom items via 
// the materials registry. Enables manual injection of specific item
// IDs into the generation pipeline.
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ores.mathieu.OresCoreCommon;
import ores.mathieu.platform.Services;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CustomRegistryConfigScreen extends Screen {

    private final Screen parent;
    private final Path configRegistryPath;
    private JsonObject configRegistryJson;

    private List<String> configCustomMaterials = new ArrayList<>();

    private EditBox idInputBox;
    private Button addIdButton;
    private Button doneButton;
    private final List<Button> buttonPool = new ArrayList<>();
    private final String[] poolTargetIds = new String[1000];
    private int buttonsUsed = 0;

    private int mainScroll = 0;
    private int maxScroll = 0;
    
    private boolean needsRestart = false;
    private String statusMessage = null;
    private int statusColor = 0xFFFFFFFF;

    public CustomRegistryConfigScreen(Screen parent) {
        super(Component.literal("Manage Custom Items"));
        this.parent = parent;
        this.configRegistryPath = Services.getPlatform().getConfigDir().resolve("ores").resolve("registry.json");
        loadConfigRegistry();
        loadData();
    }

    private void loadConfigRegistry() {
        if (Files.exists(configRegistryPath)) {
            try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(configRegistryPath))) {
                configRegistryJson = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (Exception e) {
                OresCoreCommon.LOGGER.error("Failed to load registry.json", e);
                configRegistryJson = new JsonObject();
            }
        } else {
            configRegistryJson = new JsonObject();
        }

        if (!configRegistryJson.has("materials_registry")) {
            configRegistryJson.add("materials_registry", new JsonArray());
        }
    }

    private void saveConfigRegistry() {
        JsonArray array = new JsonArray();
        for (String id : configCustomMaterials) {
            array.add(id);
        }
        configRegistryJson.add("materials_registry", array);

        try {
            if (!Files.exists(configRegistryPath.getParent())) {
                Files.createDirectories(configRegistryPath.getParent());
            }
            try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(configRegistryPath))) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(configRegistryJson, writer);
            }
        } catch (Exception e) {
            OresCoreCommon.LOGGER.error("Failed to save registry.json", e);
        }
    }

    private void loadData() {
        if (configRegistryJson.has("materials_registry")) {
            JsonArray arr = configRegistryJson.getAsJsonArray("materials_registry");
            configCustomMaterials.clear();
            for (int i = 0; i < arr.size(); i++) {
                configCustomMaterials.add(arr.get(i).getAsString());
            }
        }
        configCustomMaterials.sort(String::compareTo);
    }

    private void validateInput(String val) {
        statusMessage = null;
        
        if (val.isEmpty()) {
            idInputBox.setTextColor(0xFFFFFFFF);
            addIdButton.visible = false;
            return;
        }

        boolean canCreate = ores.mathieu.api.OresCoreAPI.canCreateId(val);
        boolean alreadyAdded = ores.mathieu.api.OresCoreAPI.isIdAdded(val) || configCustomMaterials.contains(val);

        if (!canCreate) {
            idInputBox.setTextColor(0xFFFF5555);
            addIdButton.visible = false;
        } else if (alreadyAdded) {
            idInputBox.setTextColor(0xFFFFAA00); 
            statusMessage = "Already registered";
            statusColor = 0xFFFFAA00;
            addIdButton.visible = false;
        } else {
            idInputBox.setTextColor(0xFFFFFFFF);
            addIdButton.visible = true;
        }
    }

    @Override
    protected void init() {
        idInputBox = new EditBox(this.font, 10, 5, 200, 18, Component.literal("ID Input"));
        idInputBox.setHint(Component.literal("Enter ID (e.g. raw_uranium)..."));
        idInputBox.setResponder(this::validateInput);
        this.addRenderableWidget(idInputBox);

        addIdButton = Button.builder(Component.literal("+"), btn -> {
            String val = idInputBox.getValue();
            if (!val.isEmpty() && !configCustomMaterials.contains(val)) {
                configCustomMaterials.add(val);
                needsRestart = true;
                idInputBox.setValue("");
                saveConfigRegistry();
                loadData();
            }
        }).bounds(215, 5, 20, 18).build();
        addIdButton.visible = false;
        this.addRenderableWidget(addIdButton);

        doneButton = Button.builder(Component.literal("Done"), btn -> {
            saveConfigRegistry();
            this.minecraft.setScreen(parent);
        }).bounds(this.width - 60, 4, 50, 20).build();
        this.addRenderableWidget(doneButton);
        
        buttonPool.clear();
        for (int i = 0; i < 1000; i++) {
            final int index = i;
            Button b = Button.builder(Component.literal("X"), btn -> {
                String targetId = poolTargetIds[index];
                if (targetId == null) return;
                configCustomMaterials.remove(targetId);
                needsRestart = true;
                saveConfigRegistry();
                loadData();
            }).bounds(-100, -100, 12, 12).build();
            buttonPool.add(b);
            this.addRenderableWidget(b);
        }
    }


    @Override
    @SuppressWarnings("null")
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        buttonsUsed = 0;
        
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 9, 0xFFFFFF);

        if (statusMessage != null) {
            graphics.drawString(this.font, statusMessage, 240, 9, statusColor);
        }

        int vpTop = 30;
        int vpBottom = this.height - 40;
        graphics.enableScissor(0, vpTop, this.width, vpBottom);
        
        int yOffset = vpTop + 5 - mainScroll;
        
        List<GridItem> activeItems = new ArrayList<>();
        for (String id : configCustomMaterials) activeItems.add(new GridItem(id));
        yOffset = renderGrid(graphics, mouseX, mouseY, "Manually Added Items (Custom)", activeItems, yOffset);
        
        graphics.disableScissor();

        for (int i = buttonsUsed; i < buttonPool.size(); i++) {
            buttonPool.get(i).visible = false;
        }

        int totalHeight = (yOffset + mainScroll) - (vpTop + 5);
        maxScroll = Math.max(0, totalHeight - (vpBottom - vpTop) + 10);
        mainScroll = Math.min(mainScroll, maxScroll);
        
        if (needsRestart) {
            graphics.drawCenteredString(this.font, "⚠ A restart is required for changes to take effect.", this.width / 2, this.height - 30, 0xFFFF5555);
        }
    }

    private int renderGrid(GuiGraphics graphics, int mouseX, int mouseY, String title, List<GridItem> items, int yOffset) {
        if (title != null && yOffset > -20 && yOffset < this.height) {
            graphics.drawString(this.font, title, 20, yOffset, 0xFFFFAAAA);
        }
        int gridStartY = (title != null) ? (yOffset + 15) : yOffset;
        
        int itemWidth = 180;
        int itemHeight = 20;
        int cols = Math.max(1, (this.width - 40) / itemWidth);
        
        int vpTop = 30;
        int vpBottom = this.height - 40;
        
        for (int i = 0; i < items.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            int bx = 20 + col * itemWidth;
            int by = gridStartY + row * itemHeight;

            if (by > -30 && by < this.height) {
                GridItem item = items.get(i);
                String id = item.id;
                
                graphics.fill(bx, by, bx + itemWidth - 5, by + itemHeight - 2, 0x55000000);
                
                String display = id;
                if (display.length() > 24) display = display.substring(0, 22) + "..";
                graphics.drawString(this.font, display, bx + 5, by + 5, 0xFFFFFFFF);
                
                if (buttonsUsed < buttonPool.size()) {
                    Button b = buttonPool.get(buttonsUsed);
                    b.setX(bx + itemWidth - 18);
                    b.setY(by + 2);
                    b.setWidth(12);
                    b.setHeight(12);
                    
                    b.visible = by >= vpTop && (by + 12) <= vpBottom;
                    b.active = b.visible;
                    
                    poolTargetIds[buttonsUsed] = id;
                    b.setMessage(Component.literal("X"));
                    
                    buttonsUsed++;
                }

                if (mouseX >= bx && mouseX < bx + itemWidth - 5 && mouseY >= by && mouseY < by + itemHeight - 2) {
                    graphics.fill(bx, by, bx + itemWidth - 5, by + itemHeight - 2, 0x44FFFFFF);
                }
            }
        }
        int rows = Math.max(1, (int) Math.ceil((double) items.size() / cols));
        return gridStartY + rows * itemHeight + 10;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        mainScroll -= (int) (scrollY * 16);
        mainScroll = Math.max(0, Math.min(mainScroll, maxScroll));
        return true;
    }

    private static class GridItem {
        final String id;
        GridItem(String id) {
            this.id = id;
        }
    }

    @Override
    public void onClose() {
        saveConfigRegistry();
        this.minecraft.setScreen(parent);
    }
}
