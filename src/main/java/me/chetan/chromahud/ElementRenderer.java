package me.chetan.chromahud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ElementRenderer {
    protected static ScaledResolution resolution;
    private static double currentScale;
    private static int color;
    private static int[] COLORS;
    private static boolean display;
    private static List<Long> clicks;
    private static DisplayElement current;
    private static FontRenderer fontRendererObj;
    private static String cValue;
    boolean last = false;
    private ChromaHUD mod;
    private Minecraft minecraft;

    public ElementRenderer(ChromaHUD mod) {
        this.mod = mod;
        this.minecraft = Minecraft.getMinecraft();
    }

    public static String getCValue() {
        return cValue;
    }

    public static double getCurrentScale() {
        return currentScale;
    }

    public static int getColor(int c, int x) {
        return c;
    }

    public static void display() {
        display = true;
    }

    public static void draw(int x, double y, String string) {
        ArrayList<String> tmp = new ArrayList<String>();
        tmp.add(string);
        ElementRenderer.draw(x, y, tmp);
    }

    public static int RGBtoHEX(Color color) {
        String hex = Integer.toHexString(color.getRGB() & 0xFFFFFF);
        if (hex.length() < 6) {
            if (hex.length() == 5) {
                hex = "0" + hex;
            }
            if (hex.length() == 4) {
                hex = "00" + hex;
            }
            if (hex.length() == 3) {
                hex = "000" + hex;
            }
        }
        hex = "#" + hex;
        return Integer.decode(hex);
    }

    public static void draw(int x, double y, List<String> list) {
        int tx = x;
        double ty = y;
        for (String string : list) {
            if (current.isHighlighted()) {
                int stringWidth = fontRendererObj.getStringWidth(string);
                Gui.drawRect((int)((int)((double)(tx - 1) / ElementRenderer.getCurrentScale())), (int)((int)((ty - 1.0) / ElementRenderer.getCurrentScale())), (int)((int)((double)(tx + 1) / ElementRenderer.getCurrentScale()) + stringWidth), (int)((int)((ty + 1.0) / ElementRenderer.getCurrentScale()) + 8), (int)new Color(0, 0, 0, 125).getRGB());
            }
            if (current.isChroma()) {
                ElementRenderer.drawChromaString(string, tx, (int)ty);
            } else {
                fontRendererObj.drawString(string, (float)((int)((double)tx / ElementRenderer.getCurrentScale())), (float)((int)(ty / ElementRenderer.getCurrentScale())), ElementRenderer.getColor(color, x), current.isShadow());
            }
            ty += 10.0;
        }
    }

    public static void drawChromaString(String text, int xIn, int y) {
        FontRenderer renderer = Minecraft.getMinecraft().fontRendererObj;
        int x = xIn;
        for (char c : text.toCharArray()) {
            long dif = x * 10 - y * 10;
            if (current.isStaticChroma()) {
                dif = 0L;
            }
            long l = System.currentTimeMillis() - dif;
            float ff = current.isStaticChroma() ? 1000.0f : 2000.0f;
            int i = Color.HSBtoRGB((float)(l % (long)((int)ff)) / ff, 0.8f, 0.8f);
            String tmp = String.valueOf(c);
            renderer.drawString(tmp, (float)((double)x / ElementRenderer.getCurrentScale()), (float)((double)y / ElementRenderer.getCurrentScale()), i, current.isShadow());
            x = (int)((double)x + (double)renderer.getCharWidth(c) * ElementRenderer.getCurrentScale());
        }
    }

    private static boolean isChromaInt(int e) {
        return e >= 0 && e <= 1;
    }

    public static int maxWidth(List<String> list) {
        int max = 0;
        for (String s : list) {
            max = Math.max(max, Minecraft.getMinecraft().fontRendererObj.getStringWidth(s));
        }
        return max;
    }

    public static int getColor() {
        return color;
    }

    public static int getCPS() {
        Iterator<Long> iterator = clicks.iterator();
        while (iterator.hasNext()) {
            if (System.currentTimeMillis() - iterator.next() <= 1000L) continue;
            iterator.remove();
        }
        return clicks.size();
    }

    public static DisplayElement getCurrent() {
        return current;
    }

    public static void render(List<ItemStack> itemStacks, int x, double y, boolean showDurability) {
        int line = 0;
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        for (ItemStack stack : itemStacks) {
            renderItem.renderItemAndEffectIntoGUI(stack, (int)((double)x / ElementRenderer.getCurrentScale()), (int)((y + (double)(16 * line) * ElementRenderer.getCurrentScale()) / ElementRenderer.getCurrentScale()));
            if (showDurability) {
                String dur = stack.getMaxDamage() - stack.getItemDamage() + "/" + stack.getMaxDamage();
                ElementRenderer.draw((int)((double)x + 20.0 * currentScale), y + (double)(16 * line) + 8.0, dur);
            }
            ++line;
        }
    }

    public static void startDrawing(DisplayElement element) {
        GlStateManager.scale((double)element.getScale(), (double)element.getScale(), (double)0.0);
        currentScale = element.getScale();
        color = element.getColor();
        current = element;
    }

    public static void endDrawing(DisplayElement element) {
        GlStateManager.scale((double)(1.0 / element.getScale()), (double)(1.0 / element.getScale()), (double)0.0);
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (display) {
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)this.mod.getConfigGuiInstance());
            display = false;
            boolean bl = false;
        }
        if (Minecraft.getMinecraft().inGameHasFocus) {
            cValue = Minecraft.getMinecraft().renderGlobal.getDebugInfoRenders().split("/")[0].trim();
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        resolution = new ScaledResolution(Minecraft.getMinecraft());
        if (!this.minecraft.inGameHasFocus || this.minecraft.gameSettings.showDebugInfo) {
            return;
        }
        this.renderElements();
    }

    public void renderElements() {
        boolean m;
        if (fontRendererObj == null) {
            fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
        }
        if ((m = Mouse.isButtonDown((int)0)) != this.last) {
            this.last = m;
            if (m) {
                clicks.add(System.currentTimeMillis());
            }
        }
        List<DisplayElement> elementList = this.mod.getDisplayElements();
        for (DisplayElement element : elementList) {
            ElementRenderer.startDrawing(element);
            try {
                element.draw();
            }
            catch (Exception exception) {
                // empty catch block
            }
            ElementRenderer.endDrawing(element);
        }
    }

    static {
        currentScale = 1.0;
        COLORS = new int[]{0xFFFFFF, 0xFF0000, 65280, 255, 0xFFFF00, 0xAA00AA};
        display = false;
        clicks = new ArrayList<Long>();
        fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
    }
}

