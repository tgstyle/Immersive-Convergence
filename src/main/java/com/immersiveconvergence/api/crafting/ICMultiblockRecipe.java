package com.immersiveconvergence.api.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import net.minecraft.world.item.crafting.Recipe;

@SuppressWarnings("unused") public abstract class ICMultiblockRecipe extends MultiblockRecipe {
    protected <T extends Recipe<?>> ICMultiblockRecipe(ICTagOutput outputDummy, ICRecipeType<T> type, int baseTime, int baseEnergy) {
        super(outputDummy, type.ie(), baseTime, baseEnergy, () -> new RecipeMultiplier(() -> 1.0, () -> 1.0));
    }

    protected abstract ICRecipeSerializer<?> getICSerializer();

    @Override protected final IERecipeSerializer<?> getIESerializer() { return getICSerializer(); }
}
