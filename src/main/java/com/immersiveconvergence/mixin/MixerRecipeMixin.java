package com.immersiveconvergence.mixin;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MixerRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;

@Mixin(MixerRecipe.class)
public abstract class MixerRecipeMixin {
    @Inject(method = "findRecipe(Lnet/minecraft/world/level/Level;Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraft/core/NonNullList;)Lblusunrize/immersiveengineering/api/crafting/MixerRecipe;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void ic$prioritizeHigherIngredients(Level level, FluidStack fluid, NonNullList<ItemStack> components, CallbackInfoReturnable<MixerRecipe> cir) {
        List<MixerRecipe> allMatching = MixerRecipe.RECIPES.getRecipes(level).stream().filter(r -> r.matches(fluid, components)).toList();
        cir.setReturnValue(allMatching.stream().max(Comparator.comparingInt((MixerRecipe r) -> ic$ingredientSum(r.itemInputs)).thenComparingInt(r -> ic$ingredientCount(r.itemInputs))).orElse(null));
    }

    @Unique private static int ic$ingredientSum(IngredientWithSize[] inputs) {
        int sum = 0;
        for (IngredientWithSize input : inputs) {
            if (input != null) { sum += input.getCount(); }
        }
        return sum;
    }

    @Unique private static int ic$ingredientCount(IngredientWithSize[] inputs) {
        int count = 0;
        for (IngredientWithSize input : inputs) {
            if (input != null) { count++; }
        }
        return count;
    }
}
