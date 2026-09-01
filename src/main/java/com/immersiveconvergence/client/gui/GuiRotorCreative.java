package com.immersiveconvergence.client.gui;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.network.TileSyncMessage;
import com.immersiveconvergence.common.blocks.tileentities.TileEntityRotorCreative;

import blusunrize.immersiveengineering.client.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.io.IOException;

public class GuiRotorCreative extends GuiScreen {
    private static final int PANEL_WIDTH = 96;
    private static final int PANEL_HEIGHT = 48;
    private static final int LAYOUT_HEIGHT = 112;
    private final TileEntityRotorCreative tile;
    private GuiTextField rpmField;
    private int guiLeft;
    private int guiTop;

    public GuiRotorCreative(TileEntityRotorCreative tile) { this.tile = tile; }

    private static boolean isValidInput(String string) {
        if (string.isEmpty() || string.equals("-")) { return true; }
        try {
            Integer.parseInt(string);
            return true;
        }
        catch (NumberFormatException e) { return false; }
    }

    private static int safeStringToInt(String text) {
        try { return Integer.parseInt(text); }
        catch (NumberFormatException e) { return 0; }
    }

    @Override public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        guiLeft = (width - PANEL_WIDTH) / 2;
        guiTop = (height - LAYOUT_HEIGHT) / 2;
        rpmField = new GuiTextField(0, this.fontRenderer, guiLeft + 36, guiTop + 30, 30, 9);
        rpmField.setEnableBackgroundDrawing(false);
        rpmField.setValidator(GuiRotorCreative::isValidInput);
        rpmField.setText(String.valueOf(tile.rpm));
        rpmField.setFocused(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, guiLeft + PANEL_WIDTH / 2 - 20, guiTop + 90, 40, 20, I18n.format("gui.immersiveconvergence.apply")));
    }

    @Override public void onGuiClosed() { Keyboard.enableRepeatEvents(false); }

    private void apply() {
        String text = rpmField.getText();
        int rpm = text.isEmpty() || text.equals("-") ? 0 : safeStringToInt(text);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("rpm", rpm);
        ImmersiveConvergence.packetHandler.sendToServer(new TileSyncMessage(tile, tag));
        Minecraft.getMinecraft().displayGuiScreen(null);
    }

    @Override protected void actionPerformed(@Nonnull GuiButton button) { if (button.id == 0) { apply(); } }

    @Override protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            apply();
            return;
        }
        if (rpmField.textboxKeyTyped(typedChar, keyCode)) { return; }
        super.keyTyped(typedChar, keyCode);
    }

    @Override protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        rpmField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override public void updateScreen() {
        super.updateScreen();
        rpmField.updateCursorCounter();
    }

    @Override public boolean doesGuiPauseGame() { return false; }

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        ClientUtils.bindTexture("immersiveconvergence:textures/gui/rotor.png");
        drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, PANEL_WIDTH, PANEL_HEIGHT);
        rpmField.drawTextBox();
        drawString(this.fontRenderer, I18n.format("gui.immersiveconvergence.rotor_creative.rpm"), guiLeft + 70, guiTop + 30, Color.WHITE.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
