package com.immersiveconvergence.api.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public abstract class ICRecipeBase implements ICRecipe, ICJEIRecipe {
    protected List<ICIngredient> inputList;
    protected NonNullList<ItemStack> outputList;
    protected List<FluidStack> fluidInputList;
    protected List<FluidStack> fluidOutputList;
    protected int totalProcessTime;
    protected int totalProcessEnergy;
    public List<List<ItemStack>> jeiItemInputList = new ArrayList<>();
    protected List<ItemStack> jeiTotalItemInputList;
    public List<List<ItemStack>> jeiItemOutputList = new ArrayList<>();
    protected List<ItemStack> jeiTotalItemOutputList;
    protected List<FluidStack> jeiFluidInputList;
    protected List<FluidStack> jeiFluidOutputList;

    @Override public List<ICIngredient> getItemInputs() { return inputList; }

    @Override public NonNullList<ItemStack> getItemOutputs() { return outputList; }

    @Override public List<FluidStack> getFluidInputs() { return fluidInputList; }

    @Override public List<FluidStack> getFluidOutputs() { return fluidOutputList; }

    @Override public int getTotalProcessTime() { return this.totalProcessTime; }

    @Override public int getTotalProcessEnergy() { return this.totalProcessEnergy; }

    private static ItemStack copyWithAmount(ItemStack stack, int amount) {
        if (stack.isEmpty()) { return ItemStack.EMPTY; }
        ItemStack copy = stack.copy();
        copy.setCount(amount);
        return copy;
    }

    public void setupJEI() {
        if (inputList != null) {
            this.jeiItemInputList = new ArrayList<>();
            this.jeiTotalItemInputList = new ArrayList<>();
            for (ICIngredient ingredient : inputList) {
                List<ItemStack> list = new ArrayList<>();
                if (ingredient.oreName != null) {
                    for (ItemStack ore : OreDictionary.getOres(ingredient.oreName)) {
                        list.add(copyWithAmount(ore, ingredient.inputSize));
                    }
                } else if (ingredient.stackList != null) {
                    for (ItemStack entry : ingredient.stackList) {
                        list.add(copyWithAmount(entry, ingredient.inputSize));
                    }
                } else {
                    list.add(copyWithAmount(ingredient.stack, ingredient.inputSize));
                }
                this.jeiItemInputList.add(list);
                this.jeiTotalItemInputList.addAll(list);
            }
        }
        else { this.jeiTotalItemInputList = Collections.emptyList(); }

        if (outputList != null) {
            this.jeiItemOutputList = new ArrayList<>();
            this.jeiTotalItemOutputList = new ArrayList<>();
            for (ItemStack output : outputList) {
                List<ItemStack> list = new ArrayList<>();
                list.add(!output.isEmpty() ? output.copy() : ItemStack.EMPTY);
                this.jeiItemOutputList.add(list);
                this.jeiTotalItemOutputList.addAll(list);
            }
        }
        else { this.jeiTotalItemOutputList = Collections.emptyList(); }

        this.jeiFluidInputList = copyFluids(fluidInputList);
        this.jeiFluidOutputList = copyFluids(fluidOutputList);
    }

    private static List<FluidStack> copyFluids(List<FluidStack> source) {
        if (source == null) { return Collections.emptyList(); }
        List<FluidStack> copies = new ArrayList<>();
        for (FluidStack fluid : source) {
            if (fluid != null) { copies.add(fluid.copy()); }
        }
        return copies;
    }

    @Override public List<ItemStack> getJEITotalItemInputs() { return jeiTotalItemInputList; }

    @Override public List<ItemStack> getJEITotalItemOutputs() { return jeiTotalItemOutputList; }

    @Override public List<FluidStack> getJEITotalFluidInputs() { return jeiFluidInputList; }

    @Override public List<FluidStack> getJEITotalFluidOutputs() { return jeiFluidOutputList; }
}
