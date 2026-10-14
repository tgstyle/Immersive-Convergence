package com.immersiveconvergence.api.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused") public class ICIngredientWithSize implements Predicate<ItemStack> {
    private static final String COUNT_KEY = "count";
    private static final String BASE_KEY = "base_ingredient";
    protected final Ingredient basePredicate;
    protected final int count;

    public ICIngredientWithSize(Ingredient basePredicate, int count) {
        this.basePredicate = basePredicate;
        this.count = count;
    }

    public ICIngredientWithSize(Ingredient basePredicate) { this(basePredicate, 1); }

    public ICIngredientWithSize(TagKey<Item> basePredicate, int count) { this(Ingredient.of(basePredicate), count); }

    public ICIngredientWithSize(TagKey<Item> basePredicate) { this(basePredicate, 1); }

    public static ICIngredientWithSize deserialize(JsonElement input) {
        if (input.isJsonObject() && input.getAsJsonObject().has(BASE_KEY)) { return new ICIngredientWithSize(Ingredient.fromJson(input.getAsJsonObject().get(BASE_KEY)), GsonHelper.getAsInt(input.getAsJsonObject(), COUNT_KEY, 1)); }
        return new ICIngredientWithSize(Ingredient.fromJson(input), 1);
    }

    public static ICIngredientWithSize read(FriendlyByteBuf input) {
        int count = input.readInt();
        return new ICIngredientWithSize(Ingredient.fromNetwork(input), count);
    }

    public void write(FriendlyByteBuf out) {
        out.writeInt(count);
        CraftingHelper.write(out, basePredicate);
    }

    @Nonnull public JsonElement serialize() {
        if (count == 1) { return basePredicate.toJson(); }
        JsonObject json = new JsonObject();
        json.addProperty(COUNT_KEY, count);
        json.add(BASE_KEY, basePredicate.toJson());
        return json;
    }

    @Override public boolean test(@Nullable ItemStack stack) { return stack != null && basePredicate.test(stack) && stack.getCount() >= count; }

    public boolean testIgnoringSize(ItemStack stack) { return basePredicate.test(stack); }

    @Nonnull public ItemStack[] getMatchingStacks() {
        ItemStack[] baseStacks = basePredicate.getItems();
        ItemStack[] ret = new ItemStack[baseStacks.length];
        for (int i = 0; i < baseStacks.length; ++i) { ret[i] = baseStacks[i].copyWithCount(count); }
        return ret;
    }

    @Nonnull public List<ItemStack> getMatchingStackList() { return Arrays.asList(getMatchingStacks()); }

    public boolean hasNoMatchingItems() { return basePredicate.isEmpty(); }

    public int getCount() { return count; }

    public Ingredient getBaseIngredient() { return basePredicate; }

    public ICIngredientWithSize withSize(int size) { return new ICIngredientWithSize(basePredicate, size); }

    public static ICIngredientWithSize of(ItemStack stack) { return new ICIngredientWithSize(Ingredient.of(stack), stack.getCount()); }

    public ItemStack getRandomizedExampleStack(int rand) {
        ItemStack[] all = getMatchingStacks();
        return all.length == 0 ? ItemStack.EMPTY : all[(rand / 20) % all.length];
    }
}
