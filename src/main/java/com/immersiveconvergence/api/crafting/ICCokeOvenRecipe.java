package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.api.ICMods;

import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import blusunrize.immersiveengineering.common.register.IEFluids;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") public record ICCokeOvenRecipe(ResourceLocation id, ICIngredientWithSize input, Lazy<ItemStack> output, int time, int creosoteOutput) {
    public boolean matches(ItemStack stack) { return input.test(stack); }

    @Nullable public static ICCokeOvenRecipe findRecipe(Level level, ItemStack stack) {
        if (stack.isEmpty()) { return null; }
        for (ICCokeOvenRecipe recipe : getRecipes(level)) {
            if (recipe.matches(stack)) { return recipe; }
        }
        return null;
    }

    public static List<ICCokeOvenRecipe> getRecipes(Level level) {
        if (!ICMods.immersiveEngineering()) { return List.of(); }
        List<ICCokeOvenRecipe> recipes = new ArrayList<>();
        for (CokeOvenRecipe recipe : CokeOvenRecipe.RECIPES.getRecipes(level)) { recipes.add(copyOf(recipe)); }
        return recipes;
    }

    public static Fluid creosote() { return ICMods.immersiveEngineering() ? IEFluids.CREOSOTE.getStill() : Fluids.EMPTY; }

    public static ICRecipeType<?> recipeType() { return new ICRecipeType<>(IERecipeTypes.COKE_OVEN.type(), IERecipeTypes.COKE_OVEN.recipeClass()); }

    public static ICCokeOvenRecipe copyOf(CokeOvenRecipe recipe) { return new ICCokeOvenRecipe(recipe.getId(), ICIngredientWithSize.copyOf(recipe.input), recipe.output, recipe.time, recipe.creosoteOutput); }
}
