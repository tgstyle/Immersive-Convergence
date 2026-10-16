package com.immersiveconvergence.api.crafting;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import malte0811.dualcodecs.DualCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Predicate;

public class ICIngredientWithSize implements Predicate<ItemStack> {
    private static final Codec<ICIngredientWithSize> COUNT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Ingredient.CODEC.fieldOf("basePredicate").forGetter(ICIngredientWithSize::getBaseIngredient),
            Codec.INT.fieldOf("count").forGetter(ICIngredientWithSize::getCount)
    ).apply(inst, ICIngredientWithSize::new));
    private static final Codec<ICIngredientWithSize> SINGLE_CODEC = Ingredient.CODEC.xmap(ICIngredientWithSize::new, ICIngredientWithSize::getBaseIngredient);
    public static final Codec<ICIngredientWithSize> CODEC = Codec.either(COUNT_CODEC, SINGLE_CODEC).xmap(
            e -> e.map(Function.identity(), Function.identity()),
            iws -> iws.getCount() == 1 ? Either.right(iws) : Either.left(iws)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ICIngredientWithSize> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, ICIngredientWithSize::getBaseIngredient,
            ByteBufCodecs.INT, ICIngredientWithSize::getCount,
            ICIngredientWithSize::new
    );
    public static final DualCodec<RegistryFriendlyByteBuf, ICIngredientWithSize> CODECS = new DualCodec<>(CODEC, STREAM_CODEC);
    protected final Ingredient basePredicate;
    protected final int count;

    public ICIngredientWithSize(Ingredient basePredicate, int count) {
        this.basePredicate = basePredicate;
        this.count = count;
    }

    public ICIngredientWithSize(Ingredient basePredicate) { this(basePredicate, 1); }

    @Override public boolean test(@Nullable ItemStack stack) {
        if (stack == null) { return false; }
        return basePredicate.test(stack) && stack.getCount() >= count;
    }

    public boolean testIgnoringSize(ItemStack stack) { return basePredicate.test(stack); }

    @Nonnull public ItemStack[] getMatchingStacks() {
        ItemStack[] baseStacks = basePredicate.getItems();
        ItemStack[] ret = new ItemStack[baseStacks.length];
        for (int i = 0; i < baseStacks.length; ++i) { ret[i] = baseStacks[i].copyWithCount(count); }
        return ret;
    }

    public boolean hasNoMatchingItems() { return basePredicate.isEmpty(); }

    public int getCount() { return count; }

    public Ingredient getBaseIngredient() { return basePredicate; }
}
