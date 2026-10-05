package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.TankInfoCallbacks;
import com.immersiveconvergence.mixin.GasGeneratorAccessor;

import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.MBEnergyCallbacks;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.GasGeneratorTileEntity;

@SuppressWarnings("unused") public class GasGeneratorCallbacks extends Callback<GasGeneratorTileEntity> {
    public GasGeneratorCallbacks() {
        addAdditional(MBEnergyCallbacks.INSTANCE, generator -> ((GasGeneratorAccessor) generator).ic$getEnergyStorage());
        addAdditional(new TankInfoCallbacks<>(generator -> ((GasGeneratorAccessor) generator).ic$getTank(), ""));
    }

    @ComputerCallable public boolean isActive(CallbackEnvironment<GasGeneratorTileEntity> env) { return ((GasGeneratorAccessor) env.object()).ic$isActive(); }
}
