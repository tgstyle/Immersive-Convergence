package com.immersiveconvergence.common.ie;

import com.immersiveconvergence.api.crafting.ICCokeOvenRecipe;

import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.level.Level;
import java.util.Objects;
import java.util.function.Predicate;

public final class IECokeOvenJEIBridge {
    private IECokeOvenJEIBridge() {}

    public static void addRecipes(IRecipeRegistration registration, Level level, Predicate<ICCokeOvenRecipe> filter) {
        RecipeType<CokeOvenRecipe> type = new RecipeType<>(Objects.requireNonNull(IERecipeTypes.COKE_OVEN.type().getId()), CokeOvenRecipe.class);
        registration.addRecipes(type, CokeOvenRecipe.RECIPES.getRecipes(level).stream().filter(recipe -> filter.test(IECokeOvenBridge.copyOf(recipe))).toList());
    }
}
