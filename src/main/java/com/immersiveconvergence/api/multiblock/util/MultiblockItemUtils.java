package com.immersiveconvergence.api.multiblock.util;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.common.ie.IEMultiblockBridge;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class MultiblockItemUtils {
    private MultiblockItemUtils() {}

    public static ItemStack getPickBlock(BlockState state) {
        if (ICMods.immersiveEngineering()) { return IEMultiblockBridge.pickBlock(state); }
        return new ItemStack(state.getBlock());
    }

    public static NonNullList<ItemStack> createNonNullItemStackListFromItemStack(ItemStack stack) {
        NonNullList<ItemStack> list = NonNullList.withSize(1, ItemStack.EMPTY);
        list.set(0, stack);
        return list;
    }

    public static boolean compareItemNBT(ItemStack stack1, ItemStack stack2) {
        if (stack1.isEmpty() != stack2.isEmpty()) { return false; }
        boolean hasTag1 = stack1.hasTag();
        if (hasTag1 != stack2.hasTag()) { return false; }
        if (hasTag1 && !stack1.getOrCreateTag().equals(stack2.getOrCreateTag())) { return false; }
        return stack1.areCapsCompatible(stack2);
    }
}
