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

public class CpsDisplay
extends DisplayItem {
    public CpsDisplay(JsonHolder data, int ordinal) {
        super(data, ordinal);
    }

    @Override
    public Dimension draw(int starX, double startY, boolean isConfig) {
        ElementRenderer.draw(starX, startY, "CPS: " + ElementRenderer.getCPS());
        if (isConfig) {
            return new Dimension(Minecraft.getMinecraft().fontRendererObj.getStringWidth("CPS: " + ElementRenderer.getCPS()), 10.0);
        }
        return new Dimension(0.0, 10.0);
    }
}

