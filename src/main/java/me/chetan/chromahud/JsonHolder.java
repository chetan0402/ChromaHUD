/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonPrimitive
 */
package me.chetan.chromahud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JsonHolder {
    private JsonObject object;

    public JsonHolder(JsonObject object) {
        this.object = object;
    }

    public JsonHolder(String raw) {
        this(new JsonParser().parse(raw).getAsJsonObject());
    }

    public JsonHolder() {
        this(new JsonObject());
    }

    public static String[] getNames(JsonHolder statsObject) {
        List<String> keys = statsObject.getKeys();
        String[] keyArray = new String[keys.size()];
        for (int i = 0; i < keys.size(); ++i) {
            keyArray[i] = keys.get(i);
        }
        return keyArray;
    }

    public String toString() {
        return this.object.toString();
    }

    public JsonHolder put(String key, boolean value) {
        this.object.addProperty(key, Boolean.valueOf(value));
        return this;
    }

    public void mergeNotOverride(JsonHolder merge) {
        this.merge(merge, false);
    }

    public void mergeOverride(JsonHolder merge) {
        this.merge(merge, true);
    }

    public void merge(JsonHolder merge, boolean override) {
        JsonObject object = merge.getObject();
        for (String s : merge.getKeys()) {
            if (!override && this.has(s)) continue;
            this.put(s, object.get(s));
        }
    }

    public JsonHolder put(String key, String value) {
        this.object.addProperty(key, value);
        return this;
    }

    public JsonHolder put(String key, int value) {
        this.object.addProperty(key, (Number)value);
        return this;
    }

    public JsonHolder put(String key, double value) {
        this.object.addProperty(key, (Number)value);
        return this;
    }

    public JsonHolder put(String key, long value) {
        this.object.addProperty(key, (Number)value);
        return this;
    }

    public JsonHolder optJsonObject(String key, JsonObject fallBack) {
        try {
            return new JsonHolder(this.object.get(key).getAsJsonObject());
        }
        catch (Exception e) {
            return new JsonHolder(fallBack);
        }
    }

    public JsonHolder optJsonObject(String key, JsonHolder fallBack) {
        try {
            return new JsonHolder(this.object.get(key).getAsJsonObject());
        }
        catch (Exception e) {
            return fallBack;
        }
    }

    public JsonArray optJSONArray(String key, JsonArray fallback) {
        try {
            return this.object.get(key).getAsJsonArray();
        }
        catch (Exception e) {
            return fallback;
        }
    }

    public JsonArray optJSONArray(String key) {
        return this.optJSONArray(key, new JsonArray());
    }

    public boolean has(String key) {
        return this.object.has(key);
    }

    public long optLong(String key, long fallback) {
        try {
            return this.object.get(key).getAsLong();
        }
        catch (Exception e) {
            return fallback;
        }
    }

    public long optLong(String key) {
        return this.optLong(key, 0L);
    }

    public boolean optBoolean(String key, boolean fallback) {
        try {
            return this.object.get(key).getAsBoolean();
        }
        catch (Exception e) {
            return fallback;
        }
    }

    public boolean optBoolean(String key) {
        return this.optBoolean(key, false);
    }

    public JsonObject optActualJsonObject(String key) {
        try {
            return this.object.get(key).getAsJsonObject();
        }
        catch (Exception e) {
            return new JsonObject();
        }
    }

    public JsonHolder optJsonObject(String key) {
        return this.optJsonObject(key, new JsonObject());
    }

    public int optInt(String key, int fallBack) {
        try {
            return this.object.get(key).getAsInt();
        }
        catch (Exception e) {
            return fallBack;
        }
    }

    public int optInt(String key) {
        return this.optInt(key, 0);
    }

    public String optString(String key, String fallBack) {
        try {
            return this.object.get(key).getAsString();
        }
        catch (Exception e) {
            return fallBack;
        }
    }

    public String optString(String key) {
        return this.optString(key, "");
    }

    public double optDouble(String key, double fallBack) {
        try {
            return this.object.get(key).getAsDouble();
        }
        catch (Exception e) {
            return fallBack;
        }
    }

    public List<String> getKeys() {
        ArrayList<String> tmp = new ArrayList<String>();
        for (Map.Entry stringJsonElementEntry : this.object.entrySet()) {
            tmp.add((String)stringJsonElementEntry.getKey());
        }
        return tmp;
    }

    public double optDouble(String key) {
        return this.optDouble(key, 0.0);
    }

    public JsonObject getObject() {
        return this.object;
    }

    public boolean isNull(String key) {
        return this.object.has(key) && this.object.get(key).isJsonNull();
    }

    public JsonHolder put(String values, JsonHolder values1) {
        this.put(values, values1.getObject());
        return this;
    }

    public void put(String values, JsonObject object) {
        this.object.add(values, (JsonElement)object);
    }

    public void remove(String header) {
        this.object.remove(header);
    }

    public String getString(String seed) {
        return this.optString(seed);
    }

    public JsonArray optJSONArrayNonNull(String matches) {
        return this.optJSONArray(matches);
    }

    public int getInt(String type) {
        return this.optInt(type);
    }

    public UUID getUUID(String offender) {
        return this.optUUID(offender);
    }

    private UUID optUUID(String offender) {
        return UUID.fromString(this.optString(offender));
    }

    public String getOptString(String removedBy) {
        return this.optString(removedBy, null);
    }

    public long getLong(String dateIssued) {
        return this.optLong(dateIssued);
    }

    public long getOptLong(String removeDate, long l) {
        return this.optLong(removeDate, l);
    }

    public Object get(String field) {
        return this.object.get(field);
    }

    public void put(String key, JsonArray jsonArrayHolder) {
        this.object.add(key, (JsonElement)jsonArrayHolder);
    }

    public JsonHolder put(String key, JsonElement element) {
        this.object.add(key, element);
        return this;
    }

    public JsonHolder put(String key, Object value) {
        return this.put(key, value.toString());
    }

    public JsonHolder put(String key, Number value) {
        this.object.add(key, (JsonElement)new JsonPrimitive(value));
        return this;
    }

    public JsonHolder getJsonHolder(String entry) {
        return this.optJsonObject(entry);
    }

    public void putNull(String key) {
        this.object.add(key, (JsonElement)JsonNull.INSTANCE);
    }

    public JsonElement getElement(String field) {
        return this.object.get(field);
    }
}

