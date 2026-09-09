package com.immersiveconvergence.api.crafting;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class ICIngredientStack extends IngredientStack {
    public ICIngredientStack(IngredientStack ingredient) { super(ingredient); }

    public ICIngredientStack(ItemStack stack) { super(stack); }

    public ICIngredientStack(String oreName) { super(oreName); }

    public ICIngredientStack(String oreName, int inputSize) { super(oreName, inputSize); }

    public static ICIngredientStack of(Object input) { return new ICIngredientStack(ApiUtils.createIngredientStack(input)); }

    @Nullable public static ICIngredientStack readFromNBT(NBTTagCompound nbt) {
        IngredientStack ingredient = IngredientStack.readFromNBT(nbt);
        return ingredient == null ? null : new ICIngredientStack(ingredient);
    }
}
