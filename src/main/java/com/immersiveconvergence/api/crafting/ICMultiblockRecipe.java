package com.immersiveconvergence.api.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") public abstract class ICMultiblockRecipe extends ICSerializableRecipe {
    private final int baseTime;
    private final int baseEnergy;
    private List<ICIngredientWithSize> inputList = new ArrayList<>(0);
    protected List<ICTagOutput> outputList = List.of();
    protected List<SizedFluidIngredient> fluidInputList;
    protected List<FluidStack> fluidOutputList;

    protected <T extends Recipe<?>> ICMultiblockRecipe(ICTagOutput outputDummy, ICRecipeType<T> type, int baseTime, int baseEnergy) {
        super(outputDummy, type);
        this.baseTime = baseTime;
        this.baseEnergy = baseEnergy;
    }

    @Override @Nonnull public ItemStack getResultItem(@Nonnull HolderLookup.Provider access) {
        NonNullList<ItemStack> outputs = getItemOutputs();
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.getFirst();
    }

    public List<ICIngredientWithSize> getItemInputs() { return inputList; }

    protected void setInputListWithSizes(List<ICIngredientWithSize> inputList) { this.inputList = new ArrayList<>(inputList); }

    protected void setInputList(List<Ingredient> inputList) { this.inputList = inputList.stream().map(ICIngredientWithSize::new).toList(); }

    public boolean shouldCheckItemAvailability() { return true; }

    public List<SizedFluidIngredient> getFluidInputs() { return fluidInputList; }

    public NonNullList<ItemStack> getItemOutputs() {
        NonNullList<ItemStack> outputs = NonNullList.create();
        for (ICTagOutput output : outputList) { outputs.add(output.get()); }
        return outputs;
    }

    public NonNullList<ItemStack> getActualItemOutputs() { return getItemOutputs(); }

    public List<FluidStack> getFluidOutputs() { return fluidOutputList; }

    public List<FluidStack> getActualFluidOutputs() { return getFluidOutputs(); }

    public ItemStack getDisplayStack(ItemStack input) {
        for (ICIngredientWithSize ingr : getItemInputs()) {
            if (ingr.test(input)) { return ingr.hasNoMatchingItems() ? input : ingr.getMatchingStacks()[0]; }
        }
        return ItemStack.EMPTY;
    }

    public int getTotalProcessTime() { return Math.max(1, baseTime); }

    public int getTotalProcessEnergy() { return Math.max(1, baseEnergy); }

    public abstract int getMultipleProcessTicks();

    public int getBaseTime() { return baseTime; }

    public int getBaseEnergy() { return baseEnergy; }
}
