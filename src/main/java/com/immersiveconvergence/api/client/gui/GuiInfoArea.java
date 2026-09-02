package com.immersiveconvergence.api.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import java.util.List;

@SuppressWarnings({"unused", "RedundantSuppression"}) public abstract class GuiInfoArea {
    protected final Rect2i area;

    protected GuiInfoArea(Rect2i area) { this.area = area; }

    public final void fillTooltip(int mouseX, int mouseY, List<Component> tooltip) {
        if (this.area.contains(mouseX, mouseY)) { this.fillTooltipOverArea(mouseX, mouseY, tooltip); }
    }

    protected abstract void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip);

    public abstract void draw(GuiGraphics graphics);
}
