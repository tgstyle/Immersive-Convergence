package com.immersiveconvergence.client.models;

import com.immersiveconvergence.api.wires.ICWire;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public final class ICWireEnd {
    private final ICWire wire;
    private final boolean start;
    private final int color;

    public ICWireEnd(@Nonnull ICWire wire, boolean start) {
        this.wire = wire;
        this.start = start;
        this.color = wire.getType().colorFor(wire);
    }

    @Nonnull public ICWire getWire() { return wire; }

    public boolean isStart() { return start; }

    public int getColor() { return color; }

    @Nonnull public BlockPos getPos() { return start ? wire.getA().getPos() : wire.getB().getPos(); }

    @Override public boolean equals(Object other) {
        if (this == other) { return true; }
        if (!(other instanceof ICWireEnd)) { return false; }
        ICWireEnd end = (ICWireEnd)other;
        return start == end.start && color == end.color && wire.equals(end.wire);
    }

    @Override public int hashCode() { return 31 * (31 * wire.hashCode() + color) + (start ? 1 : 0); }
}
