package com.immersiveconvergence.mixin.ie;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MixerRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Mixin(MixerRecipe.class) public abstract class MixerRecipeMixin {
    @Inject(method = "findRecipe(Lnet/minecraft/world/level/Level;Lnet/neoforged/neoforge/fluids/FluidStack;Lnet/minecraft/core/NonNullList;)Lnet/minecraft/world/item/crafting/RecipeHolder;", at = @At("RETURN"), cancellable = true, remap = false) private static void ic$prioritizeHigherIngredients(Level level, FluidStack fluid, NonNullList<ItemStack> components, CallbackInfoReturnable<RecipeHolder<MixerRecipe>> cir) {
        List<RecipeHolder<MixerRecipe>> allMatching = MixerRecipe.RECIPES.getRecipes(level).stream().filter(r -> r.value().matches(fluid, components)).toList();
        cir.setReturnValue(allMatching.stream().max(Comparator.comparingInt((RecipeHolder<MixerRecipe> r) -> ic$ingredientSum(r.value().itemInputs)).thenComparingLong(r -> ic$ingredientCount(r.value().itemInputs))).orElse(null));
    }

    @Unique private static int ic$ingredientSum(List<IngredientWithSize> inputs) {
        int sum = 0;
        for (IngredientWithSize input : inputs) {
            if (input != null) { sum += input.getCount(); }
        }
        return sum;
    }

    @Unique private static long ic$ingredientCount(List<IngredientWithSize> inputs) { return inputs.stream().filter(Objects::nonNull).count(); }
}
