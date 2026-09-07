package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.ITileDrop;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class MultiblockDrops {

    private MultiblockDrops() {}

    public static void dropMasterInventory(World world, BlockPos pos, TileEntityMultiblockPart<?> tile) {
        if (!tile.formed) { return; }
        Object master = tile.master();
        if (!(master instanceof IIEInventory)) { return; }
        if (master instanceof ITileDrop && ((ITileDrop) master).preventInventoryDrop()) { return; }
        NonNullList<ItemStack> dropped = ((IIEInventory) master).getDroppedItems();
        if (dropped == null) { return; }
        for (ItemStack s : dropped) {
            if (!s.isEmpty()) { world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, s.copy())); }
        }
    }
}
