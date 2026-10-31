package com.immersiveconvergence.api.wires;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public record ICConnectionPoint(@Nonnull BlockPos position, int index) implements Comparable<ICConnectionPoint> {
    public ICConnectionPoint {
        position = position.immutable();
    }

    @Nonnull public ChunkPos chunk() { return new ChunkPos(position); }

    @Override public int compareTo(@Nonnull ICConnectionPoint other) {
        int byPos = Long.compare(position.asLong(), other.position.asLong());
        return byPos != 0 ? byPos : Integer.compare(index, other.index);
    }

    @Nonnull @Override public String toString() { return position.getX() + "," + position.getY() + "," + position.getZ() + "#" + index; }
}
