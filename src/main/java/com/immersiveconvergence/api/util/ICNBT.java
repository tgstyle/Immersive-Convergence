package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

@SuppressWarnings("unused")
public class ICNBT {
    public static boolean hasKey(ItemStack stack, String key) { return ItemNBTHelper.hasKey(stack, key); }

    public static NBTTagCompound getTag(ItemStack stack) { return ItemNBTHelper.getTag(stack); }

    public static NBTTagCompound getTagCompound(ItemStack stack, String key) { return ItemNBTHelper.getTagCompound(stack, key); }

    public static int getInt(ItemStack stack, String key) { return ItemNBTHelper.getInt(stack, key); }
}
