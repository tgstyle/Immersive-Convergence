package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.shapes.Shapes;
import com.immersiveconvergence.api.shapes.VoxelShape;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import java.util.List;

import static com.immersiveconvergence.api.shapes.BooleanOp.OR;

public final class MultiblockShapes {
    private static final float[] FULL_BLOCK = {0f, 0f, 0f, 1f, 1f, 1f};

    private MultiblockShapes() {}

    public static BlockPos localPos(int position, int width, int length) {
        int layer = length * width;
        return new BlockPos(position % layer % width, position / layer, position % layer / width);
    }

    public static VoxelShape rotated(List<AxisAlignedBB> local, EnumFacing facing, boolean mirrored) {
        VoxelShape shape = Shapes.empty();
        for (AxisAlignedBB aabb : local) { shape = Shapes.joinUnoptimized(shape, Shapes.create(Shapes.rotateAABB(aabb, facing, mirrored)), OR); }
        return shape.optimize();
    }

    public static float[] blockBounds(VoxelShape shape) {
        if (shape.isEmpty()) { return FULL_BLOCK.clone(); }
        AxisAlignedBB bounds = shape.bounds();
        return new float[]{(float)bounds.minX, (float)bounds.minY, (float)bounds.minZ, (float)bounds.maxX, (float)bounds.maxY, (float)bounds.maxZ};
    }
}
