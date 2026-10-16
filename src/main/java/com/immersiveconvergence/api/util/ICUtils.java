package com.immersiveconvergence.api.util;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.crafting.builders.ICIEContent;
import com.immersiveconvergence.common.ie.IERecipeBridge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class ICUtils {
    public static final Random RANDOM = new Random();
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();

    public static boolean isHammer(ItemStack stack) { return stack.is(ICIEContent.HAMMERS); }

    public static ItemStack preferredStack(ItemStack[] stacks) {
        if (ICMods.immersiveEngineering()) { return IERecipeBridge.preferredStack(stacks); }
        return Arrays.stream(stacks).min(Comparator.comparing(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()))).orElseThrow(() -> new IllegalArgumentException("Empty array?"));
    }
}
