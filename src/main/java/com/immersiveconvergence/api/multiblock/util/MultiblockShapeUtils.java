package com.immersiveconvergence.api.multiblock.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class MultiblockShapeUtils {
    private MultiblockShapeUtils() {}

    public static AABB withFacingAndMirror(AABB in, Direction facing, boolean mirror) {
        AABB mirrored = mirror ? new AABB(1 - in.minX, in.minY, in.minZ, 1 - in.maxX, in.maxY, in.maxZ) : in;
        Vec3 first = rotate(new Vec3(mirrored.minX - 0.5, mirrored.minY - 0.5, mirrored.minZ - 0.5), facing);
        Vec3 second = rotate(new Vec3(mirrored.maxX - 0.5, mirrored.maxY - 0.5, mirrored.maxZ - 0.5), facing);
        return new AABB(first.x + 0.5, first.y + 0.5, first.z + 0.5, second.x + 0.5, second.y + 0.5, second.z + 0.5);
    }

    private static Vec3 rotate(Vec3 in, Direction to) {
        return switch (to) {
            case NORTH -> in;
            case SOUTH -> new Vec3(-in.x(), in.y(), -in.z());
            case EAST -> new Vec3(-in.z(), in.y(), in.x());
            case WEST -> new Vec3(in.z(), in.y(), -in.x());
            case DOWN, UP -> throw new IllegalArgumentException("Unexpected direction: " + to);
        };
    }
}
