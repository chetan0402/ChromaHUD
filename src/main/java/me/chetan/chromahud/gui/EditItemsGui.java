package me.chetan.chromahud.gui;

import me.chetan.chromahud.*;
import me.chetan.chromahud.api.ButtonConfig;
import me.chetan.chromahud.api.DisplayItem;
import me.chetan.chromahud.api.StringConfig;
import me.chetan.chromahud.api.TextConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

public class EditItemsGui
extends GuiScreen {
    private DisplayElement element;
    private HashMap<GuiButton, Consumer<GuiButton>> clicks = new HashMap();
    private HashMap<GuiButton, Consumer<GuiButton>> updates = new HashMap();
    private HashMap<String, GuiButton> nameMap = new HashMap();
    private DisplayItem modifying;
    private int tmpId;
    private ChromaHUD mod;
    private boolean mouseLock;

    public EditItemsGui(DisplayElement element, ChromaHUD mod) {
        this.element = element;
        this.mod = mod;
        this.mouseLock = Mouse.isButtonDown((int)0);
    }

    private int nextId() {
        return ++this.tmpId;
    }

    public void initGui() {
        this.reg("add", new GuiButton(this.nextId(), 2, 2, 100, 20, "Add Items"), guiButton -> Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new AddItemsGui(this.mod, this.element)), guiButton -> {});
        this.reg("Remove", new GuiButton(this.nextId(), 2, 23, 100, 20, "Remove Item"), guiButton -> {
            if (this.modifying != null) {
                this.element.removeDisplayItem(this.modifying.getOrdinal());
                if (this.modifying.getOrdinal() >= this.element.getDisplayItems().size()) {
                    this.modifying = null;
                }
            }
        }, guiButton -> {
            guiButton.enabled = this.modifying != null;
        });
        this.reg("Move Up", new GuiButton(this.nextId(), 2, 44, 100, 20, "Move Up"), guiButton -> {
            if (this.modifying != null) {
                int i = this.modifying.getOrdinal();
                Collections.swap(this.element.getDisplayItems(), this.modifying.getOrdinal(), this.modifying.getOrdinal() - 1);
                this.element.adjustOrdinal();
            }
        }, guiButton -> {
            guiButton.enabled = this.modifying != null && this.modifying.getOrdinal() > 0;
        });
        this.reg("Move Down", new GuiButton(this.nextId(), 2, 65, 100, 20, "Move Down"), guiButton -> {
            if (this.modifying != null) {
                int i = this.modifying.getOrdinal();
                Collections.swap(this.element.getDisplayItems(), this.modifying.getOrdinal(), this.modifying.getOrdinal() + 1);
                this.element.adjustOrdinal();
            }
        }, guiButton -> {
            guiButton.enabled = this.modifying != null && this.modifying.getOrdinal() < this.element.getDisplayItems().size() - 1;
        });
        this.reg("Back", new GuiButton(this.nextId(), 2, ResolutionUtil.current().getScaledHeight() - 22, 100, 20, "Back"), guiButton -> Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new DisplayElementConfig(this.element, this.mod)), guiButton -> {});
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        Consumer<GuiButton> guiButtonConsumer = this.clicks.get(button);
        if (guiButtonConsumer != null) {
            guiButtonConsumer.accept(button);
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

    public void updateScreen() {
        super.updateScreen();
        for (GuiButton guiButton : this.buttonList) {
            Consumer<GuiButton> guiButtonConsumer = this.updates.get(guiButton);
            if (guiButtonConsumer == null) continue;
            guiButtonConsumer.accept(guiButton);
        }
    }

    public void onGuiClosed() {
        this.mod.saveState();
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (this.modifying == null) {
            return;
        }
        List<TextConfig> textConfigs = ChromaHUDApi.getInstance().getTextConfigs(this.modifying.getType());
        if (textConfigs != null && !textConfigs.isEmpty()) {
            for (TextConfig config : textConfigs) {
                GuiTextField textField = config.getTextField();
                textField.textboxKeyTyped(typedChar, keyCode);
            }
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.modifying != null) {
            List<TextConfig> textConfigs;
            List<ButtonConfig> configs = ChromaHUDApi.getInstance().getButtonConfigs(this.modifying.getType());
            if (configs != null && !configs.isEmpty()) {
                for (ButtonConfig buttonConfig : configs) {
                    GuiButton button2 = buttonConfig.getButton();
                    if (!button2.mousePressed(this.mc, mouseX, mouseY)) continue;
                    buttonConfig.getAction().accept(button2, this.modifying);
                    return;
                }
            }
            if ((textConfigs = ChromaHUDApi.getInstance().getTextConfigs(this.modifying.getType())) != null && !textConfigs.isEmpty()) {
                for (TextConfig config3 : textConfigs) {
                    GuiTextField textField = config3.getTextField();
                    textField.mouseClicked(mouseX, mouseY, mouseButton);
                    if (!textField.isFocused()) continue;
                    return;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            DisplayItem item1 = null;
            ScaledResolution current = ResolutionUtil.current();
            int n = mouseX;
            int tmpY = mouseY;
            int xCenter = current.getScaledWidth() / 2;
            System.out.println(n);
            System.out.println(tmpY);
            if (n >= xCenter - 80 && n <= xCenter + 80) {
                int yPosition = 40;
                for (DisplayItem displayItem : this.element.getDisplayItems()) {
                    if (tmpY >= yPosition && tmpY <= yPosition + 20) {
                        item1 = displayItem;
                        break;
                    }
                    yPosition += 23;
                }
                System.out.println("adjusting mod");
            }
            for (GuiButton guiButton : this.buttonList) {
                if (!guiButton.isMouseOver()) continue;
                return;
            }
            this.modifying = item1;
            if (this.modifying != null) {
                ChromaHUDApi.getInstance().getTextConfigs(this.modifying.getType()).forEach(config -> config.getLoad().accept(config.getTextField(), this.modifying));
                ChromaHUDApi.getInstance().getButtonConfigs(this.modifying.getType()).forEach(button -> button.getLoad().accept(button.getButton(), this.modifying));
                ChromaHUDApi.getInstance().getStringConfigs(this.modifying.getType()).forEach(button -> button.getLoad().accept(this.modifying));
            }
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        List<ButtonConfig> configs;
        super.mouseReleased(mouseX, mouseY, state);
        if (this.modifying != null && (configs = ChromaHUDApi.getInstance().getButtonConfigs(this.modifying.getType())) != null && !configs.isEmpty()) {
            for (ButtonConfig config : configs) {
                GuiButton button = config.getButton();
                button.mouseReleased(mouseX, mouseY);
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution current = ResolutionUtil.current();
        EditItemsGui.drawRect((int)0, (int)0, (int)current.getScaledWidth(), (int)current.getScaledHeight(), (int)new Color(0, 0, 0, 150).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
        ElementRenderer.startDrawing(this.element);
        this.element.renderEditView();
        ElementRenderer.endDrawing(this.element);
        int xPosition = ResolutionUtil.current().getScaledWidth() / 2 - 80;
        int yPosition = 40;
        int width = 160;
        int height = 20;
        Color defaultColor = new Color(255, 255, 255, 100);
        Color otherColor = new Color(255, 255, 255, 150);
        for (DisplayItem displayItem : this.element.getDisplayItems()) {
            FontRenderer fontRenderer = this.mc.fontRendererObj;
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            boolean hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
            EditItemsGui.drawRect((int)xPosition, (int)yPosition, (int)(xPosition + width), (int)(yPosition + height), (int)(this.modifying != null && this.modifying.getOrdinal() == displayItem.getOrdinal() || hovered ? otherColor.getRGB() : defaultColor.getRGB()));
            int j = Color.RED.getRGB();
            String displayString = ChromaHUDApi.getInstance().getName(displayItem.getType());
            fontRenderer.drawString(displayString, (float)(xPosition + width / 2 - fontRenderer.getStringWidth(displayString) / 2), (float)(yPosition + (height - 8) / 2), j, false);
            yPosition += 23;
        }
        if (this.modifying != null) {
            List<TextConfig> list;
            List<ButtonConfig> configs = ChromaHUDApi.getInstance().getButtonConfigs(this.modifying.getType());
            xPosition = 3;
            yPosition = 89;
            if (configs != null && !configs.isEmpty()) {
                for (ButtonConfig buttonConfig : configs) {
                    GuiButton button = buttonConfig.getButton();
                    button.xPosition = xPosition;
                    button.yPosition = yPosition;
                    button.drawButton(this.mc, mouseX, mouseY);
                    yPosition += 23;
                }
            }
            if ((list = ChromaHUDApi.getInstance().getTextConfigs(this.modifying.getType())) != null && !list.isEmpty()) {
                for (TextConfig config : list) {
                    GuiTextField textField = config.getTextField();
                    textField.xPosition = xPosition;
                    textField.yPosition = yPosition;
                    textField.drawTextBox();
                    yPosition += 23;
                    config.getAction().accept(textField, this.modifying);
                }
            }
            int n = (int)(ResolutionUtil.current().getScaledWidth_double() / 2.0 - 90.0);
            List<StringConfig> stringConfigs = ChromaHUDApi.getInstance().getStringConfigs(this.modifying.getType());
            if (stringConfigs != null && !stringConfigs.isEmpty()) {
                for (StringConfig config : stringConfigs) {
                    config.getDraw().accept(this.modifying);
                    String draw = config.getString();
                    ArrayList<String> lines = new ArrayList<String>();
                    String[] split = draw.split(" ");
                    FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
                    StringBuilder currentLine = new StringBuilder();
                    for (String s : split) {
                        if (!s.contains("\n")) {
                            if (fontRendererObj.getStringWidth(" " + currentLine.toString()) + fontRendererObj.getStringWidth(s) + xPosition < n - 10) {
                                currentLine.append(" ").append(s);
                                continue;
                            }
                            lines.add(currentLine.toString());
                            currentLine = new StringBuilder();
                            currentLine.append(s);
                            continue;
                        }
                        String[] split1 = s.split("\n");
                        Iterator<String> iterator = Arrays.asList(split1).iterator();
                        while (iterator.hasNext()) {
                            currentLine.append(" ").append(iterator.next());
                            if (!iterator.hasNext()) continue;
                            lines.add(currentLine.toString());
                            currentLine = new StringBuilder();
                        }
                    }
                    lines.add(currentLine.toString());
                    yPosition += 10;
                    for (String string : lines) {
                        Minecraft.getMinecraft().fontRendererObj.drawString(string, xPosition, yPosition, Color.RED.getRGB());
                        yPosition += 10;
                    }
                }
            }
        }
    }
}

