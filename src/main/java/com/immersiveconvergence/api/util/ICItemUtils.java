package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class ICItemUtils {
    public static ItemStack insertStackIntoInventory(CapabilityReference<IItemHandler> to, ItemStack stack, boolean simulate) { return insertStackIntoInventory(to.getNullable(), stack, simulate); }

    public static ItemStack insertStackIntoInventory(IItemHandler handler, ItemStack stack, boolean simulate) {
        if (handler == null || stack.isEmpty()) { return stack; }
        return ItemHandlerHelper.insertItem(handler, stack.copy(), simulate);
    }
}
