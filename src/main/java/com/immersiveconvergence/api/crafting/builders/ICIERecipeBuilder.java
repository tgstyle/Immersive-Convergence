package com.immersiveconvergence.api.crafting.builders;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.crafting.ICIngredientWithSize;
import com.immersiveconvergence.api.crafting.ICTagOutput;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import java.util.List;

public abstract class ICIERecipeBuilder<B extends ICIERecipeBuilder<B>> {
    protected final JsonObject json = new JsonObject();

    protected ICIERecipeBuilder(String type) { json.addProperty("type", ICMods.ie(type).toString()); }

    public void build(ICJsonRecipes recipes, ResourceLocation id) {
        ICondition.writeConditions(JsonOps.INSTANCE, json, List.of(new ModLoadedCondition(ICMods.IMMERSIVE_ENGINEERING)));
        recipes.accept(id, json);
    }

    @SuppressWarnings("unchecked") protected B self() { return (B) this; }

    public B energy(int energy) {
        json.addProperty("energy", energy);
        return self();
    }

    public B time(int time) {
        json.addProperty("time", time);
        return self();
    }

    public B result(TagKey<Item> tag, int count) { return result(sized(tag, count)); }

    public B input(TagKey<Item> tag, int count) {
        json.add("input", sized(tag, count));
        return self();
    }

    public B secondary(TagKey<Item> tag, float chance) {
        array("secondaries").add(chance(sized(tag, 1), chance));
        return self();
    }

    protected B result(JsonElement result) {
        json.add("result", result);
        return self();
    }

    protected JsonArray array(String key) {
        if (!json.has(key)) { json.add(key, new JsonArray()); }
        return json.getAsJsonArray(key);
    }

    protected static <T> JsonElement encode(Codec<T> codec, T value) { return codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow(); }

    protected static JsonElement ingredient(Ingredient ingredient) { return encode(Ingredient.CODEC_NONEMPTY, ingredient); }

    protected static JsonElement sized(TagKey<Item> tag, int count) { return encode(ICIngredientWithSize.CODEC, new ICIngredientWithSize(Ingredient.of(tag), count)); }

    protected static JsonElement stack(ItemLike item, int count) { return encode(ICTagOutput.CODECS.codec(), new ICTagOutput(item, count)); }

    protected static JsonObject chance(JsonElement output, float chance) {
        JsonObject entry = new JsonObject();
        entry.addProperty("chance", chance);
        entry.add("output", output);
        return entry;
    }
}
