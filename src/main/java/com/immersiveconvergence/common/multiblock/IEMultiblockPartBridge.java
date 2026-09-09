package com.immersiveconvergence.common.multiblock;

import com.immersiveconvergence.api.multiblock.ICMultiblockPart;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.ITileDrop;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public final class IEMultiblockPartBridge {
    private IEMultiblockPartBridge() {}

    @Nullable public static ICMultiblockPart partOf(TileEntity tile) { return tile instanceof TileEntityMultiblockPart ? new Adapter((TileEntityMultiblockPart<?>)tile) : null; }

    @Nullable public static NonNullList<ItemStack> droppedItemsOf(TileEntity tile) {
        if (!(tile instanceof IIEInventory)) { return null; }
        if (tile instanceof ITileDrop && ((ITileDrop)tile).preventInventoryDrop()) { return null; }
        return ((IIEInventory)tile).getDroppedItems();
    }

    public static void readOnPlacement(TileEntity tile, ItemStack stack) {
        if (tile instanceof ITileDrop) { ((ITileDrop)tile).readOnPlacement(null, stack); }
    }

    private static final class Adapter implements ICMultiblockPart {
        private final TileEntityMultiblockPart<?> part;

        private Adapter(TileEntityMultiblockPart<?> part) { this.part = part; }

        @Override public World getWorld() { return part.getWorld(); }

        @Override public BlockPos getPos() { return part.getPos(); }

        @Override public boolean isPartUnformed() { return !part.formed; }

        @Override public void unformPart() { part.formed = false; }

        @Override public int[] getPartOffset() { return part.offset; }

        @Override public EnumFacing getPartFacing() { return part.facing; }

        @Override public boolean isPartMirrored() { return part.mirrored; }

        @Override public long getPartDisassemblyTime() { return part.onlyLocalDissassembly; }

        @Override public BlockPos getPartOrigin() { return part.getOrigin(); }

        @Override public ItemStack getPartOriginalBlock() { return part.getOriginalBlock(); }

        @Override @Nullable public TileEntity getPartMaster() { return part.master(); }
    }
}
