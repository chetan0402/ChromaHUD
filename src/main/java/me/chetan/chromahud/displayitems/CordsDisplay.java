/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CordsDisplay
extends DisplayItem {
    public int state = 0;
    public int precision = 1;

    public CordsDisplay(JsonHolder options, int orderinal) {
        super(options, orderinal);
        this.state = options.optInt("state");
        this.precision = options.optInt("precision");
    }

    @Override
    public void save() {
        this.data.put("state", this.state);
        this.data.put("precision", this.precision);
    }

    public String toString() {
        return "CordsDisplay{state=" + this.state + '}';
    }

    @Override
    public Dimension draw(int x, double y, boolean isConfig) {
        ArrayList<String> tmp = new ArrayList<>();
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player != null) {
            StringBuilder start = new StringBuilder("0");
            if (this.precision > 0) {
                start.append(".");
            }
            for (int i = 0; i < this.precision; ++i) {
                start.append("0");
            }
            DecimalFormat df = new DecimalFormat(start.toString());
            if (this.state == 0) {
                tmp.add("X: " + df.format(player.posX) + " Y: " + df.format(player.posY) + " Z: " + df.format(player.posZ));
            } else if (this.state == 1) {
                tmp.add("X: " + df.format(player.posX));
                tmp.add("Y: " + df.format(player.posY));
                tmp.add("Z: " + df.format(player.posZ));
            } else {
                tmp.add("Illegal state of cords unit (" + this.state + ")");
            }
        } else {
            tmp.add("X: null, Y: null, Z: null");
        }
        ElementRenderer.draw(x, y, tmp);
        return new Dimension(isConfig ? (double)ElementRenderer.maxWidth(tmp) : 0.0, tmp.size() * 10);
    }
}

