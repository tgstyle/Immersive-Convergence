package com.immersiveconvergence.api.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import net.minecraft.world.item.crafting.Recipe;

@SuppressWarnings("unused") public abstract class ICSerializableRecipe extends IESerializableRecipe {
    protected <T extends Recipe<?>> ICSerializableRecipe(ICTagOutput outputDummy, ICRecipeType<T> type) { super(outputDummy, type.ie()); }

    protected abstract ICRecipeSerializer<?> getICSerializer();

    @Override protected final IERecipeSerializer<?> getIESerializer() { return getICSerializer(); }
}
