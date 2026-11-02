package com.immersiveconvergence.core.integration.computer.ie.machine;

import blusunrize.immersiveengineering.common.blocks.metal.EnergyMeterBlockEntity;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.EnergyMeterCallbacks;

public class ICEnergyMeterCallbacks extends EnergyMeterCallbacks {
    @ComputerCallable public int getAvgEnergy(CallbackEnvironment<EnergyMeterBlockEntity> env) { return getAveragePower(env); }
}
