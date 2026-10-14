package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.common.ie.IECokeOvenBridge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import javax.annotation.Nullable;
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

    public static List<ICCokeOvenRecipe> getRecipes(Level level) { return ICMods.immersiveEngineering() ? IECokeOvenBridge.recipes(level) : List.of(); }

    public static Fluid creosote() { return ForgeRegistries.FLUIDS.getValue(ResourceLocation.fromNamespaceAndPath(ICMods.IMMERSIVE_ENGINEERING, "creosote")); }

    @Nullable public static ICRecipeType<?> recipeType() { return ICMods.immersiveEngineering() ? IECokeOvenBridge.recipeType() : null; }
}
