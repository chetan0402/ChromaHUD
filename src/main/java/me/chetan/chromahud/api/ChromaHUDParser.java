/*
 * Decompiled with CFR 0.151.
 */
package me.chetan.chromahud.api;

import me.chetan.chromahud.JsonHolder;
import me.chetan.chromahud.api.ChromaHUDDescription;
import me.chetan.chromahud.api.DisplayItem;
import java.util.Map;

public interface ChromaHUDParser {
    public DisplayItem parse(String var1, int var2, JsonHolder var3);

    public Map<String, String> getNames();

    public ChromaHUDDescription description();
}

