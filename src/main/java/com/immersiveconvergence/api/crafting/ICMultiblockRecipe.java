package com.immersiveconvergence.api.crafting;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public interface ICMultiblockRecipe extends ICRecipe {
    @Override default List<ICIngredient> getItemInputs() { return Collections.emptyList(); }
}
