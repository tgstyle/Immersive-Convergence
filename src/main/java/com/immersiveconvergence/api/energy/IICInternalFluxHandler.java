package com.immersiveconvergence.api.energy;

import com.immersiveconvergence.api.block.ICSideConfig;
import com.immersiveconvergence.api.util.ICFluxStorage;

import blusunrize.immersiveengineering.api.IEEnums.SideConfig;
import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorage;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IICInternalFluxHandler extends EnergyHelper.IIEInternalFluxHandler {
    @Nonnull ICFluxStorage getStorage();

    @Nonnull ICSideConfig getSideConfig(@Nullable EnumFacing facing);

    @Override @Nonnull default FluxStorage getFluxStorage() { return getStorage(); }

    @Override @Nonnull default SideConfig getEnergySideConfig(@Nullable EnumFacing facing) { return getSideConfig(facing).toIE(); }
}
