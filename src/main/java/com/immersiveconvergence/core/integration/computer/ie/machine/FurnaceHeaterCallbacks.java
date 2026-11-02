package com.immersiveconvergence.core.integration.computer.ie.machine;

import blusunrize.immersiveengineering.common.blocks.metal.FurnaceHeaterBlockEntity;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.MBEnergyCallbacks;

public class FurnaceHeaterCallbacks extends Callback<FurnaceHeaterBlockEntity> {
    public FurnaceHeaterCallbacks() { addAdditional(MBEnergyCallbacks.INSTANCE, heater -> heater.energyStorage); }

    @ComputerCallable public boolean isActive(CallbackEnvironment<FurnaceHeaterBlockEntity> env) { return env.object().getIsActive(); }
}
