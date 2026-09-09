package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.common.multiblock.IEMultiblockPartBridge;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface ICMultiblockPart {
    @Nullable static ICMultiblockPart of(@Nullable TileEntity tile) {
        if (tile instanceof ICMultiblockPart) { return (ICMultiblockPart)tile; }
        return tile != null && ICMods.immersiveEngineering() ? IEMultiblockPartBridge.partOf(tile) : null;
    }

    World getWorld();

    BlockPos getPos();

    boolean isPartUnformed();

    void unformPart();

    int[] getPartOffset();

    EnumFacing getPartFacing();

    boolean isPartMirrored();

    long getPartDisassemblyTime();

    BlockPos getPartOrigin();

    ItemStack getPartOriginalBlock();

    @Nullable TileEntity getPartMaster();
}
