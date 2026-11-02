package com.immersiveconvergence.core.compat.computer;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.ExcavatorLogic.State;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.ExcavatorCallbacks;

public class ICExcavatorCallbacks extends ExcavatorCallbacks {
    @ComputerCallable public boolean isActive(CallbackEnvironment<State> env) { return isRunning(env); }
}
