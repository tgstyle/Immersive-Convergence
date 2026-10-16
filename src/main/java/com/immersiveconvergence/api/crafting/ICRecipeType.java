package com.immersiveconvergence.api.crafting;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import java.util.function.Supplier;

public record ICRecipeType<T extends Recipe<?>>(RegistryObject<RecipeType<T>> type, Class<T> recipeClass) implements Supplier<RecipeType<T>> {
    public static <T extends Recipe<?>> ICRecipeType<T> register(DeferredRegister<RecipeType<?>> register, String name, Class<T> recipeClass) { return new ICRecipeType<>(register.register(name, () -> new RecipeType<>() {}), recipeClass); }

    @Override public RecipeType<T> get() { return type.get(); }
}
