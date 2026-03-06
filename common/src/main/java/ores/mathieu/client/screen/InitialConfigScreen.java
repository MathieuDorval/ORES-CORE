//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Main entry point for the ORES CORE configuration UI.
//  Acts as a hub to navigate to Ore Generation, Host Blocks,
//  Properties, and Custom Items configurations.
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
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@SuppressWarnings("null")
public class InitialConfigScreen extends Screen {
    private final Screen parent;

    public InitialConfigScreen(Screen parent) {
        super(Component.literal("Ores Core Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int x = (this.width - buttonWidth) / 2;
        int y = this.height / 2 - 25;

        this.addRenderableWidget(Button.builder(Component.literal("Ore Generation Config"), b -> {
            if (this.minecraft != null) this.minecraft.setScreen(new OreGenConfigScreen(this));
        }).bounds(x, y - 25, buttonWidth, 20).build());

        boolean isOresModsLoaded = ores.mathieu.platform.Services.getPlatform().isModLoaded("oresmods");
        Button hostBtn = Button.builder(Component.literal("Manage Host Blocks"), b -> {
            if (this.minecraft != null) this.minecraft.setScreen(new HostBlockManageScreen(this));
        }).bounds(x, y, buttonWidth, 20).build();

        if (!isOresModsLoaded) {
            hostBtn.active = false;
            hostBtn.setTooltip(Tooltip.create(Component.literal("Install Ores Mods to access")));
        }
        this.addRenderableWidget(hostBtn);

        Button propBtn = Button.builder(Component.literal("Manage Properties"), b -> {
            if (this.minecraft != null) this.minecraft.setScreen(new PropertiesConfigScreen(this));
        }).bounds(x, y + 25, buttonWidth, 20).build();

        if (!isOresModsLoaded) {
            propBtn.active = false;
            propBtn.setTooltip(Tooltip.create(Component.literal("Install Ores Mods to access")));
        }
        this.addRenderableWidget(propBtn);

        Button customBtn = Button.builder(Component.literal("Manage Custom Items"), b -> {
            if (this.minecraft != null) this.minecraft.setScreen(new CustomRegistryConfigScreen(this));
        }).bounds(x, y + 50, buttonWidth, 20).build();

        if (!isOresModsLoaded) {
            customBtn.active = false;
            customBtn.setTooltip(Tooltip.create(Component.literal("Install Ores Mods to access")));
        }
        this.addRenderableWidget(customBtn);

        this.addRenderableWidget(Button.builder(Component.literal("Done"), b -> this.onClose())
                .bounds(x, y + 85, buttonWidth, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.parent);
    }
}
