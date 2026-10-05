package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.mixin.ThermoelectricGenAccessor;

import blusunrize.immersiveengineering.common.blocks.metal.ThermoelectricGenBlockEntity;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;

@SuppressWarnings("unused") public class ThermoelectricGenCallbacks extends Callback<ThermoelectricGenBlockEntity> {
    @ComputerCallable public int getEnergyOutput(CallbackEnvironment<ThermoelectricGenBlockEntity> env) { return Math.max(0, ((ThermoelectricGenAccessor) env.object()).ic$getEnergyOutput()); }
}
