package com.immersiveconvergence.api.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import javax.annotation.Nonnull;

@SuppressWarnings("unused") public abstract class ICSerializableRecipe implements Recipe<Container> {
    public static final Lazy<ItemStack> LAZY_EMPTY = of(ItemStack.EMPTY);
    protected final Lazy<ItemStack> outputDummy;
    protected final RecipeType<?> type;
    protected final ResourceLocation id;

    protected <T extends Recipe<?>> ICSerializableRecipe(Lazy<ItemStack> outputDummy, ICRecipeType<T> type, ResourceLocation id) {
        this.outputDummy = outputDummy;
        this.type = type.get();
        this.id = id;
    }

    protected abstract ICRecipeSerializer<?> getICSerializer();

    @Override public boolean isSpecial() { return true; }

    @Override @Nonnull public ItemStack getToastSymbol() { return getICSerializer().getIcon(); }

    @Override public boolean matches(@Nonnull Container inv, @Nonnull Level level) { return false; }

    @Override @Nonnull public ItemStack assemble(@Nonnull Container inv, @Nonnull RegistryAccess access) { return outputDummy.get(); }

    @Override public boolean canCraftInDimensions(int width, int height) { return false; }

    @Override @Nonnull public ResourceLocation getId() { return id; }

    @Override @Nonnull public RecipeSerializer<?> getSerializer() { return getICSerializer(); }

    @Override @Nonnull public RecipeType<?> getType() { return type; }

    public static Lazy<ItemStack> of(ItemStack value) { return Lazy.of(() -> value); }
}
