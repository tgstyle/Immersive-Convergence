package com.immersiveconvergence.api.multiblock.util;

import com.immersiveconvergence.ImmersiveConvergence;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class MultiblockItemUtils {
    private MultiblockItemUtils() {}

    public static ItemStack getPickBlock(BlockState state) {
        Player player = ImmersiveConvergence.getProxy().getClientPlayer();
        if (player == null) { return ItemStack.EMPTY; }
        return state.getBlock().getCloneItemStack(state, new BlockHitResult(Vec3.ZERO, Direction.DOWN, BlockPos.ZERO, false), player.level(), BlockPos.ZERO, player);
    }

    public static NonNullList<ItemStack> createNonNullItemStackListFromItemStack(ItemStack stack) {
        NonNullList<ItemStack> list = NonNullList.withSize(1, ItemStack.EMPTY);
        list.set(0, stack);
        return list;
    }
}
