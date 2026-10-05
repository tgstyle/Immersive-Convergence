package com.immersiveconvergence.core.compat.computer;

import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.MBEnergyCallbacks;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.PumpjackLogic.State;

@SuppressWarnings("unused") public class PumpjackCallbacks extends Callback<State> {
    public PumpjackCallbacks() { addAdditional(MBEnergyCallbacks.INSTANCE, state -> state.energy); }

    @ComputerCallable public boolean isActive(CallbackEnvironment<State> env) { return env.object().wasActive; }
}
