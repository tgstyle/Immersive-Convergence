package com.immersiveconvergence.api.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public final class ICDimensionBlockPos extends BlockPos {
    public final int dimension;

    public ICDimensionBlockPos(int x, int y, int z, int dimension) {
        super(x, y, z);
        this.dimension = dimension;
    }

    public ICDimensionBlockPos(int x, int y, int z, World world) { this(x, y, z, world.provider.getDimension()); }

    public ICDimensionBlockPos(BlockPos pos, World world) { this(pos.getX(), pos.getY(), pos.getZ(), world.provider.getDimension()); }

    public ICDimensionBlockPos(BlockPos pos, int dimension) { this(pos.getX(), pos.getY(), pos.getZ(), dimension); }

    public ICDimensionBlockPos(TileEntity tile) { this(tile.getPos(), tile.getWorld()); }

    @Override public int hashCode() {
        int result = 1;
        result = 31 * result + dimension;
        result = 31 * result + getX();
        result = 31 * result + getY();
        result = 31 * result + getZ();
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (!(obj instanceof ICDimensionBlockPos)) { return false; }
        ICDimensionBlockPos other = (ICDimensionBlockPos)obj;
        return dimension == other.dimension && getX() == other.getX() && getY() == other.getY() && getZ() == other.getZ();
    }

    @Override @Nonnull public String toString() { return "Dimension: " + dimension + " Pos: " + super.toString(); }
}
