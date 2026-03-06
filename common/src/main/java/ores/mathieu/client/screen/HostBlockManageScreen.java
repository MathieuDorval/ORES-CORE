//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Configuration UI screen for managing host blocks.
// Allows users to add or remove blocks that ores can generate inside.
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import ores.mathieu.OresCoreCommon;
import ores.mathieu.api.OresCoreAPI;
import ores.mathieu.platform.Services;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("null")
public class HostBlockManageScreen extends Screen {

    private final Screen parent;
    private final Path configRegistryPath;
    private JsonObject configRegistryJson;

    private List<String> activeHostBlocks = new ArrayList<>();
    private List<String> configHostBlocks = new ArrayList<>();
    private Map<String, BlockEntry> availableBlocksMap = new HashMap<>(); 
    private List<BlockEntry> allAvailableBlocks = new ArrayList<>();
    private List<String> currentFilteredBlocks = new ArrayList<>();

    private EditBox searchBox;
    private Button doneButton;
    private final List<Button> buttonPool = new ArrayList<>();
    private final String[] poolTargetIds = new String[1500];
    private final boolean[] poolIsAdd = new boolean[1500];
    private int buttonsUsed = 0;

    private int mainScroll = 0;
    private int maxScroll = 0;
    private final int SLOT_SIZE = 24;
    
    private boolean needsRestart = false;
    private String hoveredTooltip = null;

    public HostBlockManageScreen(Screen parent) {
        super(Component.literal("Manage Host Blocks"));
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

        if (!configRegistryJson.has("stones_replacement")) {
            configRegistryJson.add("stones_replacement", new JsonArray());
        }

        configHostBlocks.clear();
        JsonArray array = configRegistryJson.getAsJsonArray("stones_replacement");
        for (int i = 0; i < array.size(); i++) {
            String blockId = array.get(i).getAsString();
            if (blockId != null && blockId.contains(":")) {
                if (OresCoreAPI.isValidHostBlockCandidate(blockId)) {
                    configHostBlocks.add(blockId);
                }
            }
        }
    }

    private void saveConfigRegistry() {
        JsonArray array = new JsonArray();
        for (String block : configHostBlocks) {
            array.add(block);
        }
        configRegistryJson.add("stones_replacement", array);

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
        Set<String> referencedInGen = new HashSet<>();
        for (ores.mathieu.worldgen.VeinConfig vein : ores.mathieu.config.ConfigManager.LOADED_VEINS) {
            String b = vein.associatedBlock;
            if (b != null && b.contains(":")) {
                referencedInGen.add(b);
            }
        }

        Set<String> modDefaults = OresCoreAPI.getModProvidedHostBlocks();
        activeHostBlocks.clear();
        for (String id : modDefaults) {
            if (id.contains(":") && OresCoreAPI.isValidHostBlockCandidate(id)) {
                if (!configHostBlocks.contains(id) || referencedInGen.contains(id)) {
                    activeHostBlocks.add(id);
                }
            }
        }
        activeHostBlocks.sort(String::compareTo);

        allAvailableBlocks.clear();
        availableBlocksMap.clear();
        for (Map.Entry<net.minecraft.resources.ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
            Block block = entry.getValue();
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            String idStr = id.toString();
            
            if (OresCoreAPI.isValidHostBlockCandidate(block)) {
                BlockEntry blockEntry = new BlockEntry(idStr, null);
                
                availableBlocksMap.put(idStr, blockEntry);
                
                if (!activeHostBlocks.contains(idStr) && !configHostBlocks.contains(idStr)) {
                    allAvailableBlocks.add(blockEntry);
                }
            }
        }
        allAvailableBlocks.sort((a, b) -> a.id.compareTo(b.id));
    }

    @Override
    protected void init() {
        searchBox = new EditBox(this.font, 10, 5, 200, 18, Component.literal("Search"));
        searchBox.setHint(Component.literal("Search for blocks..."));
        searchBox.setResponder(s -> {
            mainScroll = 0; 
            updateAvailableList(s.toLowerCase());
        });
        this.addRenderableWidget(searchBox);

        doneButton = Button.builder(Component.literal("Done"), btn -> {
            saveConfigRegistry();
            this.minecraft.setScreen(parent);
        }).bounds(this.width - 60, 4, 50, 20).build();
        this.addRenderableWidget(doneButton);
        
        buttonPool.clear();
        for (int i = 0; i < 1500; i++) {
            final int index = i;
            Button b = Button.builder(Component.empty(), btn -> {
                String targetId = poolTargetIds[index];
                if (targetId == null) return;
                if (poolIsAdd[index]) {
                    if (!configHostBlocks.contains(targetId)) {
                        configHostBlocks.add(targetId);
                        needsRestart = true;
                    }
                } else {
                    configHostBlocks.remove(targetId);
                    needsRestart = true;
                }
                updateAvailableList(searchBox.getValue().toLowerCase());
            }).bounds(-100, -100, 12, 12).build();
            buttonPool.add(b);
            this.addRenderableWidget(b);
        }
        
        updateAvailableList(searchBox.getValue().toLowerCase());
    }

    private void updateAvailableList(String filter) {
        currentFilteredBlocks.clear();
        for (BlockEntry b : allAvailableBlocks) {
            if (activeHostBlocks.contains(b.id) || configHostBlocks.contains(b.id)) continue;
            String idLower = b.id.toLowerCase();
            if (idLower.contains(filter)) {
                currentFilteredBlocks.add(b.id);
            }
        }
    }

    private int getCols() {
        return Math.max(1, (this.width - 40) / SLOT_SIZE);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        hoveredTooltip = null;
        buttonsUsed = 0;
        
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 9, 0xFFFFFF);

        int vpTop = 30;
        int vpBottom = this.height - 40;
        graphics.enableScissor(0, vpTop, this.width, vpBottom);
        
        int yOffset = vpTop + 5 - mainScroll;
        
        List<GridItem> activeItems = new ArrayList<>();
        for (String id : activeHostBlocks) activeItems.add(new GridItem(id, false, false));
        for (String id : configHostBlocks) activeItems.add(new GridItem(id, true, false));
        yOffset = renderGrid(graphics, mouseX, mouseY, "Active Host Blocks", activeItems, yOffset);
        
        List<GridItem> inactiveItems = new ArrayList<>();
        for (String id : currentFilteredBlocks) inactiveItems.add(new GridItem(id, false, true));
        yOffset = renderGrid(graphics, mouseX, mouseY, "Inactive Host Blocks (Available)", inactiveItems, yOffset);
        
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

        if (this.minecraft.level == null) {
            graphics.drawCenteredString(this.font, "⚠ Please load a world once to properly display block icons.", this.width / 2, this.height - 20, 0xFFFFAA00);
        }

        if (hoveredTooltip != null) {
            int tw = this.font.width(hoveredTooltip);
            int tx = Math.min(mouseX + 10, this.width - tw - 10);
            int ty = Math.min(mouseY + 10, this.height - 20);
            graphics.fill(tx - 2, ty - 2, tx + tw + 2, ty + 10, 0xCC000000);
            graphics.drawString(this.font, hoveredTooltip, tx, ty, 0xFFFFFFFF);
        }
    }

    private int renderGrid(GuiGraphics graphics, int mouseX, int mouseY, String title, List<GridItem> items, int yOffset) {
        if (title != null && yOffset > -20 && yOffset < this.height) {
            graphics.drawString(this.font, title, 20, yOffset, 0xFFAAAA);
        }
        int gridStartY = (title != null) ? (yOffset + 15) : yOffset;
        int cols = getCols();
        
        int vpTop = 30;
        int vpBottom = this.height - 40;
        int firstRow = Math.max(0, (vpTop - gridStartY) / SLOT_SIZE);
        int lastRow = Math.min((int) Math.ceil((double) items.size() / cols), (vpBottom - gridStartY) / SLOT_SIZE + 1);
        
        for (int i = firstRow * cols; i < Math.min(items.size(), (lastRow + 1) * cols); i++) {
            int row = i / cols;
            int col = i % cols;
            int bx = 20 + col * SLOT_SIZE;
            int by = gridStartY + row * SLOT_SIZE;

            if (by > -30 && by < this.height) {
                GridItem item = items.get(i);
                String id = item.id;
                graphics.fill(bx, by, bx + SLOT_SIZE - 2, by + SLOT_SIZE - 2, 0x55000000);
                
                BlockEntry entry = availableBlocksMap.get(id);
                if (entry != null) {
                    if (entry.stack == null) {
                        try {
                            BuiltInRegistries.BLOCK.get(Identifier.parse(entry.id)).ifPresent(holder -> {
                                Block b = holder.value();
                                try {
                                    entry.stack = b.asItem().getDefaultInstance();
                                    if (entry.stack.isEmpty()) entry.stack = new ItemStack(b);
                                } catch (Exception e) {
                                    entry.stack = ItemStack.EMPTY;
                                }
                            });
                            if (entry.stack == null) entry.stack = ItemStack.EMPTY;
                        } catch (Exception e) {
                            entry.stack = ItemStack.EMPTY;
                        }
                    }
                    if (entry.stack != null && !entry.stack.isEmpty()) {
                        graphics.renderFakeItem(entry.stack, bx + 3, by + 3);
                        graphics.renderItemDecorations(this.font, entry.stack, bx + 3, by + 3);
                    }
                }
                
                if (item.deletable || item.addable) {
                    if (buttonsUsed < buttonPool.size()) {
                        Button b = buttonPool.get(buttonsUsed);
                        b.setX(bx + SLOT_SIZE - 12);
                        b.setY(by);
                        
                        b.visible = by >= vpTop && (by + 12) <= vpBottom;
                        b.active = b.visible;
                        
                        poolTargetIds[buttonsUsed] = id;
                        poolIsAdd[buttonsUsed] = item.addable;
                        b.setMessage(Component.literal(item.addable ? "+" : "-"));
                        
                        buttonsUsed++;
                    }
                }

                if (mouseX >= bx && mouseX < bx + SLOT_SIZE - 2 && mouseY >= by && mouseY < by + SLOT_SIZE - 2) {
                    graphics.fill(bx, by, bx + SLOT_SIZE - 2, by + SLOT_SIZE - 2, 0x44FFFFFF);
                    hoveredTooltip = id;
                }
            }
        }
        int rows = Math.max(1, (int) Math.ceil((double) items.size() / cols));
        return gridStartY + rows * SLOT_SIZE + 10;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        mainScroll -= (int) (scrollY * 16);
        mainScroll = Math.max(0, Math.min(mainScroll, maxScroll));
        return true;
    }

    private static class BlockEntry {
        String id;
        ItemStack stack;
        BlockEntry(String id, ItemStack stack) {
            this.id = id;
            this.stack = stack;
        }
    }

    private static class GridItem {
        final String id;
        final boolean deletable;
        final boolean addable;
        GridItem(String id, boolean deletable, boolean addable) {
            this.id = id;
            this.deletable = deletable;
            this.addable = addable;
        }
    }

    @Override
    public void onClose() {
        saveConfigRegistry();
        this.minecraft.setScreen(parent);
    }
}
