package com.immersiveconvergence.api.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class ICMultiblockRecipe extends ICSerializableRecipe {
    private final int baseTime;
    private final int baseEnergy;
    private List<ICIngredientWithSize> inputList = new ArrayList<>(0);
    protected Lazy<NonNullList<ItemStack>> outputList = Lazy.of(NonNullList::create);
    protected List<ICFluidTagInput> fluidInputList;
    protected List<FluidStack> fluidOutputList;

    protected <T extends Recipe<?>> ICMultiblockRecipe(Lazy<ItemStack> outputDummy, ICRecipeType<T> type, ResourceLocation id, int baseTime, int baseEnergy) {
        super(outputDummy, type, id);
        this.baseTime = baseTime;
        this.baseEnergy = baseEnergy;
    }

    protected <T extends Recipe<?>> ICMultiblockRecipe(ICRecipeType<T> type, ResourceLocation id, int baseTime, int baseEnergy) { this(LAZY_EMPTY, type, id, baseTime, baseEnergy); }

    @Override @Nonnull public ItemStack getResultItem(@Nonnull RegistryAccess access) {
        NonNullList<ItemStack> outputs = getItemOutputs();
        return outputs != null && !outputs.isEmpty() ? outputs.get(0) : ItemStack.EMPTY;
    }

    public List<ICIngredientWithSize> getItemInputs() { return inputList; }

    protected void setInputListWithSizes(List<ICIngredientWithSize> inputList) { this.inputList = new ArrayList<>(inputList); }

    public List<ICFluidTagInput> getFluidInputs() { return fluidInputList; }

    public NonNullList<ItemStack> getItemOutputs() { return outputList.get(); }

    public List<FluidStack> getFluidOutputs() { return fluidOutputList; }

    public int getTotalProcessTime() { return Math.max(1, baseTime); }

    public int getTotalProcessEnergy() { return baseEnergy; }

    public int getMultipleProcessTicks() { return 0; }

    public int getBaseTime() { return baseTime; }

    public int getBaseEnergy() { return baseEnergy; }
}
