package com.immersiveconvergence.api.wires;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nonnull;

@SuppressWarnings("unused") public final class ICConnectionPoint implements Comparable<ICConnectionPoint> {
    private final BlockPos pos;
    private final int index;
    private final int hash;

    public ICConnectionPoint(@Nonnull BlockPos pos, int index) {
        this.pos = pos.toImmutable();
        this.index = index;
        long mixed = (this.pos.toLong() ^ (this.pos.toLong() >>> 32)) * 0x9E3779B97F4A7C15L + index;
        mixed = (mixed ^ (mixed >>> 29)) * 0xBF58476D1CE4E5B9L;
        this.hash = Long.hashCode(mixed ^ (mixed >>> 32));
    }

    @Nonnull public BlockPos getPos() { return pos; }

    public int getIndex() { return index; }

    @Nonnull public ChunkPos getChunk() { return new ChunkPos(pos); }

    @Override public int compareTo(@Nonnull ICConnectionPoint other) {
        int byPos = Long.compare(pos.toLong(), other.pos.toLong());
        return byPos != 0 ? byPos : Integer.compare(index, other.index);
    }

    @Override public boolean equals(Object other) {
        if (this == other) { return true; }
        if (!(other instanceof ICConnectionPoint)) { return false; }
        ICConnectionPoint point = (ICConnectionPoint)other;
        return index == point.index && pos.equals(point.pos);
    }

    @Override public int hashCode() { return hash; }

    @Override public String toString() { return pos.getX() + "," + pos.getY() + "," + pos.getZ() + "#" + index; }
}
