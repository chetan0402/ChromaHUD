/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.renderer.entity.RenderItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package me.chetan.chromahud.displayitems;

import me.chetan.chromahud.ElementRenderer;
import me.chetan.chromahud.JsonHolder;
import me.chetan.chromahud.api.Dimension;
import me.chetan.chromahud.api.DisplayItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArmourHud
extends DisplayItem {
    List<ItemStack> list = new ArrayList<>();
    private int ordinal;
    private boolean dur = false;
    private boolean hand = false;

    public ArmourHud(JsonHolder raw, int ordinal) {
        super(raw, ordinal);
        this.dur = raw.optBoolean("dur");
        this.hand = raw.optBoolean("hand");
    }

    @Override
    public void save() {
        this.data.put("dur", this.dur);
        this.data.put("hand", this.hand);
    }

    @Override
    public Dimension draw(int starX, double startY, boolean isConfig) {
        this.list.clear();
        if (isConfig) {
            this.list.add(new ItemStack(Item.getItemById(276), 1));
            this.list.add(new ItemStack(Item.getItemById(261), 1));
            this.list.add(new ItemStack(Item.getItemById(262), 64));
            this.list.add(new ItemStack(Item.getItemById(310), 1));
            this.list.add(new ItemStack(Item.getItemById(311), 1));
            this.list.add(new ItemStack(Item.getItemById(312), 1));
            this.list.add(new ItemStack(Item.getItemById(313), 1));
        } else {
            this.list = this.itemsToRender();
        }
        this.drawArmour(starX, startY);
        return new Dimension(16.0, this.getHeight());
    }

    public void drawArmour(int x, double y) {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
        if (thePlayer == null || renderItem == null) {
            return;
        }
        ElementRenderer.render(this.list, x, y, this.dur);
    }

    public int getHeight() {
        return this.list.size() * 16;
    }

    private List<ItemStack> itemsToRender() {
        ArrayList<ItemStack> items = new ArrayList<>();
        ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();
        if (this.hand && heldItem != null) {
            items.add(heldItem);
        }
        ItemStack[] inventory = Minecraft.getMinecraft().thePlayer.inventory.armorInventory;
        for (int i = 3; i >= 0; --i) {
            if (inventory[i] == null || inventory[i].getItem() == null) continue;
            items.add(inventory[i]);
        }
        for (ItemStack is : Minecraft.getMinecraft().thePlayer.inventory.mainInventory) {
            if (is == null || !is.getUnlocalizedName().equalsIgnoreCase("item.bow")) continue;
            items.add(is);
            break;
        }
        return items;
    }

    public void toggleDurability() {
        this.dur = !this.dur;
    }

    public void toggleHand() {
        this.hand = !this.hand;
    }
}

