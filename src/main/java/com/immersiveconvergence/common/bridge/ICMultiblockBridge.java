package com.immersiveconvergence.common.bridge;

import com.immersiveconvergence.api.bridge.IICMultiblockBridge;
import com.immersiveconvergence.api.multiblock.MultiblockHandler.IMultiblock;
import com.immersiveconvergence.core.integration.immersiveengineering.IEMultiblockBridge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ICMultiblockBridge implements IICMultiblockBridge {
    @Override public void register(@Nonnull IMultiblock multiblock) { IEMultiblockBridge.register(multiblock); }

    @Override @Nullable public Component displayName(@Nonnull ResourceLocation name) { return IEMultiblockBridge.displayName(name); }

    @Override public boolean formFirstMatching(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Direction side, @Nullable Player player, @Nonnull ItemStack tool, @Nonnull Predicate<ResourceLocation> allowed) { return IEMultiblockBridge.formFirstMatching(level, pos, side, player, tool, allowed); }

    @Override @Nonnull public ItemStack pickBlock(@Nonnull BlockState state) { return IEMultiblockBridge.pickBlock(state); }

    @Override public boolean isPartBlock(@Nonnull Block block) { return IEMultiblockBridge.isPartBlock(block); }

    @Override public boolean isStrayDummy(@Nullable BlockEntity part, @Nonnull BlockPos cell) { return IEMultiblockBridge.isStrayDummy(part, cell); }

    @Override public void markDisassembling(@Nullable BlockEntity be) { IEMultiblockBridge.markDisassembling(be); }
}
