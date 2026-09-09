package com.immersiveconvergence.api.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

@SuppressWarnings("unused")
public interface ICJEIRecipe {
    default boolean listInJEI() { return true; }

    List<ItemStack> getJEITotalItemInputs();

    List<ItemStack> getJEITotalItemOutputs();

    List<FluidStack> getJEITotalFluidInputs();

    List<FluidStack> getJEITotalFluidOutputs();
}
