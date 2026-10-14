package com.immersiveconvergence.common.ie;

import com.immersiveconvergence.api.crafting.ICCokeOvenRecipe;
import com.immersiveconvergence.api.crafting.ICIngredientWithSize;
import com.immersiveconvergence.api.crafting.ICRecipeType;
import com.immersiveconvergence.api.crafting.ICTagOutput;

import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.List;

public final class IECokeOvenBridge {
    private IECokeOvenBridge() {}

    public static List<ICCokeOvenRecipe> recipes(Level level) { return CokeOvenRecipe.RECIPES.getRecipes(level).stream().map(IECokeOvenBridge::copyOf).toList(); }

    public static ICRecipeType<?> recipeType() { return new ICRecipeType<>(IERecipeTypes.COKE_OVEN.type(), IERecipeTypes.COKE_OVEN.recipeClass()); }

    private static ICCokeOvenRecipe copyOf(RecipeHolder<CokeOvenRecipe> holder) {
        CokeOvenRecipe recipe = holder.value();
        return new ICCokeOvenRecipe(holder.id(), new ICIngredientWithSize(recipe.input.getBaseIngredient(), recipe.input.getCount()), new ICTagOutput(recipe.output.get()), recipe.time, recipe.creosoteOutput);
    }
}
