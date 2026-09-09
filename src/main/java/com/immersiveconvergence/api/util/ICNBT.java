package com.immersiveconvergence.api.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

@SuppressWarnings("unused")
public class ICNBT {
    private static boolean hasTag(ItemStack stack) { return !stack.isEmpty() && stack.hasTagCompound(); }

    public static boolean hasKey(ItemStack stack, String key) { return hasTag(stack) && getTag(stack).hasKey(key); }

    public static NBTTagCompound getTag(ItemStack stack) {
        if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); }
        return stack.getTagCompound();
    }

    public static NBTTagCompound getTagCompound(ItemStack stack, String key) { return hasTag(stack) ? getTag(stack).getCompoundTag(key) : new NBTTagCompound(); }

    public static int getInt(ItemStack stack, String key) { return hasTag(stack) ? getTag(stack).getInteger(key) : 0; }
}
