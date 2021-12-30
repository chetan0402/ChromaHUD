/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 */
package me.chetan.chromahud.displayitems;

import me.chetan.chromahud.ElementRenderer;
import me.chetan.chromahud.JsonHolder;
import me.chetan.chromahud.api.Dimension;
import me.chetan.chromahud.api.DisplayItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class PingDisplay
extends DisplayItem {
    public PingDisplay(JsonHolder raw, int ordinal) {
        super(raw, ordinal);
    }

    @Override
    public Dimension draw(int starX, double startY, boolean isConfig) {
        EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
        if (thePlayer != null) {
            String string = "Ping: " + Minecraft.getMinecraft().getNetHandler().getPlayerInfo(Minecraft.getMinecraft().thePlayer.getUniqueID()).getResponseTime();
            ElementRenderer.draw(starX, startY, string);
            return new Dimension(Minecraft.getMinecraft().fontRendererObj.getStringWidth(string), 10.0);
        }
        return new Dimension(0.0, 0.0);
    }
}

