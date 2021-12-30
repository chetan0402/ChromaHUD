/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 */
package me.chetan.chromahud.displayitems;

import me.chetan.chromahud.ElementRenderer;
import me.chetan.chromahud.JsonHolder;
import me.chetan.chromahud.api.Dimension;
import me.chetan.chromahud.api.DisplayItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;

public class PotionEffects
extends DisplayItem {
    public PotionEffects(JsonHolder raw, int ordinal) {
        super(raw, ordinal);
    }

    @Override
    public Dimension draw(int x, double y, boolean isConfig) {
        int row = 0;
        double scale = ElementRenderer.getCurrentScale();
        Collection<PotionEffect> effects = new ArrayList<>();
        if (isConfig) {
            effects.add(new PotionEffect(1, 100, 1));
            effects.add(new PotionEffect(3, 100, 2));
        } else {
            effects = Minecraft.getMinecraft().thePlayer.getActivePotionEffects();
        }
        ArrayList<String> tmp = new ArrayList<>();
        for (PotionEffect potioneffect : effects) {
            Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
            if (!potion.shouldRender(potioneffect)) continue;
            potion.renderInventoryEffect(x, (int)(y + (double)(row * 24) * scale / 100.0), potioneffect, Minecraft.getMinecraft());
            StringBuilder s1 = new StringBuilder(I18n.format(potion.getName()));
            if (potioneffect.getAmplifier() == 1) {
                s1.append(" ").append(I18n.format("enchantment.level.2"));
            } else if (potioneffect.getAmplifier() == 2) {
                s1.append(" ").append(I18n.format("enchantment.level.3"));
            } else if (potioneffect.getAmplifier() == 3) {
                s1.append(" ").append(I18n.format("enchantment.level.4"));
            }
            String s = Potion.getDurationString(potioneffect);
            String text = s1 + " - " + s;
            tmp.add(text);
            ElementRenderer.draw((int)((double)x / scale), y + (double)(row * 16), text);
            ++row;
        }
        return new Dimension(isConfig ? (double)ElementRenderer.maxWidth(tmp) : 0.0, row * 16);
    }
}

