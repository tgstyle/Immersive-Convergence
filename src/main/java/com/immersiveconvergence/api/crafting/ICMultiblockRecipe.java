package com.immersiveconvergence.api.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("unused") public abstract class ICMultiblockRecipe extends MultiblockRecipe {
    @Nullable private List<ICIngredientWithSize> icItemInputs;
    @Nullable private List<ICFluidTagInput> icFluidInputs;

    protected <T extends Recipe<?>> ICMultiblockRecipe(Lazy<ItemStack> outputDummy, ICRecipeType<T> type, ResourceLocation id) { super(outputDummy, type.ie(), id); }

    @Nullable public List<ICIngredientWithSize> getICItemInputs() {
        if (icItemInputs == null && getItemInputs() != null) { icItemInputs = getItemInputs().stream().map(ICIngredientWithSize::copyOf).toList(); }
        return icItemInputs;
    }

    @Nullable public List<ICFluidTagInput> getICFluidInputs() {
        if (icFluidInputs == null && getFluidInputs() != null) { icFluidInputs = getFluidInputs().stream().map(ICFluidTagInput::copyOf).toList(); }
        return icFluidInputs;
    }

    protected abstract ICRecipeSerializer<?> getICSerializer();

    @Override protected final IERecipeSerializer<?> getIESerializer() { return getICSerializer(); }
}
