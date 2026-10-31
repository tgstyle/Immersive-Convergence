package com.immersiveconvergence.api.bridge;

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

public interface IICTileBridge {
    @Nonnull IUnlistedProperty<?> objCallbackProperty();

    @Nonnull IUnlistedProperty<?> connectionsProperty();

    void removeStateFromSmartModelCache(@Nonnull IExtendedBlockState state);

    double renderDistanceMultiplier();

    @Nullable NonNullList<ItemStack> droppedItems(@Nonnull TileEntity tile);

    boolean isInventoryHandler(@Nonnull IItemHandler handler);

    void clearConnections(@Nonnull TileEntity tile, @Nonnull World world, boolean dropWires);

    void onEntityCollision(@Nonnull TileEntity tile, @Nonnull World world, @Nonnull Entity entity);

    @Nonnull IExtendedBlockState extendState(@Nonnull IExtendedBlockState extended, @Nonnull TileEntity tile);

    @Nullable Set<?> wireConnections(@Nonnull TileEntity tile);
}
