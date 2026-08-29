package com.immersiveconvergence.api.shapes;

import com.immersiveconvergence.common.util.ICMth;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.EnumFacing;

public final class CubeVoxelShape extends VoxelShape {
    CubeVoxelShape(DiscreteVoxelShape pShape) {
        super(pShape);
    }

    protected DoubleList getCoords(EnumFacing.Axis pAxis) {
        return new CubePointRange(this.shape.getSize(pAxis));
    }

    protected int findIndex(EnumFacing.Axis pAxis, double pPosition) {
        int i = this.shape.getSize(pAxis);
        return ICMth.floor(ICMth.clamp(pPosition * (double)i, -1.0D, i));
    }
}
