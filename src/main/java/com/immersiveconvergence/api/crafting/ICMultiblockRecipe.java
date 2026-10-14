package com.immersiveconvergence.api.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("unused") public abstract class ICMultiblockRecipe extends MultiblockRecipe {
    @Nullable private List<ICIngredientWithSize> icItemInputs;

    protected <T extends Recipe<?>> ICMultiblockRecipe(ICTagOutput outputDummy, ICRecipeType<T> type, int baseTime, int baseEnergy) {
        super(outputDummy, type.ie(), baseTime, baseEnergy, () -> new RecipeMultiplier(() -> 1.0, () -> 1.0));
    }

    @Nullable public List<ICIngredientWithSize> getICItemInputs() {
        if (icItemInputs == null && getItemInputs() != null) { icItemInputs = getItemInputs().stream().map(ICIngredientWithSize::copyOf).toList(); }
        return icItemInputs;
    }

    protected abstract ICRecipeSerializer<?> getICSerializer();

    @Override protected final IERecipeSerializer<?> getIESerializer() { return getICSerializer(); }
}
