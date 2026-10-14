package com.immersiveconvergence.api.multiblock.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;

public abstract class InsertOnlyInventory implements IItemHandler {
    @Override public int getSlots() { return 1; }

    @Override public final @Nonnull ItemStack getStackInSlot(int slot) { return ItemStack.EMPTY; }

    @Override public final @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { return insert(stack, simulate); }

    @Override public final @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }

    @Override public final int getSlotLimit(int slot) { return 64; }

    @Override public final boolean isItemValid(int slot, @Nonnull ItemStack stack) { return true; }

    protected abstract ItemStack insert(ItemStack toInsert, boolean simulate);
}
