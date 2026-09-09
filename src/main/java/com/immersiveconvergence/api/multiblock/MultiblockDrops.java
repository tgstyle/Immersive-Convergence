package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.ITileDrop;
import com.immersiveconvergence.api.util.IICInventory;
import com.immersiveconvergence.common.multiblock.IEMultiblockPartBridge;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public final class MultiblockDrops {

    private MultiblockDrops() {}

    @Nullable public static NonNullList<ItemStack> droppedItemsOf(@Nullable TileEntity tile) {
        if (tile instanceof IICInventory) {
            if (tile instanceof ITileDrop && ((ITileDrop)tile).preventInventoryDrop()) { return null; }
            return ((IICInventory)tile).getDroppedItems();
        }
        return tile != null && ICMods.immersiveEngineering() ? IEMultiblockPartBridge.droppedItemsOf(tile) : null;
    }

    public static void dropMasterInventory(World world, BlockPos pos, ICMultiblockPart tile) {
        if (tile.isPartUnformed()) { return; }
        NonNullList<ItemStack> dropped = droppedItemsOf(tile.getPartMaster());
        if (dropped == null) { return; }
        for (ItemStack s : dropped) {
            if (!s.isEmpty()) { world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, s.copy())); }
        }
    }
}
