package com.immersiveconvergence.api.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

@SuppressWarnings("unused") public abstract class ICSerializableRecipe implements Recipe<RecipeInput> {
    protected final ICTagOutput outputDummy;
    protected final RecipeType<?> type;

    protected <T extends Recipe<?>> ICSerializableRecipe(ICTagOutput outputDummy, ICRecipeType<T> type) {
        this.outputDummy = outputDummy;
        this.type = type.get();
    }

    protected abstract ICRecipeSerializer<?> getICSerializer();

    @Override public boolean isSpecial() { return true; }

    @Override @Nonnull public ItemStack getToastSymbol() { return getICSerializer().getIcon(); }

    @Override public boolean matches(@Nonnull RecipeInput inv, @Nonnull Level level) { return false; }

    @Override @Nonnull public ItemStack assemble(@Nonnull RecipeInput inv, @Nonnull HolderLookup.Provider access) { return outputDummy.get(); }

    @Override public boolean canCraftInDimensions(int width, int height) { return false; }

    @Override @Nonnull public RecipeSerializer<?> getSerializer() { return getICSerializer(); }

    @Override @Nonnull public RecipeType<?> getType() { return type; }
}
