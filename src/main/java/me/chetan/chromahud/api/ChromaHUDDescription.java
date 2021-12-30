/*
 * Decompiled with CFR 0.151.
 */
package me.chetan.chromahud.api;

public class ChromaHUDDescription {
    private String id;
    private String version;
    private String name;
    private String description;

    public ChromaHUDDescription(String id, String version, String name, String description) {
        this.id = id;
        this.version = version;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return this.id;
    }

    public String getVersion() {
        return this.version;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }
}

