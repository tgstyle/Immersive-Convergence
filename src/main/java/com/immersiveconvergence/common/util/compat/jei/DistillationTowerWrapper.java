package com.immersiveconvergence.common.util.compat.jei;

import blusunrize.immersiveengineering.common.util.compat.jei.MultiblockRecipeWrapper;
import flaxbeard.immersivepetroleum.api.crafting.DistillationRecipe;

public class DistillationTowerWrapper extends MultiblockRecipeWrapper {
    public final DistillationRecipe recipe;

    public DistillationTowerWrapper(DistillationRecipe recipe) {
        super(recipe);
        this.recipe = recipe;
    }
}
