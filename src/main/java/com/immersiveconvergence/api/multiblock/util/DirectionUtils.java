package com.immersiveconvergence.api.multiblock.util;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Rotation;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;

public final class DirectionUtils {
    public static final Direction[] VALUES = Direction.values();
    public static final Direction[] BY_HORIZONTAL_INDEX = Arrays.stream(VALUES).filter(direction -> direction.getAxis().isHorizontal()).sorted(Comparator.comparingInt(Direction::get2DDataValue)).toArray(Direction[]::new);

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
