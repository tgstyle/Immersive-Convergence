package com.immersiveconvergence.common.util.compat.jei;

import com.immersiveconvergence.api.crafting.ICRecipeBase;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.List;

@SuppressWarnings("unused")
public abstract class ICMultiblockRecipeWrapper implements IRecipeWrapper {
    public List<List<ItemStack>> recipeInputs;
    protected List<ItemStack> inputs;
    public List<List<ItemStack>> recipeOutputs;
    protected List<ItemStack> outputs;
    protected List<FluidStack> fluidInputs;
    protected List<FluidStack> fluidOutputs;

    public ICMultiblockRecipeWrapper(ICRecipeBase recipe) {
        recipe.setupJEI();
        this.inputs = recipe.getJEITotalItemInputs();
        this.recipeInputs = recipe.jeiItemInputList;
        this.outputs = recipe.getJEITotalItemOutputs();
        this.recipeOutputs = recipe.jeiItemOutputList;
        this.fluidInputs = recipe.getJEITotalFluidInputs();
        this.fluidOutputs = recipe.getJEITotalFluidOutputs();
    }

    @Override public void getIngredients(@Nonnull IIngredients ingredients) {
        if (!inputs.isEmpty()) { ingredients.setInputs(VanillaTypes.ITEM, inputs); }
        if (!outputs.isEmpty()) { ingredients.setOutputs(VanillaTypes.ITEM, outputs); }
        if (!fluidInputs.isEmpty()) { ingredients.setInputs(VanillaTypes.FLUID, fluidInputs); }
        if (!fluidOutputs.isEmpty()) { ingredients.setOutputs(VanillaTypes.FLUID, fluidOutputs); }
    }

    public List<ItemStack> getItemIn() { return inputs; }

    public List<ItemStack> getItemOut() { return outputs; }

    public List<FluidStack> getFluidIn() { return fluidInputs; }

    public List<FluidStack> getFluidOut() { return fluidOutputs; }
}
