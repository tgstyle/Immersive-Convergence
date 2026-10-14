package com.immersiveconvergence.api.crafting.builders;

import com.immersiveconvergence.api.ICMods;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.List;

public abstract class ICIERecipeBuilder {
    protected final JsonObject json = new JsonObject();

    protected ICIERecipeBuilder(String type) { json.addProperty("type", ICMods.IMMERSIVE_ENGINEERING + ":" + type); }

    public void build(ICJsonRecipes recipes, ResourceLocation id) {
        ICondition.writeConditions(JsonOps.INSTANCE, json, List.of(new ModLoadedCondition(ICMods.IMMERSIVE_ENGINEERING)));
        recipes.accept(id, json);
    }

    protected JsonArray array(String key) {
        if (!json.has(key)) { json.add(key, new JsonArray()); }
        return json.getAsJsonArray(key);
    }

    protected static JsonElement ingredient(Ingredient ingredient) { return Ingredient.CODEC_NONEMPTY.encodeStart(JsonOps.INSTANCE, ingredient).getOrThrow(); }

    protected static JsonElement sized(TagKey<Item> tag, int count) {
        JsonElement base = ingredient(Ingredient.of(tag));
        if (count == 1) { return base; }
        JsonObject sized = new JsonObject();
        sized.add("basePredicate", base);
        sized.addProperty("count", count);
        return sized;
    }

    protected static JsonObject stack(ItemLike item, int count) {
        JsonObject stack = new JsonObject();
        stack.addProperty("id", BuiltInRegistries.ITEM.getKey(item.asItem()).toString());
        if (count != 1) { stack.addProperty("count", count); }
        return stack;
    }

    protected static JsonObject chance(JsonElement output, float chance) {
        JsonObject entry = new JsonObject();
        entry.addProperty("chance", chance);
        entry.add("output", output);
        return entry;
    }
}
