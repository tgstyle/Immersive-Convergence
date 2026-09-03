package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.shapes.Shapes;
import com.immersiveconvergence.api.shapes.VoxelShape;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import java.util.List;

import static com.immersiveconvergence.api.shapes.BooleanOp.OR;

public final class MultiblockShapes {
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

    public static List<AxisAlignedBB> bounds(List<AxisAlignedBB> local, EnumFacing facing, boolean mirrored) {
        List<AxisAlignedBB> solid = new ArrayList<>();
        List<AxisAlignedBB> planes = new ArrayList<>();
        for (AxisAlignedBB aabb : local) {
            if (isPlane(aabb)) { planes.add(aabb); }
            else { solid.add(aabb); }
        }
        List<AxisAlignedBB> bounds = new ArrayList<>(rotated(solid, facing, mirrored).toAabbs());
        for (AxisAlignedBB plane : planes) { bounds.add(Shapes.rotateAABB(plane, facing, mirrored)); }
        return bounds;
    }

    public static boolean isPlane(AxisAlignedBB aabb) {
        int flat = 0;
        if (aabb.maxX - aabb.minX < 1.0E-7D) { flat++; }
        if (aabb.maxY - aabb.minY < 1.0E-7D) { flat++; }
        if (aabb.maxZ - aabb.minZ < 1.0E-7D) { flat++; }
        return flat == 1;
    }

    public static float[] blockBounds(List<AxisAlignedBB> bounds) {
        if (bounds.isEmpty()) { return new float[6]; }
        AxisAlignedBB union = bounds.get(0);
        for (AxisAlignedBB aabb : bounds) { union = union.union(aabb); }
        return new float[]{(float)union.minX, (float)union.minY, (float)union.minZ, (float)union.maxX, (float)union.maxY, (float)union.maxZ};
    }
}
