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

import java.util.ArrayList;

public class DirectionHUD
extends DisplayItem {
    private static final String[] dir = new String[]{"South", "South West", "West", "North West", "North", "North East", "East", "South East"};

    public DirectionHUD(JsonHolder raw, int ordinal) {
        super(raw, ordinal);
    }

    @Override
    public Dimension draw(int x, double y, boolean isConfig) {
        ArrayList<String> list = new ArrayList<>();
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player != null) {
            int d = (int)player.rotationYaw;
            if (d < 0) {
                d += 360;
            }
            int direction = (d + 22) % 360;
            direction /= 45;
            try {
                while (direction < 0) {
                    direction += 8;
                }
                list.add(dir[direction]);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        ElementRenderer.draw(x, y, list);
        return new Dimension(isConfig ? (double)ElementRenderer.maxWidth(list) : 0.0, 10.0);
    }
}

