package com.immersiveconvergence.core.integration.computer.ie.multiblock;

import com.immersiveconvergence.core.integration.computer.IEComputerValues;
import com.immersiveconvergence.core.integration.computer.alias.TankInfoAlias;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.mixer.MixerLogic.State;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.MixerCallbacks;

public class ICMixerCallbacks extends MixerCallbacks {
    public ICMixerCallbacks() { addAdditional(new TankInfoAlias<>(state -> state.tank, "getTank")); }

    @ComputerCallable public boolean isActive(CallbackEnvironment<State> env) { return isRunning(env); }

    @ComputerCallable public boolean isValidRecipe(CallbackEnvironment<State> env) { return IEComputerValues.firstRecipe(env.object().processor.getQueue(), env.level()) != null; }
}
