package com.immersiveconvergence.api.util;

import net.minecraft.world.item.ItemStack;

public class ICNBT {
    public static boolean hasKey(ItemStack stack, String key) { return stack.hasTag() && stack.getOrCreateTag().contains(key); }

    public static boolean hasKey(ItemStack stack, String key, int type) { return stack.hasTag() && stack.getOrCreateTag().contains(key, type); }
}
