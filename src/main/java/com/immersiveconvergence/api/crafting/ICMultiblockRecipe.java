package com.immersiveconvergence.api.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") public abstract class ICMultiblockRecipe extends ICSerializableRecipe {
    private List<ICIngredientWithSize> inputList = new ArrayList<>(0);
    protected Lazy<NonNullList<ItemStack>> outputList = Lazy.of(NonNullList::create);
    protected List<ICFluidTagInput> fluidInputList;
    protected List<FluidStack> fluidOutputList;

    protected <T extends Recipe<?>> ICMultiblockRecipe(Lazy<ItemStack> outputDummy, ICRecipeType<T> type, ResourceLocation id) { super(outputDummy, type, id); }

    public abstract int getTotalProcessTime();

    public abstract int getTotalProcessEnergy();

    public abstract int getMultipleProcessTicks();

    @Override @Nonnull public ItemStack getResultItem(@Nonnull RegistryAccess access) {
        NonNullList<ItemStack> outputs = getItemOutputs();
        return outputs != null && !outputs.isEmpty() ? outputs.get(0) : ItemStack.EMPTY;
    }

    public List<ICIngredientWithSize> getItemInputs() { return inputList; }

    protected void setInputListWithSizes(List<ICIngredientWithSize> inputList) { this.inputList = new ArrayList<>(inputList); }

    protected void setInputList(List<Ingredient> inputList) { this.inputList = inputList.stream().map(ICIngredientWithSize::new).toList(); }

    public boolean shouldCheckItemAvailability() { return true; }

    public List<ICFluidTagInput> getFluidInputs() { return fluidInputList; }

    public NonNullList<ItemStack> getItemOutputs() { return outputList.get(); }

    public NonNullList<ItemStack> getActualItemOutputs() { return getItemOutputs(); }

    public List<FluidStack> getFluidOutputs() { return fluidOutputList; }

    public List<FluidStack> getActualFluidOutputs() { return getFluidOutputs(); }

    public ItemStack getDisplayStack(ItemStack input) {
        for (ICIngredientWithSize ingr : getItemInputs()) {
            if (ingr.test(input)) { return ingr.hasNoMatchingItems() ? input : ingr.getMatchingStacks()[0]; }
        }
        return ItemStack.EMPTY;
    }
}
