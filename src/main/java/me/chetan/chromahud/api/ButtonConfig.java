/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiButton
 */
package me.chetan.chromahud.api;

import me.chetan.chromahud.api.DisplayItem;
import java.util.function.BiConsumer;
import net.minecraft.client.gui.GuiButton;

public class ButtonConfig {
    private BiConsumer<GuiButton, DisplayItem> action;
    private GuiButton button;
    private BiConsumer<GuiButton, DisplayItem> load;

    public ButtonConfig(BiConsumer<GuiButton, DisplayItem> action, GuiButton button, BiConsumer<GuiButton, DisplayItem> load) {
        this.action = action;
        this.button = button;
        this.load = load;
    }

    public BiConsumer<GuiButton, DisplayItem> getAction() {
        return this.action;
    }

    public GuiButton getButton() {
        return this.button;
    }

    public BiConsumer<GuiButton, DisplayItem> getLoad() {
        return this.load;
    }
}

