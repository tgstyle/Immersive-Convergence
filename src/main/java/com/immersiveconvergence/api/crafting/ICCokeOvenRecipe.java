package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.bridge.ICBridges;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import javax.annotation.Nullable;
import java.util.List;

public record ICCokeOvenRecipe(ResourceLocation id, ICIngredientWithSize input, Lazy<ItemStack> output, int time, int creosoteOutput) {
    private static final List<ICCokeOvenRecipe> NO_IE_RECIPES = List.of(
            new ICCokeOvenRecipe(ICLib.rl("cokeoven/charcoal"), new ICIngredientWithSize(ItemTags.LOGS, 1), Lazy.of(() -> new ItemStack(Items.CHARCOAL)), 900, 250),
            new ICCokeOvenRecipe(ICLib.rl("cokeoven/coke"), new ICIngredientWithSize(Ingredient.of(Items.COAL), 1), ifabStack("coal_coke"), 1800, 500),
            new ICCokeOvenRecipe(ICLib.rl("cokeoven/coke_block"), new ICIngredientWithSize(Ingredient.of(Items.COAL_BLOCK), 1), ifabStack("coal_coke_block"), 16200, 5000));

    private static Lazy<ItemStack> ifabStack(String path) { return Lazy.of(() -> new ItemStack(ForgeRegistries.ITEMS.getHolder(ICMods.ifab(path)).orElseThrow().value())); }

    public boolean matches(ItemStack stack) { return input.test(stack); }

    @Nullable public static ICCokeOvenRecipe findRecipe(Level level, ItemStack stack) {
        if (stack.isEmpty()) { return null; }
        for (ICCokeOvenRecipe recipe : getRecipes(level)) {
            if (recipe.matches(stack)) { return recipe; }
        }
        return null;
    }

    @Nullable public static ICCokeOvenRecipe getById(Level level, ResourceLocation id) { return ICMods.immersiveEngineering() ? ICBridges.integration().cokeOvenRecipeById(level, id) : NO_IE_RECIPES.stream().filter(recipe -> recipe.id().equals(id)).findFirst().orElse(null); }

    public static List<ICCokeOvenRecipe> getRecipes(Level level) { return ICMods.immersiveEngineering() ? ICBridges.integration().cokeOvenRecipes(level) : NO_IE_RECIPES; }

    public static Fluid creosote() { return ForgeRegistries.FLUIDS.getValue(ICMods.immersiveEngineering() ? ICMods.ie("creosote") : ICMods.ifab("creosote")); }

    @Nullable public static ICRecipeType<?> recipeType() { return ICMods.immersiveEngineering() ? ICBridges.integration().cokeOvenRecipeType() : null; }
}
