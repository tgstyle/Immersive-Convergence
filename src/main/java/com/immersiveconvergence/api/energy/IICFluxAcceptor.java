package com.immersiveconvergence.api.energy;

import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public interface IICFluxAcceptor extends IICFluxConnection {
    int receiveEnergy(@Nullable EnumFacing from, int energy, boolean simulate);

    int getEnergyStored(@Nullable EnumFacing from);

    int getMaxEnergyStored(@Nullable EnumFacing from);
}
