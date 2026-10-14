package com.immersiveconvergence.api.crafting;

import com.immersiveconvergence.api.util.ICUtils;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import malte0811.dualcodecs.DualCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.function.Function;

@SuppressWarnings("unused") public class ICTagOutput {
    private static final Codec<ItemStack> ITEM_STACK_COUNT_OPTIONAL = Codec.either(ItemStack.CODEC, ItemStack.SINGLE_ITEM_CODEC).xmap(
            either -> either.map(Function.identity(), Function.identity()),
            stack -> stack.getCount() == 1 ? Either.right(stack) : Either.left(stack)
    );
    public static final ICTagOutput EMPTY = new ICTagOutput(new ICIngredientWithSize(Ingredient.EMPTY));
    public static final DualCodec<RegistryFriendlyByteBuf, ICTagOutput> CODECS = new DualCodec<>(
            Codec.either(ICIngredientWithSize.CODEC, ITEM_STACK_COUNT_OPTIONAL).xmap(ICTagOutput::new, out -> out.rawData),
            ItemStack.OPTIONAL_STREAM_CODEC.map(ICTagOutput::new, ICTagOutput::get)
    );
    private final Either<ICIngredientWithSize, ItemStack> rawData;
    @Nullable private ItemStack cachedStack;

    public ICTagOutput(Either<ICIngredientWithSize, ItemStack> rawData) { this.rawData = rawData; }

    public ICTagOutput(ItemLike item) { this(item, 1); }

    public ICTagOutput(ItemLike item, int count) { this(new ItemStack(item, count)); }

    public ICTagOutput(ICIngredientWithSize ingredient) { this(Either.left(ingredient)); }

    public ICTagOutput(ItemStack stack) { this(Either.right(stack)); }

    public ICTagOutput(TagKey<Item> tag, int count) { this(new ICIngredientWithSize(tag, count)); }

    public ICTagOutput(TagKey<Item> tag) { this(tag, 1); }

    public ItemStack get() {
        if (cachedStack == null) { cachedStack = rawData.map(iws -> iws.hasNoMatchingItems() ? ItemStack.EMPTY : ICUtils.preferredStack(iws.getMatchingStacks()), Function.identity()); }
        return cachedStack;
    }
}
