package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.bridge.ICBridges;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import javax.annotation.Nullable;
import java.util.List;

public record ICCokeOvenRecipe(ResourceLocation id, ICIngredientWithSize input, ICTagOutput output, int time, int creosoteOutput) {
    private static final TagKey<Item> COAL_COKE = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "coal_coke"));
    private static final TagKey<Item> STORAGE_BLOCKS_COAL_COKE = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/coal_coke"));
    private static final List<ICCokeOvenRecipe> FABRICATION_RECIPES = List.of(
            new ICCokeOvenRecipe(ICMods.ifab("cokeoven/charcoal"), new ICIngredientWithSize(Ingredient.of(ItemTags.LOGS_THAT_BURN), 8), new ICTagOutput(Items.CHARCOAL), 3000, 250),
            new ICCokeOvenRecipe(ICMods.ifab("cokeoven/coke"), new ICIngredientWithSize(Ingredient.of(Items.COAL), 16), new ICTagOutput(new ICIngredientWithSize(Ingredient.of(COAL_COKE))), 6000, 500),
            new ICCokeOvenRecipe(ICMods.ifab("cokeoven/coke_block"), new ICIngredientWithSize(Ingredient.of(Items.COAL_BLOCK), 2), new ICTagOutput(new ICIngredientWithSize(Ingredient.of(STORAGE_BLOCKS_COAL_COKE))), 6000, 5000));

    public boolean matches(ItemStack stack) { return input.testIgnoringSize(stack); }

    @Nullable public static ICCokeOvenRecipe findRecipe(Level level, ItemStack stack) {
        if (stack.isEmpty()) { return null; }
        for (ICCokeOvenRecipe recipe : getRecipes(level)) {
            if (recipe.matches(stack)) { return recipe; }
        }
        return null;
    }

    @Nullable public static ICCokeOvenRecipe getById(Level level, ResourceLocation id) { return ICMods.immersiveEngineering() ? ICBridges.integration().cokeOvenRecipeById(level, id) : FABRICATION_RECIPES.stream().filter(recipe -> recipe.id().equals(id)).findFirst().orElse(null); }

    public static List<ICCokeOvenRecipe> getRecipes(Level level) { return ICMods.immersiveEngineering() ? ICBridges.integration().cokeOvenRecipes(level) : FABRICATION_RECIPES; }

    public static Fluid creosote() { return BuiltInRegistries.FLUID.get(ICMods.immersiveEngineering() ? ICMods.ie("creosote") : ICMods.ifab("creosote")); }

    @Nullable public static ICRecipeType<?> recipeType() { return ICMods.immersiveEngineering() ? ICBridges.integration().cokeOvenRecipeType() : null; }
}
