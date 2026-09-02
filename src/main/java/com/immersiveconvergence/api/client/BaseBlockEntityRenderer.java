package com.immersiveconvergence.api.client;

import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Quaternionf;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.DoubleSupplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public abstract class BaseBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    public static DoubleSupplier viewDistanceModifier = () -> 1.0;
    private static final Map<Direction, Quaternionf> ROTATE_FOR_FACING = Util.make(new EnumMap<>(Direction.class), m -> {
        for (Direction facing : DirectionUtils.BY_HORIZONTAL_INDEX) { m.put(facing, new Quaternionf().rotateY(Mth.DEG_TO_RAD * (180 - facing.toYRot()))); }
    });

    protected static void rotateForFacingNoCentering(PoseStack stack, Direction facing) { stack.mulPose(ROTATE_FOR_FACING.get(facing)); }

    @Override public int getViewDistance() { return (int) (BlockEntityRenderer.super.getViewDistance() * viewDistanceModifier.getAsDouble()); }

    protected static void rotateForFacing(PoseStack stack, Direction facing) {
        stack.translate(0.5, 0.5, 0.5);
        rotateForFacingNoCentering(stack, facing);
        stack.translate(-0.5, -0.5, -0.5);
    }
}
