package com.immersiveconvergence.api.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.function.DoubleSupplier;

public abstract class BaseBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    public static DoubleSupplier viewDistanceModifier = () -> 1.0;

    @Override public int getViewDistance() { return (int) (BlockEntityRenderer.super.getViewDistance() * viewDistanceModifier.getAsDouble()); }
}
