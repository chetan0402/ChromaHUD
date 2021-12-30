/*
 * Decompiled with CFR 0.151.
 */
package me.chetan.chromahud.api;

import java.util.function.Consumer;

public class StringConfig {
    private String string;
    private Consumer<DisplayItem> load;
    private Consumer<DisplayItem> draw;

    public StringConfig(String string, Consumer<DisplayItem> load, Consumer<DisplayItem> draw) {
        this.string = string;
        this.load = load;
        this.draw = draw;
    }

    public StringConfig(String string) {
        this.string = string;
        this.load = displayItem -> {};
        this.draw = displayItem -> {};
    }

    public Consumer<DisplayItem> getLoad() {
        return this.load;
    }

    public Consumer<DisplayItem> getDraw() {
        return this.draw;
    }

    public String getString() {
        return this.string;
    }

    public void setString(String string) {
        this.string = string;
    }
}

