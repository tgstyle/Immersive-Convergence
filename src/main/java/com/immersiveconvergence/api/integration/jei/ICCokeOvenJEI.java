package com.immersiveconvergence.api.integration.jei;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.crafting.ICCokeOvenRecipe;

import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.function.Predicate;

@SuppressWarnings("unused") public final class ICCokeOvenJEI {
    private ICCokeOvenJEI() {}

    public static void addRecipes(IRecipeRegistration registration, Level level, Predicate<ICCokeOvenRecipe> filter) {
        if (!ICMods.immersiveEngineering()) { return; }
        RecipeType<CokeOvenRecipe> type = new RecipeType<>(Objects.requireNonNull(IERecipeTypes.COKE_OVEN.type().getId()), CokeOvenRecipe.class);
        registration.addRecipes(type, CokeOvenRecipe.RECIPES.getRecipes(level).stream().filter(recipe -> filter.test(ICCokeOvenRecipe.copyOf(recipe))).toList());
    }
}
