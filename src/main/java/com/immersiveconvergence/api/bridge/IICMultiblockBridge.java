package com.immersiveconvergence.api.bridge;

import com.immersiveconvergence.api.multiblock.MultiblockHandler.IMultiblock;

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public interface IICMultiblockBridge {
    void register(@Nonnull IMultiblock multiblock);

    @Nullable Component displayName(@Nonnull ResourceLocation name);

    boolean formFirstMatching(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Direction side, @Nullable Player player, @Nonnull ItemStack tool, @Nonnull Predicate<ResourceLocation> allowed);

    @Nonnull ItemStack pickBlock(@Nonnull BlockState state);

    boolean isPartBlock(@Nonnull Block block);

    boolean isStrayDummy(@Nullable BlockEntity part, @Nonnull BlockPos cell);

    void markDisassembling(@Nullable BlockEntity be);
}
