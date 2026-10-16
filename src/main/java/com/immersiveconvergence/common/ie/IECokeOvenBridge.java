package com.immersiveconvergence.common.ie;

import com.immersiveconvergence.api.crafting.ICCokeOvenRecipe;
import com.immersiveconvergence.api.crafting.ICIngredientWithSize;
import com.immersiveconvergence.api.crafting.ICRecipeType;

import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.List;

public final class IECokeOvenBridge {
    private IECokeOvenBridge() {}

    public static List<ICCokeOvenRecipe> recipes(Level level) { return CokeOvenRecipe.RECIPES.getRecipes(level).stream().map(IECokeOvenBridge::copyOf).toList(); }

    @Nullable public static ICCokeOvenRecipe getById(Level level, ResourceLocation id) {
        CokeOvenRecipe recipe = CokeOvenRecipe.RECIPES.getById(level, id);
        return recipe != null ? copyOf(recipe) : null;
    }

    public static ICRecipeType<?> recipeType() { return new ICRecipeType<>(IERecipeTypes.COKE_OVEN.type(), IERecipeTypes.COKE_OVEN.recipeClass()); }

    private static ICCokeOvenRecipe copyOf(CokeOvenRecipe recipe) { return new ICCokeOvenRecipe(recipe.getId(), new ICIngredientWithSize(recipe.input.getBaseIngredient(), recipe.input.getCount()), recipe.output, recipe.time, recipe.creosoteOutput); }
}
