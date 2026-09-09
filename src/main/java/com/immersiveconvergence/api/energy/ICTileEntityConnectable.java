package com.immersiveconvergence.api.energy;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.TileEntityImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public abstract class ICTileEntityConnectable extends TileEntityImmersiveConnectable {
    @Override @Nonnull public Vec3d getConnectionOffset(@Nonnull Connection con) { return connectionOffset(ICWireType.required(con.cableType), otherEnd(con)); }

    @Override @Nonnull public Vec3d getConnectionOffset(@Nonnull Connection con, TargetingInfo target, Vec3i offsetLink) { return connectionOffset(ICWireType.required(con.cableType), ICTargetingInfo.of(target)); }

    @Nonnull public abstract Vec3d connectionOffset(@Nonnull ICWireType cable, @Nullable BlockPos otherEnd);

    @Nonnull public Vec3d connectionOffset(@Nonnull ICWireType cable, @Nonnull ICTargetingInfo target) { return connectionOffset(cable, (BlockPos)null); }

    @Override public boolean allowEnergyToPass(Connection con) { return allowEnergyToPass(); }

    public boolean allowEnergyToPass() { return true; }

    @Override public boolean canConnectCable(WireType cableType, TargetingInfo target, @Nonnull Vec3i offset) {
        Boolean accepted = canConnectCable(ICWireType.required(cableType), ICTargetingInfo.of(target), offset);
        return accepted != null ? accepted : super.canConnectCable(cableType, target, offset);
    }

    @Nullable public Boolean canConnectCable(@Nonnull ICWireType cable, @Nonnull ICTargetingInfo target, @Nonnull Vec3i offset) { return null; }

    @Override public void connectCable(WireType cableType, TargetingInfo target, IImmersiveConnectable other) {
        if (!connectCable(ICWireType.required(cableType), ICTargetingInfo.of(target), other.getConnectionMaster(cableType, target))) { super.connectCable(cableType, target, other); }
    }

    public boolean connectCable(@Nonnull ICWireType cable, @Nonnull ICTargetingInfo target, BlockPos otherMaster) { return false; }

    @Override public WireType getCableLimiter(@Nonnull TargetingInfo target) {
        ICWireType cable = cableLimiter(ICTargetingInfo.of(target));
        return cable == null ? null : cable.toIE();
    }

    @Nullable public ICWireType cableLimiter(@Nonnull ICTargetingInfo target) { return ICWireType.of(limitType); }

    @Override public void removeCable(Connection connection) {
        if (!removeCable(connection == null ? null : otherEnd(connection), connection == null)) { super.removeCable(connection); }
    }

    public boolean removeCable(@Nullable BlockPos otherEnd, boolean all) { return false; }

    private BlockPos otherEnd(Connection con) { return con.start.equals(pos) ? con.end : con.start; }
}
