package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.api.ICMods;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import javax.annotation.Nonnull;

@SuppressWarnings("unused") public final class IEOrIngredientFactory implements IIngredientFactory {
    @Nonnull @Override public Ingredient parse(JsonContext context, JsonObject json) { return CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, ICMods.immersiveEngineering() ? "ie" : "fallback"), context); }
}
