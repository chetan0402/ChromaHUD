package me.chetan.chromahud;

import me.chetan.chromahud.api.ChromaHUDDescription;
import me.chetan.chromahud.api.ChromaHUDParser;
import me.chetan.chromahud.api.DisplayItem;
import me.chetan.chromahud.displayitems.*;

import java.util.HashMap;
import java.util.Map;

public class DefaultChromaHudParser
implements ChromaHUDParser {
    private HashMap<String, String> names = new HashMap();

    public DefaultChromaHudParser() {
        this.names.put("CORDS", "Cords");
        this.names.put("TEXT", "Text");
        this.names.put("ARMOUR_HUD", "Armour");
        this.names.put("POTION", "Potion");
        this.names.put("FPS", "FPS");
        this.names.put("PING", "Ping");
        this.names.put("DIRECTION", "Direction");
        this.names.put("CPS", "CPS");
        this.names.put("ARROW_COUNT", "Arrow Count");
        this.names.put("TIME", "Time");
        this.names.put("C_COUNTER", "C Counter");
    }

    @Override
    public DisplayItem parse(String type, int ord, JsonHolder item) {
        switch (type) {
            case "CORDS": {
                return new CordsDisplay(item, ord);
            }
            case "TEXT": {
                return new TextItem(item, ord);
            }
            case "ARMOUR_HUD": {
                return new ArmourHud(item, ord);
            }
            case "POTION": {
                return new PotionEffects(item, ord);
            }
            case "FPS": {
                return new FPS(item, ord);
            }
            case "PING": {
                return new PingDisplay(item, ord);
            }
            case "DIRECTION": {
                return new DirectionHUD(item, ord);
            }
            case "CPS": {
                return new CpsDisplay(item, ord);
            }
            case "ARROW_COUNT": {
                return new ArrowCount(item, ord);
            }
            case "C_COUNTER": {
                return new CCounter(item, ord);
            }
            case "TIME": {
                return new TimeHud(item, ord);
            }
        }
        return null;
    }

    @Override
    public Map<String, String> getNames() {
        return this.names;
    }

    @Override
    public ChromaHUDDescription description() {
        return new ChromaHUDDescription("DEFAULT", "3.0", "ChromaHUD", "Default display items for ChromaHUD.");
    }
}

