package com.immersiveconvergence.api.energy;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.block.BaseBlockEntity;
import com.immersiveconvergence.common.ie.IEWireBridge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({"unused", "RedundantSuppression"}) public abstract class ICConnectableBlockEntity extends BaseBlockEntity {
    public ICConnectableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    public boolean canConnect() { return false; }

    @Nonnull public BlockPos connectionMaster(@Nullable ICWireType cable, @Nonnull ICTargetingInfo target) { return worldPosition; }

    public boolean acceptsCable(@Nonnull ICWireType cable, @Nonnull ICConnectionPoint target, @Nonnull Vec3i offset) { return false; }

    public void cableConnected(@Nonnull ICWireType cable, @Nonnull ICConnectionPoint target) {}

    @Nullable public ICConnectionPoint targetedPoint(@Nonnull ICTargetingInfo target, @Nonnull Vec3i offset) { return new ICConnectionPoint(worldPosition, 0); }

    public void cableRemoved(@Nullable ICWireType cable, @Nonnull ICConnectionPoint attachedPoint) { setChanged(); }

    @Nonnull public Vec3 connectionOffset(@Nonnull ICConnectionPoint here, @Nonnull ICConnectionPoint other, @Nonnull ICWireType cable) { return new Vec3(0.5, 0.5, 0.5); }

    @Nonnull public Collection<ICConnectionPoint> connectionPoints() { return List.of(new ICConnectionPoint(worldPosition, 0)); }

    protected boolean hasExternalConnections(@Nonnull ICConnectionPoint cp) { return level != null && ICMods.immersiveEngineering() && IEWireBridge.hasExternalConnections(level, cp); }
}
