package com.immersiveconvergence.api.wires;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import javax.annotation.Nonnull;

@SuppressWarnings("unused") public final class ICConnectionPoint implements Comparable<ICConnectionPoint> {
    private final BlockPos position;
    private final int index;
    private final int hash;

    public ICConnectionPoint(@Nonnull BlockPos position, int index) {
        this.position = position.immutable();
        this.index = index;
        long mixed = (this.position.asLong() ^ (this.position.asLong() >>> 32)) * 0x9E3779B97F4A7C15L + index;
        mixed = (mixed ^ (mixed >>> 29)) * 0xBF58476D1CE4E5B9L;
        this.hash = Long.hashCode(mixed ^ (mixed >>> 32));
    }

    @Nonnull public BlockPos position() { return position; }

    public int index() { return index; }

    @Nonnull public ChunkPos chunk() { return new ChunkPos(position); }

    @Override public int compareTo(@Nonnull ICConnectionPoint other) {
        int byPos = Long.compare(position.asLong(), other.position.asLong());
        return byPos != 0 ? byPos : Integer.compare(index, other.index);
    }

    @Override public boolean equals(Object other) {
        if (this == other) { return true; }
        if (!(other instanceof ICConnectionPoint point)) { return false; }
        return index == point.index && position.equals(point.position);
    }

    @Override public int hashCode() { return hash; }

    @Nonnull @Override public String toString() { return position.getX() + "," + position.getY() + "," + position.getZ() + "#" + index; }
}
