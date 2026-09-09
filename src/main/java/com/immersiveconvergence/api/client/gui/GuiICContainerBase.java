package com.immersiveconvergence.api.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

public abstract class GuiICContainerBase extends GuiContainer {
    public GuiICContainerBase(Container inventorySlotsIn) { super(inventorySlotsIn); }

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
