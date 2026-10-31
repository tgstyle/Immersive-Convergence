package com.immersiveconvergence.common.bridge;

import com.immersiveconvergence.api.bridge.IICTileBridge;
import com.immersiveconvergence.common.block.IETileBridge;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public final class ICTileBridge implements IICTileBridge {
    @Override @Nonnull public IUnlistedProperty<?> objCallbackProperty() { return IETileBridge.objCallbackProperty(); }

    @Override @Nonnull public IUnlistedProperty<?> connectionsProperty() { return IETileBridge.connectionsProperty(); }

    @Override public void removeStateFromSmartModelCache(@Nonnull IExtendedBlockState state) { IETileBridge.removeStateFromSmartModelCache(state); }

    @Override public double renderDistanceMultiplier() { return IETileBridge.renderDistanceMultiplier(); }

    @Override @Nullable public NonNullList<ItemStack> droppedItems(@Nonnull TileEntity tile) { return IETileBridge.droppedItems(tile); }

    @Override public boolean isInventoryHandler(@Nonnull IItemHandler handler) { return IETileBridge.isInventoryHandler(handler); }

    @Override public void clearConnections(@Nonnull TileEntity tile, @Nonnull World world, boolean dropWires) { IETileBridge.clearConnections(tile, world, dropWires); }

    @Override public void onEntityCollision(@Nonnull TileEntity tile, @Nonnull World world, @Nonnull Entity entity) { IETileBridge.onEntityCollision(tile, world, entity); }

    @Override @Nonnull public IExtendedBlockState extendState(@Nonnull IExtendedBlockState extended, @Nonnull TileEntity tile) { return IETileBridge.extendState(extended, tile); }

    @Override @Nullable public Set<?> wireConnections(@Nonnull TileEntity tile) { return IETileBridge.wireConnections(tile); }
}
