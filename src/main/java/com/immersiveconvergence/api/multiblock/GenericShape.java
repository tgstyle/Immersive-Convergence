package com.immersiveconvergence.api.multiblock;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import java.util.List;

@SuppressWarnings("unused")
public abstract class GenericShape {
    public final int width, height, length;

    protected GenericShape(int w, int h, int l) {
        this.width = w; this.height = h; this.length = l;
    }

    public abstract List<AxisAlignedBB> getShape(BlockPos posInMultiblock);
}
