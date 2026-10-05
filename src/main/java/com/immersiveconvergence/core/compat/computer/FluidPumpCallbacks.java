package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.TankInfoCallbacks;
import com.immersiveconvergence.mixin.FluidPumpAccessor;

import blusunrize.immersiveengineering.common.blocks.metal.FluidPumpBlockEntity;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.MBEnergyCallbacks;

@SuppressWarnings("unused") public class FluidPumpCallbacks extends Callback<FluidPumpBlockEntity> {
    public FluidPumpCallbacks() {
        addAdditional(MBEnergyCallbacks.INSTANCE, pump -> ((FluidPumpAccessor) pump).ic$getEnergyStorage());
        addAdditional(new TankInfoCallbacks<>(pump -> ((FluidPumpAccessor) pump).ic$getTank(), ""));
    }

    @ComputerCallable public boolean isPlacingCobble(CallbackEnvironment<FluidPumpBlockEntity> env) { return ((FluidPumpAccessor) env.object()).ic$isPlaceCobble(); }
}
