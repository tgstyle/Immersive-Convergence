package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.api.util.ICUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.common.util.Lazy;
import javax.annotation.Nonnull;

public abstract class ICRecipeSerializer<R extends Recipe<?>> implements RecipeSerializer<R> {
    public abstract ItemStack getIcon();

    public abstract R readFromJson(ResourceLocation recipeId, JsonObject json, IContext context);

    @Override public R fromJson(ResourceLocation recipeId, JsonObject json, IContext context) { return CraftingHelper.processConditions(json, "conditions", context) ? readFromJson(recipeId, json, context) : null; }

    @Override @Nonnull public R fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject serializedRecipe) { return readFromJson(recipeId, serializedRecipe, IContext.EMPTY); }

    protected static Lazy<ItemStack> readOutput(JsonElement outputObject) {
        if (outputObject.isJsonObject() && outputObject.getAsJsonObject().has("item")) { return Lazy.of(() -> ShapedRecipe.itemStackFromJson(outputObject.getAsJsonObject())); }
        ICIngredientWithSize outgredient = ICIngredientWithSize.deserialize(outputObject);
        return Lazy.of(() -> outgredient.hasNoMatchingItems() ? ItemStack.EMPTY : ICUtils.preferredStack(outgredient.getMatchingStacks()));
    }

    protected static Lazy<ItemStack> readLazyStack(FriendlyByteBuf buf) {
        ItemStack stack = buf.readItem();
        return Lazy.of(() -> stack);
    }

    protected static void writeLazyStack(FriendlyByteBuf buf, Lazy<ItemStack> stack) { buf.writeItem(stack.get()); }
}
