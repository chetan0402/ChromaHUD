/*
 * Decompiled with CFR 0.151.
 */
package me.chetan.chromahud.api;

import me.chetan.chromahud.ChromaHUDApi;
import me.chetan.chromahud.JsonHolder;
import me.chetan.chromahud.api.ButtonConfig;
import me.chetan.chromahud.api.Dimension;
import java.util.ArrayList;
import java.util.List;

public abstract class DisplayItem {
    private int ordinal;
    protected JsonHolder data;

    public JsonHolder getData() {
        this.save();
        return this.data;
    }

    public void save() {
    }

    public DisplayItem(JsonHolder data, int ordinal) {
        this.data = data;
        this.ordinal = ordinal;
    }

    public String name() {
        return ChromaHUDApi.getInstance().getName(this.data.optString("type"));
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public abstract Dimension draw(int var1, double var2, boolean var4);

    public List<ButtonConfig> configOptions() {
        return new ArrayList<ButtonConfig>();
    }

    public String getType() {
        return this.data.optString("type");
    }
}

