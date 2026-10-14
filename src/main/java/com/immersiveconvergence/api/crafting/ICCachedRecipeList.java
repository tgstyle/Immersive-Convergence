package com.immersiveconvergence.api.crafting;

import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import net.minecraft.world.item.crafting.Recipe;

@SuppressWarnings("unused") public class ICCachedRecipeList<R extends Recipe<?>> extends CachedRecipeList<R> {
    public ICCachedRecipeList(ICRecipeType<R> type) { super(type.ie()); }

    public static int reloadCount() { return getReloadCount(); }
}
