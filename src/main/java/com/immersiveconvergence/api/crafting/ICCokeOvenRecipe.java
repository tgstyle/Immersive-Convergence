package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.common.ie.IECokeOvenBridge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import javax.annotation.Nullable;
import java.util.List;

public record ICCokeOvenRecipe(ResourceLocation id, ICIngredientWithSize input, ICTagOutput output, int time, int creosoteOutput) {
    public boolean matches(ItemStack stack) { return input.testIgnoringSize(stack); }

    @Nullable public static ICCokeOvenRecipe findRecipe(Level level, ItemStack stack) {
        if (stack.isEmpty()) { return null; }
        for (ICCokeOvenRecipe recipe : getRecipes(level)) {
            if (recipe.matches(stack)) { return recipe; }
        }
        return null;
    }

    @Nullable public static ICCokeOvenRecipe getById(Level level, ResourceLocation id) { return ICMods.immersiveEngineering() ? IECokeOvenBridge.getById(level, id) : null; }

    public static List<ICCokeOvenRecipe> getRecipes(Level level) { return ICMods.immersiveEngineering() ? IECokeOvenBridge.recipes(level) : List.of(); }

    public static Fluid creosote() { return BuiltInRegistries.FLUID.get(ICMods.ie("creosote")); }

    @Nullable public static ICRecipeType<?> recipeType() { return ICMods.immersiveEngineering() ? IECokeOvenBridge.recipeType() : null; }
}
