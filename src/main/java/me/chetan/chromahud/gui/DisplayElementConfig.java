package me.chetan.chromahud.gui;

import me.chetan.chromahud.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;

public class DisplayElementConfig
extends GuiScreen {
    private DisplayElement element;
    private HashMap<GuiButton, Consumer<GuiButton>> clicks = new HashMap();
    private HashMap<GuiButton, Consumer<GuiButton>> updates = new HashMap();
    private HashMap<String, GuiButton> nameMap = new HashMap();
    private int ids;
    private int lastX;
    private int lastY;
    private DynamicTexture texture;
    private DynamicTexture texture2;
    private int hue = -1;
    private int saturation = -1;
    private int brightness = 5;
    private ChromaHUD mod;
    private int lastWidth = 0;
    private int lastHeight = 0;
    private boolean mouseLock;

    public DisplayElementConfig(DisplayElement element, ChromaHUD mod) {
        if (element == null) {
            throw new NullPointerException("Display element is null!");
        }
        this.mod = mod;
        this.element = element;
        this.regenImage();
        this.mouseLock = Mouse.isButtonDown((int)0);
    }

    public void regenImage() {
        int y;
        int dim = 256;
        BufferedImage image = new BufferedImage(dim, dim, 1);
        for (int x = 0; x < dim; ++x) {
            for (y = 0; y < dim; ++y) {
                image.setRGB(x, y, Color.HSBtoRGB((float)x / 256.0f, 1.0f - (float)y / 256.0f, 1.0f));
            }
        }
        this.texture = new DynamicTexture(image);
        if (this.hue != -1 && this.saturation != -1) {
            BufferedImage image1 = new BufferedImage(1, dim, 1);
            for (y = 0; y < dim; ++y) {
                float hue = (float)this.hue / 256.0f;
                float saturation = (float)this.saturation / 256.0f;
                image1.setRGB(0, y, Color.HSBtoRGB(hue, saturation, 1.0f - (float)y / 256.0f));
            }
            this.texture2 = new DynamicTexture(image1);
        }
    }

    private void reg(String name, GuiButton button, Consumer<GuiButton> consumer) {
        this.reg(name, button, consumer, button1 -> {});
    }

    private void reg(String name, GuiButton button, Consumer<GuiButton> consumer, Consumer<GuiButton> tick) {
        this.buttonList.add(button);
        this.clicks.put(button, consumer);
        this.updates.put(button, tick);
        this.nameMap.put(name, button);
    }

    private int nextId() {
        return ++this.ids;
    }

    public void initGui() {
        super.initGui();
    }

    private void repack() {
        this.buttonList.clear();
        this.clicks.clear();
        this.updates.clear();
        this.nameMap.clear();
        ScaledResolution current = ResolutionUtil.current();
        int start_y = Math.max((int)(current.getScaledHeight_double() * 0.1) - 20, 5);
        int posX = (int)(current.getScaledWidth_double() * 0.5) - 100;
        this.reg("pos", new GuiButton(this.nextId(), posX, start_y, "Change Position"), button -> Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new MoveElementGui(this.mod, this.element)));
        this.reg("items", new GuiButton(this.nextId(), posX, start_y + 22, "Change Items"), button -> Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new EditItemsGui(this.element, this.mod)));
        this.reg("Highlight", new GuiButton(this.nextId(), posX, start_y + 44, "-"), button -> this.element.setHighlighted(!this.element.isHighlighted()), button -> {
            button.displayString = EnumChatFormatting.YELLOW.toString() + "Highlighted: " + (this.element.isHighlighted() ? EnumChatFormatting.GREEN + "Yes" : EnumChatFormatting.RED.toString() + "No");
        });
        this.reg("shadow", new GuiButton(this.nextId(), posX, start_y + 66, "-"), button -> this.element.setShadow(!this.element.isShadow()), button -> {
            button.displayString = EnumChatFormatting.YELLOW.toString() + "Shadow: " + (this.element.isShadow() ? EnumChatFormatting.GREEN + "Yes" : EnumChatFormatting.RED.toString() + "No");
        });
        this.reg("Scale Slider", (GuiButton)new GuiSlider(this.nextId(), posX, start_y + 88, 200, 20, "Scale: ", "", 50.0, 200.0, this.element.getScale() * 100.0, false, true), button -> {}, button -> {
            this.element.setScale(((GuiSlider)button).getValue() / 100.0);
            button.displayString = EnumChatFormatting.YELLOW + "Scale: " + ((GuiSlider)button).getValueInt() + "%";
            System.out.println(this.element.getScale());
        });
        this.reg("color", new GuiButton(this.nextId(), posX, start_y + 110, "-"), button -> {
            if (this.element.isChroma()) {
                this.element.setChroma(false);
                this.element.setRgb(true);
            } else if (this.element.isRGB()) {
                this.element.setRgb(false);
                this.element.setColorPallet(true);
            } else {
                this.element.setColorPallet(false);
                this.element.setChroma(true);
            }
        }, button -> {
            String type = "Error";
            if (this.element.isRGB()) {
                type = "RGB";
            }
            if (this.element.isColorPallet()) {
                type = "Color Pallet";
            }
            if (this.element.isChroma()) {
                type = "Chroma";
            }
            button.displayString = EnumChatFormatting.YELLOW + "Color mode: " + EnumChatFormatting.GREEN.toString() + type;
        });
        this.reg("chromaMode", new GuiButton(this.nextId(), posX, start_y + 132, "-"), button -> this.element.setStaticChroma(!this.element.isStaticChroma()), button -> {
            if (!this.element.isChroma()) {
                button.enabled = false;
                button.visible = false;
            } else {
                button.visible = true;
                button.enabled = true;
                button.displayString = EnumChatFormatting.YELLOW + "Chroma mode: " + (this.element.isStaticChroma() ? EnumChatFormatting.GREEN + "Static" : EnumChatFormatting.GREEN + "Wave");
            }
        });
        this.reg("redSlider", (GuiButton)new GuiSlider(this.nextId(), posX, start_y + 132, 200, 20, "Red: ", "", 0.0, 255.0, (double)this.element.getData().optInt("red"), false, true), button -> {}, button -> {
            if (!this.element.isRGB()) {
                button.enabled = false;
                button.visible = false;
            } else {
                button.visible = true;
                button.enabled = true;
                this.element.getData().put("red", ((GuiSlider)button).getValueInt());
                button.displayString = EnumChatFormatting.YELLOW + "Red: " + this.element.getData().optInt("red");
            }
        });
        this.reg("blueSlider", (GuiButton)new GuiSlider(this.nextId(), posX, start_y + 176, 200, 20, "Blue: ", "", 0.0, 255.0, (double)this.element.getData().optInt("blue"), false, true), button -> {}, button -> {
            if (!this.element.isRGB()) {
                button.enabled = false;
                button.visible = false;
            } else {
                button.visible = true;
                button.enabled = true;
                this.element.getData().put("blue", ((GuiSlider)button).getValueInt());
                button.displayString = EnumChatFormatting.YELLOW + "Blue: " + this.element.getData().optInt("blue");
            }
        });
        this.reg("greenSlider", (GuiButton)new GuiSlider(this.nextId(), posX, start_y + 154, 200, 20, "Green: ", "", 0.0, 255.0, (double)this.element.getData().optInt("green"), false, true), button -> {}, button -> {
            if (!this.element.isRGB()) {
                button.enabled = false;
                button.visible = false;
            } else {
                button.visible = true;
                button.enabled = true;
                this.element.getData().put("green", ((GuiSlider)button).getValueInt());
                button.displayString = EnumChatFormatting.YELLOW + "Green: " + this.element.getData().optInt("green");
            }
        });
        this.reg("Back", new GuiButton(this.nextId(), 2, ResolutionUtil.current().getScaledHeight() - 22, 100, 20, "Back"), guiButton -> Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new GeneralConfigGui(this.mod)), guiButton -> {});
        this.reg("Delete", new GuiButton(this.nextId(), 2, ResolutionUtil.current().getScaledHeight() - 44, 100, 20, "Delete"), guiButton -> {
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new GeneralConfigGui(this.mod));
            ChromaHUDApi.getInstance().getElements().remove(this.element);
            System.out.println("Removed");
        }, guiButton -> {});
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        Consumer<GuiButton> guiButtonConsumer = this.clicks.get(button);
        if (guiButtonConsumer != null) {
            guiButtonConsumer.accept(button);
        }
    }

    public void updateScreen() {
        ScaledResolution current = ResolutionUtil.current();
        if (current.getScaledWidth() != this.lastWidth || current.getScaledHeight() != this.lastHeight) {
            this.repack();
            this.lastWidth = current.getScaledWidth();
            this.lastHeight = current.getScaledHeight();
        }
        if (this.element.isRGB()) {
            this.element.recalculateColor();
        }
        for (GuiButton guiButton : this.buttonList) {
            Consumer<GuiButton> guiButtonConsumer = this.updates.get(guiButton);
            if (guiButtonConsumer == null) continue;
            guiButtonConsumer.accept(guiButton);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.apply(mouseX, mouseY);
    }

    public float scale() {
        return (float)this.availableSpace() / 285.0f;
    }

    private void apply(int mouseX, int mouseY) {
        if (this.mouseLock) {
            return;
        }
        if (!Mouse.isButtonDown((int)0)) {
            return;
        }
        if (!this.element.isColorPallet()) {
            return;
        }
        ScaledResolution current = ResolutionUtil.current();
        float scale = this.scale();
        int left = this.posX(1);
        int right = this.posX(2);
        int top = this.posY(1);
        int bottom = this.posY(3);
        float x = 0.0f;
        float y = 0.0f;
        if (mouseX > left && mouseX < right && mouseY > top && mouseY < bottom) {
            x = mouseX - left;
            y = mouseY - top;
            x /= scale;
            if ((y /= scale) > 0.0f && y <= 256.0f) {
                if (x < 256.0f && x > 0.0f) {
                    this.hue = (int)x;
                    this.saturation = (int)(256.0f - y);
                    System.out.println("Update: " + this.hue + " sat: " + this.saturation);
                    this.regenImage();
                    this.lastX = mouseX;
                    this.lastY = mouseY;
                } else if (x > 271.0f && x < 286.0f) {
                    System.out.println(y);
                    this.brightness = (int)y;
                    this.regenImage();
                }
            }
        }
    }

    public void onGuiClosed() {
        super.onGuiClosed();
        this.mod.saveState();
    }

    private void drawCircle(int x, int y) {
        GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        Gui.drawRect((int)(x - 2), (int)(y + 12), (int)(x + 2), (int)(y + 3), (int)Color.BLACK.getRGB());
        Gui.drawRect((int)(x - 2), (int)(y - 3), (int)(x + 2), (int)(y - 12), (int)Color.BLACK.getRGB());
        Gui.drawRect((int)(x + 12), (int)(y - 2), (int)(x + 3), (int)(y + 2), (int)Color.BLACK.getRGB());
        Gui.drawRect((int)(x - 12), (int)(y - 2), (int)(x - 3), (int)(y + 2), (int)Color.BLACK.getRGB());
        Gui.drawRect((int)(this.posX(2) + 5), (int)((int)((float)this.startY() + (float)(this.brightness - 2) * this.scale())), (int)(this.posX(2) + 15), (int)((int)((float)this.startY() + (float)(this.brightness + 2) * this.scale())), (int)Color.BLACK.getRGB());
        this.element.setColor(Color.HSBtoRGB((float)this.hue / 255.0f, (float)this.saturation / 255.0f, 1.0f - (float)this.brightness / 255.0f));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution current = ResolutionUtil.current();
        this.mouseLock = this.mouseLock && Mouse.isButtonDown((int)0);
        DisplayElementConfig.drawRect((int)0, (int)0, (int)current.getScaledWidth(), (int)current.getScaledHeight(), (int)new Color(0, 0, 0, 150).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
        ElementRenderer.startDrawing(this.element);
        this.element.renderEditView();
        ElementRenderer.endDrawing(this.element);
        int left = this.posX(1);
        int top = this.posY(2);
        int right = this.posX(2);
        int size = right - left;
        int bottom = this.posY(3);
        if (this.element.isRGB()) {
            int start_y = Math.max((int)(current.getScaledHeight_double() * 0.1) - 20, 5) + 176 + 15;
            int left1 = current.getScaledWidth() / 2 - 100;
            int right1 = current.getScaledWidth() / 2 + 100;
            Gui.drawRect((int)left1, (int)start_y, (int)right1, (int)(right1 - left1 + 200), (int)this.element.getColor());
        }
        if (!this.element.isColorPallet()) {
            return;
        }
        this.apply(mouseX, mouseY);
        GlStateManager.bindTexture((int)this.texture.getGlTextureId());
        GlStateManager.enableTexture2D();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)left, (float)top, (float)0.0f);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.scale((float)((float)size / 285.0f), (float)((float)size / 285.0f), (float)0.0f);
        this.drawTexturedModalRect(0, 0, 0, 0, 256, 256);
        if (this.texture2 != null) {
            GlStateManager.bindTexture((int)this.texture2.getGlTextureId());
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GL11.glTranslatef((float)271.0f, (float)0.0f, (float)0.0f);
            this.drawTexturedModalRect(0, 0, 0, 0, 15, 256);
        }
        GlStateManager.scale((float)(285.0f / (float)size), (float)(285.0f / (float)size), (float)0.0f);
        GL11.glPopMatrix();
        if (this.lastX != 0 && this.lastY != 0) {
            this.drawCircle(this.lastX, this.lastY);
        }
    }

    public int availableSpace() {
        ScaledResolution current = ResolutionUtil.current();
        int yMin = current.getScaledHeight() - 15 - this.startY();
        if (yMin + 20 > current.getScaledWidth()) {
            return yMin - 50;
        }
        return yMin;
    }

    private int startY() {
        ScaledResolution current = ResolutionUtil.current();
        return (int)(Math.max(current.getScaledHeight_double() * 0.1 - 20.0, 5.0) + 132.0);
    }

    private int posX(int vertex) {
        int i = this.availableSpace();
        ScaledResolution current = ResolutionUtil.current();
        switch (vertex) {
            case 1: 
            case 3: {
                return (current.getScaledWidth() - i + 30) / 2;
            }
            case 2: 
            case 4: {
                return (current.getScaledWidth() + i + 30) / 2;
            }
        }
        throw new IllegalArgumentException("Vertex not found " + vertex);
    }

    private int posY(int vertex) {
        switch (vertex) {
            case 1: 
            case 2: {
                return this.startY();
            }
            case 3: 
            case 4: {
                return this.startY() + this.availableSpace();
            }
        }
        throw new IllegalArgumentException("Vertex not found " + vertex);
    }
}

