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

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeHud
extends DisplayItem {
    private String format;

    public TimeHud(JsonHolder data, int ordinal) {
        super(data, ordinal);
        this.format = data.optString("format");
        if (this.format.isEmpty()) {
            this.format = "HH:mm:ss";
        }
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.data.put("format", format);
        this.format = format;
    }

    @Override
    public Dimension draw(int starX, double startY, boolean isConfig) {
        try {
            String string = new SimpleDateFormat(this.format).format(new Date(System.currentTimeMillis()));
            ElementRenderer.draw(starX, startY, string);
            return new Dimension(isConfig ? (double)Minecraft.getMinecraft().fontRendererObj.getStringWidth(string) : 0.0, 10.0);
        }
        catch (Exception e) {
            ElementRenderer.draw(starX, startY, "Invalid");
            return new Dimension(0.0, 0.0);
        }
    }
}

