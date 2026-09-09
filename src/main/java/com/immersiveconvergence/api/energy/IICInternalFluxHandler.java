package com.immersiveconvergence.api.energy;

import com.immersiveconvergence.api.block.ICSideConfig;
import com.immersiveconvergence.api.util.ICFluxStorage;

import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IICInternalFluxHandler extends IICFluxHandler {
    @Nonnull ICFluxStorage getStorage();

    @Nonnull ICSideConfig getSideConfig(@Nullable EnumFacing facing);

    @Override @Nonnull default ICFluxStorage getFluxStorage() { return getStorage(); }

    @Override @Nonnull default ICSideConfig getEnergySideConfig(@Nullable EnumFacing facing) { return getSideConfig(facing); }
}
