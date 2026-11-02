package com.immersiveconvergence.core.integration.computer.ie.multiblock;

import com.immersiveconvergence.core.integration.computer.alias.TankInfoAlias;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.DieselGeneratorLogic.State;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.DieselGenCallbacks;

public class ICDieselGeneratorCallbacks extends DieselGenCallbacks {
    public ICDieselGeneratorCallbacks() { addAdditional(new TankInfoAlias<>(state -> state.tank, "getTankInfo")); }

    @ComputerCallable public boolean isActive(CallbackEnvironment<State> env) { return isRunning(env); }
}
