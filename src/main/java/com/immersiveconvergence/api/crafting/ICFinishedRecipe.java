package com.immersiveconvergence.api.crafting;

import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;

@SuppressWarnings("unused") public class ICFinishedRecipe<R extends ICFinishedRecipe<R>> extends IEFinishedRecipe<R> {
    protected ICFinishedRecipe(ICRecipeSerializer<?> serializer) { super(serializer); }
}
