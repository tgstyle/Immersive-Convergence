package com.immersiveconvergence.api.crafting;

import blusunrize.immersiveengineering.api.crafting.IMultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public interface ICMultiblockRecipe extends IMultiblockRecipe {
    @Override default List<IngredientStack> getItemInputs() { return Collections.emptyList(); }
}
