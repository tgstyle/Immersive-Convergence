package com.immersiveconvergence.common.util.compat.jei;

import flaxbeard.immersivepetroleum.api.crafting.DistillationRecipe;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.blocks.metal.BlockTypes_IPMetalMultiblock;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.List;

public class DistillationTowerCategory extends ICRecipeCategory<DistillationRecipe, DistillationTowerWrapper> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation("immersivepetroleum", "textures/gui/distillation.png");
    private final IDrawable tankOverlay;

    public DistillationTowerCategory(IGuiHelper helper) {
        super("distillationTower", "tile.immersivepetroleum.metal_multiblock.distillation_tower.name", helper.createDrawable(BACKGROUND, 56, 15, 116, 58), DistillationRecipe.class,
                new ItemStack(IPContent.blockMetalMultiblock, 1, BlockTypes_IPMetalMultiblock.DISTILLATION_TOWER_PARENT.getMeta()));
        tankOverlay = helper.drawableBuilder(BACKGROUND, 177, 31, 16, 47).addPadding(-2, 2, -2, 2).build();
    }

    @Override public void setRecipe(@Nonnull IRecipeLayout layout, @Nonnull DistillationTowerWrapper wrapper, @Nonnull IIngredients ingredients) {
        List<List<FluidStack>> inputs = ingredients.getInputs(VanillaTypes.FLUID);
        List<List<FluidStack>> outputs = ingredients.getOutputs(VanillaTypes.FLUID);
        IGuiFluidStackGroup fluids = layout.getFluidStacks();
        if (!inputs.isEmpty()) {
            fluids.init(0, true, 6, 6, 16, 47, amountOf(inputs.get(0)), false, tankOverlay);
            fluids.set(0, inputs.get(0));
        }
        int total = 0;
        for (List<FluidStack> output : outputs) { total += amountOf(output); }
        int bottom = 6 + 47;
        for (int i = 0; i < outputs.size(); i++) {
            int amount = amountOf(outputs.get(i));
            int height = i == outputs.size() - 1 ? bottom - 6 : Math.max(1, Math.round(47F * amount / total));
            bottom -= height;
            fluids.init(1 + i, false, 56, bottom, 16, height, amount, false, null);
            fluids.set(1 + i, outputs.get(i));
        }
        List<List<ItemStack>> items = ingredients.getOutputs(VanillaTypes.ITEM);
        IGuiItemStackGroup slots = layout.getItemStacks();
        for (int i = 0; i < items.size(); i++) {
            slots.init(i, false, 78 + 18 * i, 38);
            slots.set(i, items.get(i));
        }
        float[] chances = wrapper.recipe.chances;
        slots.addTooltipCallback((slot, input, stack, tooltip) -> {
            if (!input && chances != null && slot < chances.length) { tooltip.add(I18n.format("desc.immersiveengineering.info.chance") + " " + Math.round(chances[slot] * 100) + "%"); }
        });
    }

    private static int amountOf(List<FluidStack> stacks) {
        int amount = 0;
        for (FluidStack stack : stacks) { amount = Math.max(amount, stack.amount); }
        return amount;
    }

    @Override public void drawExtras(@Nonnull Minecraft minecraft) { tankOverlay.draw(minecraft, 56, 6); }

    @Override @Nonnull public IRecipeWrapper getRecipeWrapper(@Nonnull DistillationRecipe recipe) { return new DistillationTowerWrapper(recipe); }
}
