package com.immersiveconvergence.core.integration.computer.ie.machine;

import com.immersiveconvergence.mixin.accessor.immersiveengineering.ThermoelectricGenBlockEntityAccessor;

import blusunrize.immersiveengineering.common.blocks.metal.ThermoelectricGenBlockEntity;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;

public class ThermoelectricGenCallbacks extends Callback<ThermoelectricGenBlockEntity> {
    @ComputerCallable public int getEnergyOutput(CallbackEnvironment<ThermoelectricGenBlockEntity> env) { return Math.max(0, ((ThermoelectricGenBlockEntityAccessor) env.object()).ic$getEnergyOutput()); }
}
