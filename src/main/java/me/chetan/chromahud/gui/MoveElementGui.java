/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.ScaledResolution
 *  org.lwjgl.input.Mouse
 */
package me.chetan.chromahud.gui;

import me.chetan.chromahud.ChromaHUD;
import me.chetan.chromahud.DisplayElement;
import me.chetan.chromahud.ElementRenderer;
import me.chetan.chromahud.ResolutionUtil;
import me.chetan.chromahud.gui.DisplayElementConfig;
import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

public class MoveElementGui
extends GuiScreen {
    private HashMap<GuiButton, Consumer<GuiButton>> clicks = new HashMap();
    private HashMap<GuiButton, Consumer<GuiButton>> updates = new HashMap();
    private HashMap<String, GuiButton> nameMap = new HashMap();
    private ChromaHUD mod;
    private DisplayElement element;
    private GuiButton edit;
    private double lastX;
    private double lastY;
    private boolean lastD = false;
    private boolean mouseLock;

    public MoveElementGui(ChromaHUD mod, DisplayElement element) {
        this.mod = mod;
        this.element = element;
        this.mouseLock = Mouse.isButtonDown((int)0);
    }

    public void initGui() {
        super.initGui();
        this.edit = new GuiButton(1, 5, 5, 100, 20, "Save");
        this.reg("", this.edit, button -> Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new DisplayElementConfig(this.element, this.mod)), guiButton -> {});
    }

    public void onGuiClosed() {
        super.onGuiClosed();
        this.mod.saveState();
    }

    public void updateScreen() {
        super.updateScreen();
        for (GuiButton guiButton : this.buttonList) {
            Consumer<GuiButton> guiButtonConsumer = this.updates.get(guiButton);
            if (guiButtonConsumer == null) continue;
            guiButtonConsumer.accept(guiButton);
        }
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        Consumer<GuiButton> guiButtonConsumer = this.clicks.get(button);
        if (guiButtonConsumer != null) {
            guiButtonConsumer.accept(button);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution current = ResolutionUtil.current();
        MoveElementGui.drawRect((int)0, (int)0, (int)current.getScaledWidth(), (int)current.getScaledHeight(), (int)new Color(0, 0, 0, 150).getRGB());
        this.mouseLock = this.mouseLock && Mouse.isButtonDown((int)0);
        super.drawScreen(mouseX, mouseY, partialTicks);
        ElementRenderer.startDrawing(this.element);
        this.element.drawForConfig();
        ElementRenderer.endDrawing(this.element);
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        double x1 = this.element.getXloc() * resolution.getScaledWidth_double();
        double x2 = this.element.getXloc() * resolution.getScaledWidth_double() + this.element.getDimensions().getWidth();
        double y1 = this.element.getYloc() * resolution.getScaledHeight_double();
        double y2 = this.element.getYloc() * resolution.getScaledHeight_double() + this.element.getDimensions().getHeight();
        this.drawHorizontalLine((int)(x1 - 5.0), (int)(x2 + 5.0), (int)y1 - 5, Color.RED.getRGB());
        this.drawHorizontalLine((int)(x1 - 5.0), (int)(x2 + 5.0), (int)y2 + 5, Color.RED.getRGB());
        this.drawVerticalLine((int)x1 - 5, (int)(y1 - 5.0), (int)(y2 + 5.0), Color.RED.getRGB());
        this.drawVerticalLine((int)x2 + 5, (int)(y1 - 5.0), (int)(y2 + 5.0), Color.RED.getRGB());
        int propX = (int)x1 - 5;
        int propY = (int)y1 - 30;
        if (propX < 10 || propX > resolution.getScaledWidth() - 200) {
            propX = resolution.getScaledWidth() / 2;
        }
        if (propY > resolution.getScaledHeight() - 5 || propY < 0) {
            propY = resolution.getScaledHeight() / 2;
        }
        this.edit.xPosition = propX;
        this.edit.yPosition = propY;
        if (Mouse.isButtonDown((int)0) && !this.mouseLock) {
            if ((double)mouseX > x1 && (double)mouseX < x2 && (double)mouseY > y1 && (double)mouseY < y2 || this.lastD) {
                double x3 = (double)Mouse.getX() / ResolutionUtil.current().getScaledWidth_double();
                double y3 = (double)Mouse.getY() / ResolutionUtil.current().getScaledHeight_double();
                this.element.setXloc(this.element.getXloc() - (this.lastX - x3) / (double)ResolutionUtil.current().getScaleFactor());
                this.element.setYloc(this.element.getYloc() + (this.lastY - y3) / (double)ResolutionUtil.current().getScaleFactor());
                if (this.element.getXloc() < 0.0) {
                    this.element.setXloc(0.0);
                }
                if (this.element.getYloc() < 0.0) {
                    this.element.setYloc(0.0);
                }
                if (this.element.getXloc() * (double)resolution.getScaledWidth() + (double)this.element.getDimensions().width > (double)resolution.getScaledWidth()) {
                    this.element.setXloc((resolution.getScaledWidth_double() - (double)this.element.getDimensions().width) / resolution.getScaledWidth_double());
                }
                if (this.element.getYloc() * (double)resolution.getScaledHeight() + (double)this.element.getDimensions().height > (double)resolution.getScaledHeight()) {
                    this.element.setYloc((resolution.getScaledHeight_double() - (double)this.element.getDimensions().height) / resolution.getScaledHeight_double());
                }
                this.lastD = true;
            } else {
                this.lastD = false;
            }
        } else {
            this.lastD = false;
        }
        this.lastX = (double)Mouse.getX() / ResolutionUtil.current().getScaledWidth_double();
        this.lastY = (double)Mouse.getY() / ResolutionUtil.current().getScaledHeight_double();
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void reg(String name, GuiButton button, Consumer<GuiButton> consumer, Consumer<GuiButton> tick) {
        this.buttonList.add(button);
        this.clicks.put(button, consumer);
        this.updates.put(button, tick);
        this.nameMap.put(name, button);
    }
}

