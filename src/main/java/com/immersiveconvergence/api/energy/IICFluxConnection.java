package com.immersiveconvergence.api.energy;

import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public interface IICFluxConnection {
    boolean canConnectEnergy(@Nullable EnumFacing from);
}
