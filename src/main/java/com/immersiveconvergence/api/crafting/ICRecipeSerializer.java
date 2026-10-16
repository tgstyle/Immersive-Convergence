package com.immersiveconvergence.api.crafting;

import com.mojang.serialization.MapCodec;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import javax.annotation.Nonnull;

public abstract class ICRecipeSerializer<R extends Recipe<?>> implements RecipeSerializer<R> {
    public abstract ItemStack getIcon();

    protected abstract DualMapCodec<RegistryFriendlyByteBuf, R> codecs();

    @Override @Nonnull public final MapCodec<R> codec() { return codecs().mapCodec(); }

    @Override @Nonnull public final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() { return codecs().streamCodec(); }
}
