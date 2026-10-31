package com.immersiveconvergence.api.wires;

import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public record ICWireHit(@Nonnull ICWire wire, @Nonnull Vec3 point) {
    @Nonnull public ICWire getWire() { return wire; }

    @Nonnull public Vec3 getPoint() { return point; }
}
