package com.immersiveconvergence.api.multiblock.util;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Rotation;

import javax.annotation.Nullable;

public final class DirectionUtils {
    private DirectionUtils() {}

    @Nullable public static Rotation getRotationBetweenFacings(Direction orig, Direction to) {
        if (to == orig) { return Rotation.NONE; }
        if (orig.getAxis() == Axis.Y || to.getAxis() == Axis.Y) { return null; }
        Direction turned = orig.getClockWise();
        if (turned == to) { return Rotation.CLOCKWISE_90; }
        turned = turned.getClockWise();
        if (turned == to) { return Rotation.CLOCKWISE_180; }
        turned = turned.getClockWise();
        if (turned == to) { return Rotation.COUNTERCLOCKWISE_90; }
        return null;
    }
}
