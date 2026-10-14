package com.immersiveconvergence.api.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public record ICRecipeType<T extends Recipe<?>>(DeferredHolder<RecipeType<?>, RecipeType<T>> type, Class<T> recipeClass) implements Supplier<RecipeType<T>> {
    public static <T extends Recipe<?>> ICRecipeType<T> register(DeferredRegister<RecipeType<?>> register, String name, Class<T> recipeClass) {
        return new ICRecipeType<>(register.register(name, () -> new RecipeType<>() {}), recipeClass);
    }

    @Override public RecipeType<T> get() { return type.get(); }

    IERecipeTypes.TypeWithClass<T> ie() { return new IERecipeTypes.TypeWithClass<>(type, recipeClass); }
}
