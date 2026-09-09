package com.immersiveconvergence.api.energy;

import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityConnectorRedstone;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings({"unused", "deprecation"})
public class ICTileEntityConnectorRedstone extends TileEntityConnectorRedstone {
    @Override public Vec3d getRaytraceOffset(IImmersiveConnectable link) {
        Vec3d offset = raytraceOffset();
        return offset != null ? offset : super.getRaytraceOffset(link);
    }

    @Nullable public Vec3d raytraceOffset() { return null; }

    @Override @Nonnull public Vec3d getConnectionOffset(@Nonnull Connection con) {
        Vec3d offset = connectionOffset(ICWireType.required(con.cableType));
        return offset != null ? offset : super.getConnectionOffset(con);
    }

    @Nullable public Vec3d connectionOffset(@Nonnull ICWireType cable) { return null; }
}
