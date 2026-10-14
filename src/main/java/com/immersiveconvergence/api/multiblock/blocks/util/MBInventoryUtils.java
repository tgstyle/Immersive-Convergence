package com.immersiveconvergence.api.multiblock.blocks.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MBInventoryUtils {
    public static void dropItems(IItemHandler inv, Consumer<ItemStack> drop) {
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            final ItemStack stack = inv.getStackInSlot(slot);
            if (!stack.isEmpty()) { drop.accept(stack.copy()); }
        }
    }
}
