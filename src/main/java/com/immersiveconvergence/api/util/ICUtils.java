package com.immersiveconvergence.api.util;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.multiblock.util.SafeChunkUtils;
import com.immersiveconvergence.common.ie.IERecipeBridge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class ICUtils {
    public static final Random RANDOM = new Random();
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();
    private static final TagKey<Item> HAMMERS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ICMods.IMMERSIVE_ENGINEERING, "tools/hammers"));

    public static boolean isHammer(ItemStack stack) { return stack.is(HAMMERS); }

    public static boolean isChunkSafe(LevelAccessor w, BlockPos pos) { return !SafeChunkUtils.isChunkUnsafe(w, pos); }

    public static ItemStack preferredStack(ItemStack[] stacks) {
        if (ICMods.immersiveEngineering()) { return IERecipeBridge.preferredStack(stacks); }
        return Arrays.stream(stacks).min(Comparator.comparing(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()))).orElseThrow(() -> new IllegalArgumentException("Empty array?"));
    }
}
