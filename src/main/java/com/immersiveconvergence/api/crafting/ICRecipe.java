package com.immersiveconvergence.api.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

@SuppressWarnings("unused")
public interface ICRecipe {
    List<ICIngredient> getItemInputs();

    default boolean shouldCheckItemAvailability() { return true; }

    List<FluidStack> getFluidInputs();

    NonNullList<ItemStack> getItemOutputs();

    default NonNullList<ItemStack> getActualItemOutputs(TileEntity tile) { return getItemOutputs(); }

    List<FluidStack> getFluidOutputs();

    default List<FluidStack> getActualFluidOutputs(TileEntity tile) { return getFluidOutputs(); }

    default ItemStack getDisplayStack(ItemStack input) {
        for (ICIngredient ingredient : getItemInputs()) {
            if (ingredient.matchesItemStack(input)) {
                ItemStack copy = input.copy();
                copy.setCount(ingredient.inputSize);
                return copy;
            }
        }
        return ItemStack.EMPTY;
    }

    int getTotalProcessTime();

    int getTotalProcessEnergy();

    int getMultipleProcessTicks();

    NBTTagCompound writeToNBT(NBTTagCompound nbt);
}
