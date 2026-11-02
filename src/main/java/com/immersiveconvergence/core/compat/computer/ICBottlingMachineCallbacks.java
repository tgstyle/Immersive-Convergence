package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.core.compat.computer.alias.TankInfoAlias;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.bottling_machine.BottlingMachineLogic.State;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.BottlingMachineCallbacks;

public class ICBottlingMachineCallbacks extends BottlingMachineCallbacks {
    public ICBottlingMachineCallbacks() { addAdditional(new TankInfoAlias<>(state -> state.tank, "getTank")); }

    @ComputerCallable public boolean isActive(CallbackEnvironment<State> env) { return isRunning(env); }
}
