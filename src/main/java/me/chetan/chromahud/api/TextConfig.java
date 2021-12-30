/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiTextField
 */
package me.chetan.chromahud.api;

import net.minecraft.client.gui.GuiTextField;

import java.util.function.BiConsumer;

public class TextConfig {
    private BiConsumer<GuiTextField, DisplayItem> action;
    private GuiTextField button;
    private BiConsumer<GuiTextField, DisplayItem> load;

    public TextConfig(BiConsumer<GuiTextField, DisplayItem> action, GuiTextField button, BiConsumer<GuiTextField, DisplayItem> load) {
        this.action = action;
        this.button = button;
        this.load = load;
    }

    public BiConsumer<GuiTextField, DisplayItem> getAction() {
        return this.action;
    }

    public GuiTextField getTextField() {
        return this.button;
    }

    public BiConsumer<GuiTextField, DisplayItem> getLoad() {
        return this.load;
    }
}

