package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class ICNBT {
    public static boolean hasKey(ItemStack stack, String key) { return ItemNBTHelper.hasKey(stack, key); }

    public static boolean hasKey(ItemStack stack, String key, int type) { return ItemNBTHelper.hasKey(stack, key, type); }
}
