package com.immersiveconvergence.common.util.compat.jei;

import com.immersiveconvergence.api.crafting.MultiblockRecipeBase;

import blusunrize.immersiveengineering.common.util.compat.jei.MultiblockRecipeWrapper;

@SuppressWarnings("unused")
public abstract class ICMultiblockRecipeWrapper extends MultiblockRecipeWrapper {
    public ICMultiblockRecipeWrapper(MultiblockRecipeBase recipe) { super(recipe); }
}
