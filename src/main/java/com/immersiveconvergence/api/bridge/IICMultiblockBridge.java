package com.immersiveconvergence.api.bridge;

import com.immersiveconvergence.api.multiblock.ICMultiblock;
import com.immersiveconvergence.api.multiblock.ICMultiblockPart;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public interface IICMultiblockBridge {
    void register(@Nonnull ICMultiblock multiblock);

    boolean exists(@Nonnull String uniqueName);

    boolean formFirstMatching(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side, @Nonnull EntityPlayer player, @Nonnull ItemStack stack, @Nonnull Predicate<String> allowed);

    boolean formationCancelled(@Nonnull EntityPlayer player, @Nonnull ICMultiblock multiblock, @Nonnull BlockPos pos, @Nonnull ItemStack hammer);

    void formationDone(@Nonnull EntityPlayer player, @Nonnull ICMultiblock multiblock, @Nonnull BlockPos pos, @Nonnull ItemStack hammer);

    @Nullable ICMultiblockPart partOf(@Nonnull TileEntity tile);

    @Nullable NonNullList<ItemStack> droppedItemsOf(@Nonnull TileEntity tile);

    void readOnPlacement(@Nonnull TileEntity tile, @Nonnull ItemStack stack);
}
