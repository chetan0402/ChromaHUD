package me.chetan.chromahud.gui;

import me.chetan.chromahud.*;
import me.chetan.chromahud.api.ChromaHUDDescription;
import me.chetan.chromahud.api.ChromaHUDParser;
import me.chetan.chromahud.api.DisplayItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AddItemsGui
extends GuiScreen {
    private ChromaHUD mod;
    private DisplayElement element;
    private HashMap<GuiButton, Consumer<GuiButton>> clicks = new HashMap();
    private HashMap<GuiButton, Consumer<GuiButton>> updates = new HashMap();
    private HashMap<String, GuiButton> nameMap = new HashMap();
    private int tmpId = 0;
    private boolean adding = true;
    private int offset = 0;
    private List<DisplayElement> all = new ArrayList<DisplayElement>();
    private DisplayElement target;
    private boolean mouseLock;

    public AddItemsGui(ChromaHUD mod, DisplayElement element) {
        this.mod = mod;
        this.element = element;
        for (ChromaHUDParser chromaHUDParser : ChromaHUDApi.getInstance().getParsers()) {
            for (String s : chromaHUDParser.getNames().keySet()) {
                DisplayItem item = chromaHUDParser.parse(s, 0, new JsonHolder().put("type", s));
                DisplayElement blank = DisplayElement.blank();
                blank.getDisplayItems().add(item);
                blank.adjustOrdinal();
                this.all.add(blank);
            }
        }
        for (DisplayElement displayElement : this.all) {
            displayElement.drawForConfig();
        }
        this.target = DisplayElement.blank();
        this.target.setColor(Color.GREEN.getRGB());
        this.mouseLock = Mouse.isButtonDown((int)0);
    }

    private int nextId() {
        return ++this.tmpId;
    }

    public void initGui() {
        super.initGui();
        this.reg("Add", new GuiButton(this.nextId(), 2, 2, 100, 20, "Add"), guiButton -> {
            this.adding = true;
            this.offset = 0;
        }, guiButton -> {
            guiButton.enabled = !this.adding;
        });
        this.reg("Explore", new GuiButton(this.nextId(), 2, 23, 100, 20, "Explore"), guiButton -> {
            this.adding = false;
            this.offset = 0;
        }, guiButton -> {
            guiButton.enabled = this.adding;
        });
        this.reg("Down", new GuiButton(this.nextId(), 2, 65, 100, 20, "Scroll Down"), guiButton -> this.offset += 50, guiButton -> {});
        this.reg("Up", new GuiButton(this.nextId(), 2, 44, 100, 20, "Scroll Up"), guiButton -> this.offset -= 50, guiButton -> {});
        this.reg("Back", new GuiButton(this.nextId(), 2, ResolutionUtil.current().getScaledHeight() - 22, 100, 20, "Back"), guiButton -> Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new EditItemsGui(this.element, this.mod)), guiButton -> {});
    }

    private void reg(String name, GuiButton button, Consumer<GuiButton> consumer, Consumer<GuiButton> tick) {
        this.buttonList.add(button);
        this.clicks.put(button, consumer);
        this.updates.put(button, tick);
        this.nameMap.put(name, button);
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        Consumer<GuiButton> guiButtonConsumer = this.clicks.get(button);
        if (guiButtonConsumer != null) {
            guiButtonConsumer.accept(button);
        }
    }

    public void updateScreen() {
        super.updateScreen();
        for (GuiButton guiButton : this.buttonList) {
            Consumer<GuiButton> guiButtonConsumer = this.updates.get(guiButton);
            if (guiButtonConsumer == null) continue;
            guiButtonConsumer.accept(guiButton);
        }
    }

    public void drawDefaultBackground() {
        super.drawDefaultBackground();
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();
        if (i < 0) {
            this.offset += 10;
        } else if (i > 0) {
            this.offset -= 10;
        }
    }

    public void onGuiClosed() {
        this.mod.saveState();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.mouseLock = this.mouseLock && Mouse.isButtonDown((int)0);
        ScaledResolution current = ResolutionUtil.current();
        AddItemsGui.drawRect((int)0, (int)0, (int)current.getScaledWidth(), (int)current.getScaledHeight(), (int)new Color(0, 0, 0, 150).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
        ElementRenderer.startDrawing(this.target);
        if (this.adding) {
            Color defaultColor = new Color(255, 255, 255, 100);
            int cursorY = 50 + this.offset;
            this.drawCenteredString(this.mc.fontRendererObj, "Click Explore to see examples!", current.getScaledWidth() / 2, cursorY - 30, Color.RED.getRGB());
            List<ChromaHUDParser> parsers = ChromaHUDApi.getInstance().getParsers();
            for (ChromaHUDParser parser : parsers) {
                Map<String, String> names = parser.getNames();
                for (String s : names.keySet()) {
                    int i1;
                    String text1 = names.get(s) + "";
                    AddItemsGui.drawRect((int)(current.getScaledWidth() / 2 - 80), (int)cursorY, (int)(current.getScaledWidth() / 2 + 80), (int)(cursorY + 20), (int)defaultColor.getRGB());
                    int j = Color.RED.getRGB();
                    int width = 160;
                    int height = 20;
                    this.mc.fontRendererObj.drawString(text1, (float)(current.getScaledWidth() / 2 - 80 + width / 2 - this.mc.fontRendererObj.getStringWidth(text1) / 2), (float)(cursorY + (height - 8) / 2), j, false);
                    int i = ResolutionUtil.current().getScaledHeight() - Mouse.getY() / current.getScaleFactor();
                    if (Mouse.isButtonDown((int)0) && !this.mouseLock && i >= cursorY && i <= cursorY + 23 && (i1 = Mouse.getX() / current.getScaleFactor()) >= current.getScaledWidth() / 2 - 80 && i1 <= current.getScaledWidth() / 2 + 80) {
                        DisplayItem item = ChromaHUDApi.getInstance().parse(s, 0, new JsonHolder().put("type", s));
                        this.element.getDisplayItems().add(item);
                        this.element.adjustOrdinal();
                        Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new EditItemsGui(this.element, this.mod));
                    }
                    cursorY += 23;
                }
            }
        } else {
            int cursorY = 50 + this.offset;
            List<ChromaHUDParser> parsers = ChromaHUDApi.getInstance().getParsers();
            for (ChromaHUDParser parser : parsers) {
                ChromaHUDDescription description = parser.description();
                String text = "Items in " + description.getName() + ".";
                this.mc.fontRendererObj.drawString(text, (float)((current.getScaledWidth() - this.mc.fontRendererObj.getStringWidth(text)) / 2), (float)cursorY, Color.RED.getRGB(), true);
                cursorY += 30;
                Map<String, String> names = parser.getNames();
                for (String s : names.keySet()) {
                    String text1 = names.get(s) + ": ";
                    DisplayElement displayElement = this.find(s);
                    if (displayElement == null) {
                        String text2 = "ERROR LOCATING DISPLAY ELEMENT " + s;
                        this.mc.fontRendererObj.drawString(text2, (float)((current.getScaledWidth() - this.mc.fontRendererObj.getStringWidth(text2)) / 2), (float)cursorY, Color.RED.getRGB(), true);
                        cursorY += 15;
                        continue;
                    }
                    Dimension dimensions = displayElement.getDimensions();
                    int stringWidth = this.mc.fontRendererObj.getStringWidth(text1);
                    double totalWidth = dimensions.getWidth() + (double)stringWidth;
                    double left = (current.getScaledWidth_double() - totalWidth) / 2.0;
                    double startDraw = left + (double)stringWidth;
                    displayElement.setXloc(startDraw / current.getScaledWidth_double());
                    displayElement.setYloc((double)cursorY / current.getScaledHeight_double());
                    displayElement.drawForConfig();
                    this.mc.fontRendererObj.drawString(text1, (float)(((double)current.getScaledWidth() - totalWidth) / 2.0), (float)cursorY, Color.RED.getRGB(), true);
                    cursorY += dimensions.height + 5;
                }
            }
        }
        ElementRenderer.endDrawing(this.target);
    }

    private DisplayElement find(String key) {
        for (DisplayElement displayElement : this.all) {
            if (!displayElement.getDisplayItems().get(0).getType().equalsIgnoreCase(key)) continue;
            return displayElement;
        }
        return null;
    }
}

