package me.chetan.chromahud.gui;

import me.chetan.chromahud.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class GeneralConfigGui
extends GuiScreen {
    private ChromaHUD mod;
    private boolean mouseDown;
    private DisplayElement currentElement;
    private GuiButton edit;
    private HashMap<GuiButton, Consumer<GuiButton>> clicks = new HashMap();
    private boolean mouseLock;

    public GeneralConfigGui(ChromaHUD mod) {
        this.mod = mod;
        this.mouseLock = Mouse.isButtonDown((int)0);
    }

    private void reg(GuiButton button, Consumer<GuiButton> consumer) {
        this.buttonList.add(button);
        this.clicks.put(button, consumer);
    }

    public void initGui() {
        super.initGui();
        this.edit = new GuiButton(1, 5, 5, 100, 20, "Edit");
        this.reg(this.edit, button -> {
            if (this.currentElement != null) {
                Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new DisplayElementConfig(this.currentElement, this.mod));
            }
        });
        this.reg(new GuiButton(2, 2, ResolutionUtil.current().getScaledHeight() - 22, 100, 20, "New"), guiButton -> {
            DisplayElement blank = DisplayElement.blank();
            ChromaHUDApi.getInstance().getElements().add(blank);
            System.out.println(ChromaHUDApi.getInstance().getElements());
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new DisplayElementConfig(blank, this.mod));
        });
        this.edit.visible = false;
    }

    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution current = ResolutionUtil.current();
        GeneralConfigGui.drawRect((int)0, (int)0, (int)current.getScaledWidth(), (int)current.getScaledHeight(), (int)new Color(0, 0, 0, 150).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
        List<DisplayElement> elementList = this.mod.getDisplayElements();
        for (DisplayElement element : elementList) {
            ElementRenderer.startDrawing(element);
            try {
                element.drawForConfig();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            ElementRenderer.endDrawing(element);
        }
        if (this.currentElement != null) {
            ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
            double x1 = this.currentElement.getXloc() * resolution.getScaledWidth_double();
            double x2 = this.currentElement.getXloc() * resolution.getScaledWidth_double() + this.currentElement.getDimensions().getWidth();
            double y1 = this.currentElement.getYloc() * resolution.getScaledHeight_double();
            double y2 = this.currentElement.getYloc() * resolution.getScaledHeight_double() + this.currentElement.getDimensions().getHeight();
            this.drawHorizontalLine((int)(x1 - 5.0), (int)(x2 + 5.0), (int)y1 - 5, Color.RED.getRGB());
            this.drawHorizontalLine((int)(x1 - 5.0), (int)(x2 + 5.0), (int)y2 + 5, Color.RED.getRGB());
            this.drawVerticalLine((int)x1 - 5, (int)(y1 - 5.0), (int)(y2 + 5.0), Color.RED.getRGB());
            this.drawVerticalLine((int)x2 + 5, (int)(y1 - 5.0), (int)(y2 + 5.0), Color.RED.getRGB());
            this.edit.visible = true;
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
        } else {
            this.edit.visible = false;
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        ScaledResolution current = ResolutionUtil.current();
        int i = Mouse.getEventDWheel();
        List<DisplayElement> elements = ChromaHUDApi.getInstance().getElements();
        if (elements.size() > 0) {
            int i1;
            if (i < 0) {
                if (this.currentElement == null) {
                    this.currentElement = elements.get(0);
                } else {
                    i1 = elements.indexOf(this.currentElement);
                    if (++i1 > elements.size() - 1) {
                        i1 = 0;
                    }
                    this.currentElement = elements.get(i1);
                }
            } else if (i > 0) {
                if (this.currentElement == null) {
                    this.currentElement = elements.get(0);
                } else {
                    i1 = elements.indexOf(this.currentElement);
                    if (--i1 < 0) {
                        i1 = elements.size() - 1;
                    }
                    this.currentElement = elements.get(i1);
                }
            }
        }
        boolean isOver = false;
        for (GuiButton button : this.buttonList) {
            if (!button.isMouseOver()) continue;
            isOver = true;
        }
        if (!this.mouseDown && Mouse.isButtonDown((int)0) && !isOver) {
            int clickX = Mouse.getX() / current.getScaleFactor();
            int clickY = Mouse.getY() / current.getScaleFactor();
            boolean found = false;
            for (DisplayElement element : this.mod.getDisplayElements()) {
                Dimension dimension = element.getDimensions();
                double displayXLoc = current.getScaledWidth_double() * element.getXloc();
                double displayYLoc = current.getScaledHeight_double() - current.getScaledHeight_double() * element.getYloc();
                if (!((double)clickX > displayXLoc) || !((double)clickX < displayXLoc + dimension.getWidth()) || !((double)clickY < displayYLoc) || !((double)clickY > displayYLoc - dimension.getHeight())) continue;
                this.currentElement = element;
                found = true;
                break;
            }
            if (!found) {
                this.currentElement = null;
            }
        }
        this.mouseDown = Mouse.isButtonDown((int)0);
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        Consumer<GuiButton> guiButtonConsumer = this.clicks.get(button);
        if (guiButtonConsumer != null) {
            guiButtonConsumer.accept(button);
        }
    }

    public void onGuiClosed() {
        super.onGuiClosed();
        this.mod.saveState();
    }
}

