package com.immersiveconvergence.api.wires;

import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

@SuppressWarnings("unused") public final class ICWireHit {
    private final ICWire wire;
    private final Vec3d point;

    public ICWireHit(@Nonnull ICWire wire, @Nonnull Vec3d point) {
        this.wire = wire;
        this.point = point;
    }

    @Nonnull public ICWire getWire() { return wire; }

    @Nonnull public Vec3d getPoint() { return point; }
}
