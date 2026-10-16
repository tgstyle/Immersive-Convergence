package com.immersiveconvergence.api.crafting.builders;

import com.immersiveconvergence.api.ICMods;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class ICIEConditional {
    private ICIEConditional() {}

    public static Consumer<FinishedRecipe> wrap(Consumer<FinishedRecipe> out) { return recipe -> out.accept(new Wrapped(recipe)); }

    static void addConditions(JsonObject json) { json.add("conditions", CraftingHelper.serialize(new ICondition[] {new ModLoadedCondition(ICMods.IMMERSIVE_ENGINEERING)})); }

    private record Wrapped(FinishedRecipe recipe) implements FinishedRecipe {
        @Override public void serializeRecipeData(@Nonnull JsonObject json) { recipe.serializeRecipeData(json); }

        @Override public @Nonnull JsonObject serializeRecipe() {
            JsonObject json = recipe.serializeRecipe();
            addConditions(json);
            return json;
        }

        @Override public @Nonnull ResourceLocation getId() { return recipe.getId(); }

        @Override public @Nonnull RecipeSerializer<?> getType() { return recipe.getType(); }

        @Override public @Nullable JsonObject serializeAdvancement() {
            JsonObject json = recipe.serializeAdvancement();
            if (json != null) { addConditions(json); }
            return json;
        }

        @Override public @Nullable ResourceLocation getAdvancementId() { return recipe.getAdvancementId(); }
    }
}
