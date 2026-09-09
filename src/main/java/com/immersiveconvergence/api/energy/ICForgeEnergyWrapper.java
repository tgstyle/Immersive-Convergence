package com.immersiveconvergence.api.energy;

import blusunrize.immersiveengineering.common.util.EnergyHelper;
import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public class ICForgeEnergyWrapper extends EnergyHelper.IEForgeEnergyWrapper {
    public ICForgeEnergyWrapper(IICInternalFluxHandler fluxHandler, EnumFacing side) { super(fluxHandler, side); }
}
