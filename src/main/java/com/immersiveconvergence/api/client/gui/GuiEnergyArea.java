package com.immersiveconvergence.api.client.gui;

import blusunrize.immersiveengineering.api.client.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class GuiEnergyArea extends GuiInfoArea {
    public static final String KEY_ENERGY_STORED = "gui.immersiveconvergence.energy_stored";
    private final IEnergyStorage energy;

    public GuiEnergyArea(int xMin, int yMin, IEnergyStorage energy) {
        super(new Rect2i(xMin, yMin, 7, 46));
        this.energy = energy;
    }

    @Override protected void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip) { tooltip.add(TextUtils.applyFormat(Component.translatable(KEY_ENERGY_STORED, energy.getEnergyStored(), energy.getMaxEnergyStored()), ChatFormatting.GRAY)); }

    @Override public void draw(GuiGraphics graphics) {
        final int height = area.getHeight();
        int stored = (int) (height * (energy.getEnergyStored() / (float) energy.getMaxEnergyStored()));
        graphics.fillGradient(area.getX(), area.getY() + (height - stored), area.getX() + area.getWidth(), area.getY() + area.getHeight(), 0xffb51500, 0xff600b00);
    }
}
