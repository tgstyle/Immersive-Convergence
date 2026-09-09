package com.immersiveconvergence.api.client.gui;

import com.immersiveconvergence.api.ICLib;
import com.immersiveconvergence.api.client.ICClientUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

@SuppressWarnings("unused")
public class ICGuiButton extends GuiButton {
    protected final String texture;
    protected final int texU;
    protected final int texV;
    private int[] hoverOffset;

    public ICGuiButton(int buttonId, int x, int y, int w, int h, String name, String texture, int u, int v) {
        super(buttonId, x, y, w, h, name);
        this.texture = texture;
        this.texU = u;
        this.texV = v;
    }

    public ICGuiButton setHoverOffset(int x, int y) {
        this.hoverOffset = new int[]{x, y};
        return this;
    }

    public boolean canClick(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

    @Override public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) { return canClick(mc, mouseX, mouseY); }

    @Override public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) { return; }
        ICClientUtils.bindTexture(texture);
        FontRenderer fontRenderer = mc.fontRenderer;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.hovered = canClick(mc, mouseX, mouseY);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
        if (hoverOffset != null && this.hovered) { this.drawTexturedModalRect(x, y, texU + hoverOffset[0], texV + hoverOffset[1], width, height); }
        else { this.drawTexturedModalRect(x, y, texU, texV, width, height); }
        this.mouseDragged(mc, mouseX, mouseY);
        if (displayString == null || displayString.isEmpty()) { return; }
        int colour = 0xE0E0E0;
        if (!this.enabled) { colour = 0xA0A0A0; }
        else if (this.hovered) { colour = ICLib.COLOUR_I_ImmersiveOrange; }
        this.drawCenteredString(fontRenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, colour);
    }
}
