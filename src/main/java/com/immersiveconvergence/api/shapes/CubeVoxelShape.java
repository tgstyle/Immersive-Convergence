package com.immersiveconvergence.api.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

public final class CubeVoxelShape extends VoxelShape {
    CubeVoxelShape(DiscreteVoxelShape pShape) {
        super(pShape);
    }

    protected DoubleList getCoords(EnumFacing.Axis pAxis) {
        return new CubePointRange(this.shape.getSize(pAxis));
    }

    protected int findIndex(EnumFacing.Axis pAxis, double pPosition) {
        int i = this.shape.getSize(pAxis);
        return MathHelper.floor(MathHelper.clamp(pPosition * (double)i, -1.0D, i));
    }
}
