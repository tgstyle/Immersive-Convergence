package com.immersiveconvergence.api.capability;

import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public interface IHeatConsumer {
    int getFluidAmount();

    default boolean acceptsHeatFrom(EnumFacing side) { return true; }
}
