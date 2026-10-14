package com.immersiveconvergence.api.crafting;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import malte0811.dualcodecs.DualCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Function;

@SuppressWarnings("unused") public class ICIngredientWithSize extends IngredientWithSize {
    private static final Codec<ICIngredientWithSize> COUNT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Ingredient.CODEC.fieldOf("basePredicate").forGetter(IngredientWithSize::getBaseIngredient),
            Codec.INT.fieldOf("count").forGetter(IngredientWithSize::getCount)
    ).apply(inst, ICIngredientWithSize::new));
    private static final Codec<ICIngredientWithSize> SINGLE_CODEC = Ingredient.CODEC.xmap(ICIngredientWithSize::new, IngredientWithSize::getBaseIngredient);
    public static final Codec<ICIngredientWithSize> CODEC = Codec.either(COUNT_CODEC, SINGLE_CODEC).xmap(
            e -> e.map(Function.identity(), Function.identity()),
            iws -> iws.getCount() == 1 ? Either.right(iws) : Either.left(iws)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ICIngredientWithSize> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, IngredientWithSize::getBaseIngredient,
            ByteBufCodecs.INT, IngredientWithSize::getCount,
            ICIngredientWithSize::new
    );
    public static final DualCodec<RegistryFriendlyByteBuf, ICIngredientWithSize> CODECS = new DualCodec<>(CODEC, STREAM_CODEC);

    public ICIngredientWithSize(Ingredient basePredicate, int count) { super(basePredicate, count); }

    public ICIngredientWithSize(Ingredient basePredicate) { super(basePredicate); }

    @Override public ICIngredientWithSize withSize(int size) { return new ICIngredientWithSize(getBaseIngredient(), size); }

    static ICIngredientWithSize copyOf(IngredientWithSize ingredient) { return ingredient instanceof ICIngredientWithSize ic ? ic : new ICIngredientWithSize(ingredient.getBaseIngredient(), ingredient.getCount()); }
}
