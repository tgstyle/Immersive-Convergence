package com.immersiveconvergence.api.crafting.builders;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class ICJsonRecipes {
    private final Map<ResourceLocation, JsonObject> recipes = new LinkedHashMap<>();

    void accept(ResourceLocation id, JsonObject json) { if (recipes.putIfAbsent(id, json) != null) { throw new IllegalStateException("Duplicate recipe " + id); } }

    public CompletableFuture<?> save(CachedOutput output, PackOutput.PathProvider paths) { return CompletableFuture.allOf(recipes.entrySet().stream().map(entry -> DataProvider.saveStable(output, entry.getValue(), paths.json(entry.getKey()))).toArray(CompletableFuture[]::new)); }
}
