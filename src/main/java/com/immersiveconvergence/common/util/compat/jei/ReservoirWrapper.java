package com.immersiveconvergence.common.util.compat.jei;

import flaxbeard.immersivepetroleum.api.crafting.PumpjackHandler;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class ReservoirWrapper implements IRecipeWrapper {
    public final PumpjackHandler.ReservoirType reservoir;
    public final int weight;
    public final int totalWeight;

    public ReservoirWrapper(PumpjackHandler.ReservoirType reservoir, int weight, int totalWeight) {
        this.reservoir = reservoir;
        this.weight = weight;
        this.totalWeight = totalWeight;
    }

    @Override public void getIngredients(@Nonnull IIngredients ingredients) {
        if (reservoir.getFluid() != null) { ingredients.setOutput(VanillaTypes.FLUID, new FluidStack(reservoir.getFluid(), reservoir.maxSize)); }
    }

    @Override public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        String key = "desc.immersivepetroleum.info.reservoir." + reservoir.name;
        String name = I18n.hasKey(key) ? I18n.format(key) : reservoir.name;
        minecraft.fontRenderer.drawString(name, 62, 8, 0x404040, false);
        minecraft.fontRenderer.drawString(reservoir.minSize + " - " + reservoir.maxSize + " mB", 62, 22, 0x8B8B8B, false);
        minecraft.fontRenderer.drawString(I18n.format("desc.immersiveengineering.info.chance") + " " + (totalWeight > 0 ? Math.round(100F * weight / totalWeight) : 0) + "%", 62, 34, 0x8B8B8B, false);
        if (reservoir.replenishRate > 0) { minecraft.fontRenderer.drawString("+" + reservoir.replenishRate + " mB/t", 62, 46, 0x8B8B8B, false); }
    }
}
