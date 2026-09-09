package com.immersiveconvergence.api.energy;

import com.immersiveconvergence.api.block.ICSideConfig;

import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public interface IICFluxConnector extends IICFluxConnection {
    @Nonnull ICSideConfig getEnergySideConfig(@Nullable EnumFacing facing);

    @Override default boolean canConnectEnergy(@Nullable EnumFacing fd) { return getEnergySideConfig(fd) != ICSideConfig.NONE; }

    ICFluxWrapper getCapabilityWrapper(EnumFacing facing);
}
