package com.immersiveconvergence.api.wires;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public final class ICConnectionPoint implements Comparable<ICConnectionPoint> {
    private final BlockPos pos;
    private final int index;

    public ICConnectionPoint(@Nonnull BlockPos pos, int index) {
        this.pos = pos.toImmutable();
        this.index = index;
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

    @Override public int hashCode() { return 31 * pos.hashCode() + index; }

    @Override public String toString() { return pos.getX() + "," + pos.getY() + "," + pos.getZ() + "#" + index; }
}
