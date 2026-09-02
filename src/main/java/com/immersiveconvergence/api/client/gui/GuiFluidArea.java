package com.immersiveconvergence.api.client.gui;

import com.immersiveconvergence.api.client.FluidRender;
import com.immersiveconvergence.api.client.RenderTypes;

import blusunrize.immersiveengineering.api.client.TextUtils;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class GuiFluidArea extends GuiInfoArea {
    public static final String KEY_EMPTY = "gui.immersiveconvergence.empty";
    public static final String KEY_HOLD_SHIFT = "desc.immersiveconvergence.info.holdShiftForInfo";
    public static final String KEY_FLUID_REGISTRY = "gui.immersiveconvergence.fluid_registry";
    public static final String KEY_FLUID_DENSITY = "gui.immersiveconvergence.fluid_density";
    public static final String KEY_FLUID_TEMPERATURE = "gui.immersiveconvergence.fluid_temperature";
    public static final String KEY_FLUID_VISCOSITY = "gui.immersiveconvergence.fluid_viscosity";
    public static final String KEY_FLUID_NBT = "gui.immersiveconvergence.fluid_nbt";
    public static final String KEY_FLUID_CAPACITY = "gui.immersiveconvergence.fluid_capacity";
    public static final String KEY_FLUID_AMOUNT = "gui.immersiveconvergence.fluid_amount";
    private final IFluidTank tank;
    private final Rect2i area;
    private final int overlayUMin;
    private final int overlayVMin;
    private final int overlayWidth;
    private final int overlayHeight;
    private final ResourceLocation overlayTexture;

    public GuiFluidArea(IFluidTank tank, Rect2i area, int overlayUMin, int overlayVMin, int overlayWidth, int overlayHeight, ResourceLocation overlayTexture) {
        super(area);
        this.tank = tank;
        this.area = area;
        this.overlayUMin = overlayUMin;
        this.overlayVMin = overlayVMin;
        this.overlayWidth = overlayWidth;
        this.overlayHeight = overlayHeight;
        this.overlayTexture = overlayTexture;
    }

    @Override public void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip) {
        Objects.requireNonNull(tooltip);
        fillTooltip(tank.getFluid(), tank.getCapacity(), tooltip::add);
    }

    public static void fillTooltip(FluidStack fluid, int tankCapacity, Consumer<Component> tooltip) {
        if (!fluid.isEmpty()) { tooltip.accept(fluid.getDisplayName().copy().withStyle(fluid.getFluid().getFluidType().getRarity(fluid).getStyleModifier())); }
        else { tooltip.accept(Component.translatable(KEY_EMPTY)); }
        if (Minecraft.getInstance().options.advancedItemTooltips && !fluid.isEmpty()) {
            if (!Screen.hasShiftDown()) { tooltip.accept(Component.translatable(KEY_HOLD_SHIFT)); }
            else {
                tooltip.accept(TextUtils.applyFormat(Component.translatable(KEY_FLUID_REGISTRY, ForgeRegistries.FLUIDS.getKey(fluid.getFluid())), ChatFormatting.DARK_GRAY));
                tooltip.accept(TextUtils.applyFormat(Component.translatable(KEY_FLUID_DENSITY, fluid.getFluid().getFluidType().getDensity(fluid)), ChatFormatting.DARK_GRAY));
                tooltip.accept(TextUtils.applyFormat(Component.translatable(KEY_FLUID_TEMPERATURE, fluid.getFluid().getFluidType().getTemperature(fluid)), ChatFormatting.DARK_GRAY));
                tooltip.accept(TextUtils.applyFormat(Component.translatable(KEY_FLUID_VISCOSITY, fluid.getFluid().getFluidType().getViscosity(fluid)), ChatFormatting.DARK_GRAY));
                tooltip.accept(TextUtils.applyFormat(Component.translatable(KEY_FLUID_NBT, fluid.getTag()), ChatFormatting.DARK_GRAY));
            }
        }
        if (tankCapacity > 0) { tooltip.accept(TextUtils.applyFormat(Component.translatable(KEY_FLUID_CAPACITY, fluid.getAmount(), tankCapacity), ChatFormatting.GRAY)); }
        else if (tankCapacity == 0) { tooltip.accept(TextUtils.applyFormat(Component.translatable(KEY_FLUID_AMOUNT, fluid.getAmount()), ChatFormatting.GRAY)); }
    }

    @Override public void draw(GuiGraphics graphics) {
        FluidStack fluid = tank.getFluid();
        float capacity = (float) tank.getCapacity();
        graphics.pose().pushPose();
        MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        if (!fluid.isEmpty()) {
            int fluidHeight = (int) ((float) area.getHeight() * ((float) fluid.getAmount() / capacity));
            FluidRender.drawRepeatedFluidSpriteGui(buffer, graphics.pose(), fluid, area.getX(), area.getY() + area.getHeight() - fluidHeight, area.getWidth(), fluidHeight);
        }
        int xOff = (area.getWidth() - overlayWidth) / 2;
        int yOff = (area.getHeight() - overlayHeight) / 2;
        RenderType renderType = RenderTypes.getGui(overlayTexture);
        FluidRender.drawTexturedRect(buffer.getBuffer(renderType), graphics.pose(), area.getX() + xOff, area.getY() + yOff, overlayWidth, overlayHeight, 256.0F, overlayUMin, overlayUMin + overlayWidth, overlayVMin, overlayVMin + overlayHeight);
        buffer.endBatch();
        graphics.pose().popPose();
    }
}
