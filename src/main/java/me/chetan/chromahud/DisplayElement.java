package me.chetan.chromahud;

import com.google.gson.JsonArray;
import me.chetan.chromahud.api.Dimension;
import me.chetan.chromahud.api.DisplayItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DisplayElement {
    private double xloc;
    private double yloc;
    private List<DisplayItem> displayItems = new ArrayList<DisplayItem>();
    private double scale = 1.0;
    private int color;
    private double prevX;
    private double prevY;
    private boolean shadow;
    private boolean highlighted;
    private JsonHolder data;
    private double brightness;

    public DisplayElement(JsonHolder object) {
        this.data = object;
        this.xloc = object.optDouble("x");
        this.yloc = object.optDouble("y");
        this.scale = object.optDouble("scale");
        ArrayList<DisplayItem> items = new ArrayList<DisplayItem>();
        JsonArray itemss = object.optJSONArray("items");
        int ord = 0;
        for (int i1 = 0; i1 < itemss.size(); ++i1) {
            JsonHolder item = new JsonHolder(itemss.get(i1).getAsJsonObject());
            DisplayItem type = ChromaHUDApi.getInstance().parse(item.optString("type"), ord, item);
            if (type == null) continue;
            items.add(type);
            ++ord;
        }
        this.displayItems = items;
        this.shadow = object.optBoolean("shadow");
        this.highlighted = object.optBoolean("highlighted");
        this.brightness = this.data.optDouble("brightness");
        this.color = this.data.optInt("color");
        this.recalculateColor();
    }

    public static DisplayElement blank() {
        return new DisplayElement(new JsonHolder().put("x", 0.5).put("y", 0.5).put("scale", 1).put("color", Color.WHITE.getRGB()).put("color_pallet", true));
    }

    public double getBrightness() {
        return this.data.optDouble("brightness");
    }

    public void setBrightness(double brightness) {
        this.data.put("brightness", brightness);
    }

    public String toString() {
        return "DisplayElement{xloc=" + this.xloc + ", yloc=" + this.yloc + ", displayItems=" + this.displayItems + ", scale=" + this.scale + ", color=" + this.color + '}';
    }

    public boolean isChroma() {
        return this.data.optBoolean("chroma");
    }

    public void setChroma(boolean chroma) {
        this.data.put("chroma", chroma);
    }

    public void recalculateColor() {
        if (this.isChroma()) {
            this.color = 0;
        } else if (this.isRGB()) {
            this.color = new Color(this.data.optInt("red"), this.data.optInt("green"), this.data.optInt("blue")).getRGB();
        }
    }

    public void reformatColor() {
        if (this.isChroma()) {
            // empty if block
        }
    }

    public void draw() {
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        int x = (int)(this.xloc * resolution.getScaledWidth_double());
        double y = (int)(this.yloc * resolution.getScaledHeight_double());
        for (DisplayItem iDisplayItem : this.displayItems) {
            try {
                Dimension d = iDisplayItem.draw(x, y, false);
                y += d.getHeight() * ElementRenderer.getCurrentScale();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
        this.data.put("color", color);
    }

    public double getXloc() {
        return this.xloc;
    }

    public void setXloc(double xloc) {
        this.data.put("x", xloc);
        this.xloc = xloc;
    }

    public void removeDisplayItem(int ordinal) {
        this.displayItems.remove(ordinal);
        this.adjustOrdinal();
    }

    public double getYloc() {
        return this.yloc;
    }

    public void setYloc(double yloc) {
        this.data.put("y", yloc);
        this.yloc = yloc;
    }

    public List<DisplayItem> getDisplayItems() {
        return this.displayItems;
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.data.put("scale", scale);
        this.scale = scale;
    }

    public java.awt.Dimension getDimensions() {
        return new java.awt.Dimension((int)this.prevX, (int)this.prevY);
    }

    public void drawForConfig() {
        this.recalculateColor();
        this.prevX = 0.0;
        this.prevY = 0.0;
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        double addy = 0.0;
        int x = (int)(this.xloc * resolution.getScaledWidth_double());
        double y = (int)(this.yloc * resolution.getScaledHeight_double());
        for (DisplayItem iDisplayItem : this.displayItems) {
            Dimension d = iDisplayItem.draw(x, y, true);
            y += d.getHeight() * ElementRenderer.getCurrentScale();
            addy += d.getHeight() * ElementRenderer.getCurrentScale();
            this.prevX = (int)Math.max(d.getWidth() * ElementRenderer.getCurrentScale(), this.prevX);
        }
        this.prevY = addy;
    }

    public void renderEditView() {
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        int x = (int)(0.8 * resolution.getScaledWidth_double());
        double y = (int)(0.2 * resolution.getScaledHeight_double());
        for (DisplayItem iDisplayItem : this.displayItems) {
            Dimension d = iDisplayItem.draw(x, y, false);
            y += d.getHeight() * ElementRenderer.getCurrentScale();
        }
    }

    public void adjustOrdinal() {
        for (int ord = 0; ord < this.displayItems.size(); ++ord) {
            this.displayItems.get(ord).setOrdinal(ord);
        }
    }

    public void setRgb(boolean state) {
        this.data.put("rgb", state);
    }

    public boolean isRGB() {
        return this.data.optBoolean("rgb");
    }

    public boolean isColorPallet() {
        return this.data.optBoolean("color_pallet");
    }

    public void setColorPallet(boolean state) {
        this.data.put("color_pallet", state);
    }

    public boolean isStaticChroma() {
        return this.data.optBoolean("static_chroma");
    }

    public void setStaticChroma(boolean state) {
        this.data.put("static_chroma", state);
    }

    public boolean isShadow() {
        return this.shadow;
    }

    public void setShadow(boolean shadow) {
        this.data.put("shadow", shadow);
        this.shadow = shadow;
    }

    public boolean isHighlighted() {
        return this.highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.data.put("highlighted", highlighted);
        this.highlighted = highlighted;
    }

    public JsonHolder getData() {
        return this.data;
    }
}

