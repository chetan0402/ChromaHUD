/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package me.chetan.chromahud.displayitems;

import me.chetan.chromahud.ElementRenderer;
import me.chetan.chromahud.JsonHolder;
import me.chetan.chromahud.api.Dimension;
import me.chetan.chromahud.api.DisplayItem;
import net.minecraft.client.Minecraft;

public class FPS
extends DisplayItem {
    public FPS(JsonHolder raw, int ordinal) {
        super(raw, ordinal);
    }

    @Override
    public Dimension draw(int starX, double startY, boolean ignored) {
        String string = "FPS: " + Minecraft.getDebugFPS();
        ElementRenderer.draw(starX, startY, string);
        return new Dimension(Minecraft.getMinecraft().fontRendererObj.getStringWidth(string), 10.0);
    }
}

