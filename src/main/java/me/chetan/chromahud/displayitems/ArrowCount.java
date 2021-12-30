/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package me.chetan.chromahud.displayitems;

import me.chetan.chromahud.ElementRenderer;
import me.chetan.chromahud.JsonHolder;
import me.chetan.chromahud.api.Dimension;
import me.chetan.chromahud.api.DisplayItem;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ArrowCount
extends DisplayItem {
    public ArrowCount(JsonHolder data, int ordinal) {
        super(data, ordinal);
        this.data = data;
    }

    @Override
    public Dimension draw(int starX, double startY, boolean isConfig) {
        ArrayList<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(Item.getItemById(262), 64));
        EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
        if (thePlayer != null) {
            int c = 0;
            for (ItemStack is : thePlayer.inventory.mainInventory) {
                if (is == null || !is.getUnlocalizedName().equalsIgnoreCase("item.arrow")) continue;
                c += is.stackSize;
            }
            ElementRenderer.render(list, starX, startY, false);
            ElementRenderer.draw(starX + 16, startY + 8.0, "x" + (isConfig ? 64 : c));
            return new Dimension(16.0, 16.0);
        }
        return new Dimension(0.0, 0.0);
    }
}

