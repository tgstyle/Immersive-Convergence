package com.immersiveconvergence.common.bridge;

import com.immersiveconvergence.api.bridge.IICMultiblockBridge;
import com.immersiveconvergence.api.multiblock.ICMultiblock;
import com.immersiveconvergence.api.multiblock.ICMultiblockPart;
import com.immersiveconvergence.common.multiblock.IEMultiblockBridge;
import com.immersiveconvergence.common.multiblock.IEMultiblockPartBridge;

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

public final class ICMultiblockBridge implements IICMultiblockBridge {
    @Override public void register(@Nonnull ICMultiblock multiblock) { IEMultiblockBridge.register(multiblock); }

    @Override public boolean exists(@Nonnull String uniqueName) { return IEMultiblockBridge.exists(uniqueName); }

    @Override public boolean formFirstMatching(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side, @Nonnull EntityPlayer player, @Nonnull ItemStack stack, @Nonnull Predicate<String> allowed) { return IEMultiblockBridge.formFirstMatching(world, pos, side, player, stack, allowed); }

    @Override public boolean formationCancelled(@Nonnull EntityPlayer player, @Nonnull ICMultiblock multiblock, @Nonnull BlockPos pos, @Nonnull ItemStack hammer) { return IEMultiblockBridge.formationCancelled(player, multiblock, pos, hammer); }

    @Override public void formationDone(@Nonnull EntityPlayer player, @Nonnull ICMultiblock multiblock, @Nonnull BlockPos pos, @Nonnull ItemStack hammer) { IEMultiblockBridge.formationDone(player, multiblock, pos, hammer); }

    @Override @Nullable public ICMultiblockPart partOf(@Nonnull TileEntity tile) { return IEMultiblockPartBridge.partOf(tile); }

    @Override @Nullable public NonNullList<ItemStack> droppedItemsOf(@Nonnull TileEntity tile) { return IEMultiblockPartBridge.droppedItemsOf(tile); }

    @Override public void readOnPlacement(@Nonnull TileEntity tile, @Nonnull ItemStack stack) { IEMultiblockPartBridge.readOnPlacement(tile, stack); }
}
