package com.immersiveconvergence.api.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class ICIngredientStack extends ICIngredient {
    public ICIngredientStack(ICIngredient ingredient) { super(ingredient); }

    public ICIngredientStack(ItemStack stack) { super(stack); }

    public ICIngredientStack(String oreName) { super(oreName); }

    public ICIngredientStack(String oreName, int inputSize) { super(oreName, inputSize); }

    public static ICIngredientStack of(Object input) { return new ICIngredientStack(ICIngredient.create(input)); }

    @Nullable public static ICIngredientStack readFromNBT(NBTTagCompound nbt) {
        ICIngredient ingredient = ICIngredient.readFromNBT(nbt);
        return ingredient == null ? null : new ICIngredientStack(ingredient);
    }
}
