/*
 * Decompiled with CFR 0.151.
 */
package me.chetan.chromahud.displayitems;

import me.chetan.chromahud.ElementRenderer;
import me.chetan.chromahud.JsonHolder;
import me.chetan.chromahud.api.Dimension;
import me.chetan.chromahud.api.DisplayItem;
import java.util.ArrayList;

public class TextItem
extends DisplayItem {
    private String text;

    public TextItem(JsonHolder object, int ordinal) {
        super(object, ordinal);
        this.text = object.optString("text");
    }

    @Override
    public Dimension draw(int x, double y, boolean isConfig) {
        ArrayList<String> list = new ArrayList<>();
        if (this.text.isEmpty()) {
            list.add("Text is empty??");
        } else {
            list.add(this.text);
        }
        ElementRenderer.draw(x, y, list);
        return new Dimension(ElementRenderer.maxWidth(list), 10.0);
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
        this.data.put("text", text);
    }
}

