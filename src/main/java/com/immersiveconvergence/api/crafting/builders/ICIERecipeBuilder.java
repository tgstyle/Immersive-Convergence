package com.immersiveconvergence.api.crafting.builders;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.crafting.ICIngredientWithSize;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class ICIERecipeBuilder<B extends ICIERecipeBuilder<B>> {
    protected final JsonObject json = new JsonObject();
    private final ResourceLocation type;

    protected ICIERecipeBuilder(String type) { this.type = ICMods.ie(type); }

    public void build(Consumer<FinishedRecipe> out, ResourceLocation id) { out.accept(new Result(id, type, json)); }

    @SuppressWarnings("unchecked") protected B self() { return (B) this; }

    public B energy(int energy) {
        json.addProperty("energy", energy);
        return self();
    }

    protected static JsonElement sized(TagKey<Item> tag, int count) { return new ICIngredientWithSize(Ingredient.of(tag), count).serialize(); }

    private record Result(ResourceLocation id, ResourceLocation type, JsonObject json) implements FinishedRecipe {
        @Override public void serializeRecipeData(@Nonnull JsonObject out) { json.entrySet().forEach(entry -> out.add(entry.getKey(), entry.getValue().deepCopy())); }

        @Override public @Nonnull JsonObject serializeRecipe() {
            JsonObject out = new JsonObject();
            out.addProperty("type", type.toString());
            serializeRecipeData(out);
            ICIEConditional.addConditions(out);
            return out;
        }

        @Override public @Nonnull ResourceLocation getId() { return id; }

        @Override public @Nonnull RecipeSerializer<?> getType() { return Objects.requireNonNull(ForgeRegistries.RECIPE_SERIALIZERS.getValue(type)); }

        @Override public @Nullable JsonObject serializeAdvancement() { return null; }

        @Override public @Nullable ResourceLocation getAdvancementId() { return null; }
    }
}
