package com.immersiveconvergence.core.compat.computer;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.arcfurnace.ArcFurnaceLogic.State;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.ArcFurnaceCallbacks;

@SuppressWarnings("unused") public class ICArcFurnaceCallbacks extends ArcFurnaceCallbacks {
    @ComputerCallable public boolean hasElectrodes(CallbackEnvironment<State> env) { return env.object().hasElectrodes(); }
}
