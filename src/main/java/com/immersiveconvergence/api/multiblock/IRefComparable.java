package com.immersiveconvergence.api.multiblock;

import net.minecraft.item.ItemStack;

@SuppressWarnings("unused")
public interface IRefComparable {
    boolean isEquals(ItemStack toCompare);

    ItemStack toItemStack();
}
