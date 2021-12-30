package me.chetan.chromahud;

import com.google.gson.JsonArray;
import me.chetan.chromahud.api.*;
import me.chetan.chromahud.displayitems.TextItem;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class ChromaHUDApi {
    public static final String VERSION = "3.0";
    private static ChromaHUDApi instance;
    private List<ChromaHUDParser> parsers = new ArrayList<ChromaHUDParser>();
    private HashMap<String, String> names = new HashMap();
    private List<DisplayElement> elements = new ArrayList<DisplayElement>();
    private boolean posted = false;
    private HashMap<String, ArrayList<ButtonConfig>> buttonConfigs = new HashMap();
    private HashMap<String, ArrayList<TextConfig>> textConfigs = new HashMap();
    private HashMap<String, ArrayList<StringConfig>> stringConfigs = new HashMap();

    private ChromaHUDApi() {
        instance = this;
    }

    public static ChromaHUDApi getInstance() {
        if (instance == null) {
            instance = new ChromaHUDApi();
        }
        return instance;
    }

    public List<ButtonConfig> getButtonConfigs(String type) {
        ArrayList<ButtonConfig> configs = this.buttonConfigs.get(type.toLowerCase());
        if (configs != null) {
            return new ArrayList<>(configs);
        }
        return new ArrayList<ButtonConfig>();
    }

    public List<TextConfig> getTextConfigs(String type) {
        ArrayList<TextConfig> configs = this.textConfigs.get(type.toLowerCase());
        if (configs != null) {
            return new ArrayList<>(configs);
        }
        return new ArrayList<>();
    }

    public List<StringConfig> getStringConfigs(String type) {
        ArrayList<StringConfig> configs = this.stringConfigs.get(type.toLowerCase());
        if (configs != null) {
            return new ArrayList<StringConfig>(configs);
        }
        return new ArrayList<StringConfig>();
    }

    public List<DisplayElement> getElements() {
        return this.elements;
    }

    public void registerTextConfig(String type, TextConfig config) {
        if (!this.textConfigs.containsKey(type = type.toLowerCase())) {
            this.textConfigs.put(type, new ArrayList());
        }
        this.textConfigs.get(type).add(config);
    }

    public void registerStringConfig(String type, StringConfig config) {
        if (!this.stringConfigs.containsKey(type = type.toLowerCase())) {
            this.stringConfigs.put(type, new ArrayList());
        }
        this.stringConfigs.get(type).add(config);
    }

    public void registerButtonConfig(String type, ButtonConfig config) {
        if (!this.buttonConfigs.containsKey(type = type.toLowerCase())) {
            this.buttonConfigs.put(type, new ArrayList());
        }
        this.buttonConfigs.get(type).add(config);
    }

    public void register(ChromaHUDParser parser) {
        if (this.posted) {
            throw new IllegalStateException("Cannot register parser after FMLPostInitialization event");
        }
        this.parsers.add(parser);
        this.names.putAll(parser.getNames());
    }

    protected void post(JsonHolder config) {
        if (this.posted) {
            throw new IllegalStateException("Already posted!");
        }
        this.posted = true;
        this.elements.clear();
        JsonArray displayElements = config.optJSONArray("elements");
        for (int i = 0; i < displayElements.size(); ++i) {
            JsonHolder object = new JsonHolder(displayElements.get(i).getAsJsonObject());
            try {
                DisplayElement e = new DisplayElement(object);
                if (e.getDisplayItems().size() <= 0) continue;
                this.elements.add(e);
                continue;
            }
            catch (Exception e) {
                e.printStackTrace();
                Logger.getLogger("ChromaHUD").severe("A fatal error occurred while loading the display element " + object);
            }
        }
        if (!config.has("elements")) {
            DisplayElement blank = DisplayElement.blank();
            TextItem e = new TextItem(new JsonHolder().put("type", "TEXT"), 0);
            e.setText("/chromahud to get started :)");
            blank.setHighlighted(true);
            blank.setColor(Color.RED.getRGB());
            blank.getDisplayItems().add(e);
            this.getElements().add(blank);
        }
    }

    public DisplayItem parse(String type, int ord, JsonHolder item) {
        for (ChromaHUDParser parser : this.parsers) {
            DisplayItem parsed = parser.parse(type, ord, item);
            if (parsed == null) continue;
            return parsed;
        }
        return null;
    }

    public String getName(String type) {
        return this.names.getOrDefault(type, type);
    }

    public List<ChromaHUDParser> getParsers() {
        return new ArrayList<ChromaHUDParser>(this.parsers);
    }
}

