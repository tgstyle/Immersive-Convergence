package com.immersiveconvergence.api.multiblock.util;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.common.ie.IEMultiblockBridge;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class MultiblockItemUtils {
    private MultiblockItemUtils() {}

    public static ItemStack getPickBlock(BlockState state) {
        if (ICMods.immersiveEngineering()) { return IEMultiblockBridge.pickBlock(state); }
        return new ItemStack(state.getBlock());
    }
}
