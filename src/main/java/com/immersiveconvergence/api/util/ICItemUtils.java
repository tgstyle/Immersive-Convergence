package com.immersiveconvergence.api.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class ICItemUtils {
    public static ItemStack insertStackIntoInventory(Supplier<IItemHandler> ref, ItemStack stack, boolean simulate) { return insertStackIntoInventory(ref.get(), stack, simulate); }

    public static ItemStack insertStackIntoInventory(IItemHandler handler, ItemStack stack, boolean simulate) {
        if (handler == null || stack.isEmpty()) { return stack; }
        return ItemHandlerHelper.insertItem(handler, stack.copy(), simulate);
    }
}
