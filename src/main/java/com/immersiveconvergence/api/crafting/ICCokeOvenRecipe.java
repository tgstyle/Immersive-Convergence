package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.api.ICMods;

import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

import java.util.Collection;

@SuppressWarnings("unused")
public class ICCokeOvenRecipe {
    public final ItemStack output;
    public final int time;
    public final int creosoteOutput;
    public final int inputSize;

    private ICCokeOvenRecipe(CokeOvenRecipe recipe) {
        this.output = recipe.output;
        this.time = recipe.time;
        this.creosoteOutput = recipe.creosoteOutput;
        this.inputSize = inputSize(recipe.input);
    }

    private static int inputSize(Object input) {
        if (input instanceof ItemStack) { return Math.max(1, ((ItemStack)input).getCount()); }
        if (input instanceof IngredientStack) { return Math.max(1, ((IngredientStack)input).inputSize); }
        if (input instanceof Collection) {
            for (Object entry : (Collection<?>)input) {
                if (entry instanceof ItemStack) { return Math.max(1, ((ItemStack)entry).getCount()); }
            }
        }
        return 1;
    }

    @Nullable public static ICCokeOvenRecipe findRecipe(ItemStack input) {
        if (!ICMods.immersiveEngineering()) { return null; }
        CokeOvenRecipe recipe = CokeOvenRecipe.findRecipe(input);
        return recipe == null ? null : new ICCokeOvenRecipe(recipe);
    }
}
