package com.immersiveconvergence.core.integration.computer.ie.machine;

import com.immersiveconvergence.mixin.accessor.immersiveengineering.FluidPumpBlockEntityAccessor;

import blusunrize.immersiveengineering.common.blocks.metal.FluidPumpBlockEntity;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.MBEnergyCallbacks;

public class FluidPumpCallbacks extends Callback<FluidPumpBlockEntity> {
    public FluidPumpCallbacks() {
        addAdditional(MBEnergyCallbacks.INSTANCE, pump -> ((FluidPumpBlockEntityAccessor) pump).ic$getEnergyStorage());
        addAdditional(new TankInfoCallbacks<>(pump -> ((FluidPumpBlockEntityAccessor) pump).ic$getTank(), ""));
    }

    @ComputerCallable public boolean isPlacingCobble(CallbackEnvironment<FluidPumpBlockEntity> env) { return ((FluidPumpBlockEntityAccessor) env.object()).ic$isPlaceCobble(); }
}
