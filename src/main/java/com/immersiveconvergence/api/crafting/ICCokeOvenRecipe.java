package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.api.ICMods;

import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

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

    private ICCokeOvenRecipe(ItemStack output, int time, int creosoteOutput, int inputSize) {
        this.output = output;
        this.time = time;
        this.creosoteOutput = creosoteOutput;
        this.inputSize = inputSize;
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
        if (!ICMods.immersiveEngineering()) { return findFabricationRecipe(input); }
        CokeOvenRecipe recipe = CokeOvenRecipe.findRecipe(input);
        return recipe == null ? null : new ICCokeOvenRecipe(recipe);
    }

    @Nullable private static ICCokeOvenRecipe findFabricationRecipe(ItemStack input) {
        if (OreDictionary.itemMatches(new ItemStack(Items.COAL), input, false)) { return oreOutputRecipe("fuelCoke", 1800, 500); }
        if (OreDictionary.containsMatch(false, OreDictionary.getOres("blockCoal"), input)) { return oreOutputRecipe("blockFuelCoke", 1800 * 9, 5000); }
        if (OreDictionary.containsMatch(false, OreDictionary.getOres("logWood"), input)) { return new ICCokeOvenRecipe(new ItemStack(Items.COAL, 1, 1), 900, 250, 1); }
        return null;
    }

    @Nullable private static ICCokeOvenRecipe oreOutputRecipe(String ore, int time, int creosoteOutput) {
        NonNullList<ItemStack> stacks = OreDictionary.getOres(ore);
        return stacks.isEmpty() ? null : new ICCokeOvenRecipe(ItemHandlerHelper.copyStackWithSize(stacks.get(0), 1), time, creosoteOutput, 1);
    }
}
