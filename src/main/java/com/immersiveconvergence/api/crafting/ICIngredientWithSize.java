package com.immersiveconvergence.api.crafting;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

@SuppressWarnings("unused") public class ICIngredientWithSize extends IngredientWithSize {
    public ICIngredientWithSize(Ingredient basePredicate, int count) { super(basePredicate, count); }

    public ICIngredientWithSize(Ingredient basePredicate) { super(basePredicate); }

    public ICIngredientWithSize(TagKey<Item> basePredicate, int count) { super(basePredicate, count); }

    public static ICIngredientWithSize deserialize(JsonElement input) { return copyOf(IngredientWithSize.deserialize(input)); }

    public static ICIngredientWithSize read(FriendlyByteBuf input) { return copyOf(IngredientWithSize.read(input)); }

    public static ICIngredientWithSize of(ItemStack stack) { return new ICIngredientWithSize(Ingredient.of(stack), stack.getCount()); }

    @Override public ICIngredientWithSize withSize(int size) { return new ICIngredientWithSize(getBaseIngredient(), size); }

    static ICIngredientWithSize copyOf(IngredientWithSize ingredient) { return ingredient instanceof ICIngredientWithSize ic ? ic : new ICIngredientWithSize(ingredient.getBaseIngredient(), ingredient.getCount()); }
}
