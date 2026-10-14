package com.immersiveconvergence.api.crafting;

import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.utils.codec.IECodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import malte0811.dualcodecs.DualCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

@SuppressWarnings("unused") public class ICTagOutput extends TagOutput {
    public static final ICTagOutput EMPTY = new ICTagOutput(Either.left(new ICIngredientWithSize(Ingredient.EMPTY)));
    public static final DualCodec<RegistryFriendlyByteBuf, ICTagOutput> CODECS = new DualCodec<>(
            Codec.either(ICIngredientWithSize.CODEC, IECodecs.ITEM_STACK_COUNT_OPTIONAL).xmap(ICTagOutput::new, out -> out.data),
            ItemStack.OPTIONAL_STREAM_CODEC.map(ICTagOutput::new, TagOutput::get)
    );
    private final Either<ICIngredientWithSize, ItemStack> data;

    public ICTagOutput(Either<ICIngredientWithSize, ItemStack> data) {
        super(data.mapLeft(i -> i));
        this.data = data;
    }

    public ICTagOutput(ItemLike item) { this(new ItemStack(item)); }

    public ICTagOutput(ItemStack stack) { this(Either.right(stack)); }

    public ICTagOutput(TagKey<Item> tag, int count) { this(Either.left(new ICIngredientWithSize(Ingredient.of(tag), count))); }

    static ICTagOutput copyOf(TagOutput output) { return new ICTagOutput(output.get()); }
}
