package com.immersiveconvergence.api.capability;

import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public interface IHeatProvider {
    double getHeatLevel();

    default boolean providesHeatTo(EnumFacing side) { return true; }
}
