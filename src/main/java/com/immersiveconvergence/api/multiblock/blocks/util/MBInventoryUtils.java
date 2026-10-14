package com.immersiveconvergence.api.multiblock.blocks.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MBInventoryUtils {
    public static void dropItems(IItemHandlerModifiable inv, Consumer<ItemStack> drop) {
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            final ItemStack stack = inv.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                drop.accept(stack.copy());
                inv.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }
    }
}
