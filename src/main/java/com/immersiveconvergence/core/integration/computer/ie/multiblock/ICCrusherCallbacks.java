package com.immersiveconvergence.core.integration.computer.ie.multiblock;

import com.immersiveconvergence.core.integration.computer.IEComputerValues;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.CrusherLogic.State;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.CrusherCallbacks;
import java.util.Map;

public class ICCrusherCallbacks extends CrusherCallbacks {
    @ComputerCallable public boolean isActive(CallbackEnvironment<State> env) { return isRunning(env); }

    @ComputerCallable public Map<Integer, Object> getInputQueue(CallbackEnvironment<State> env) { return IEComputerValues.inputQueue(env.object().getProcessQueue(), env.level(), recipe -> recipe.output.get()); }
}
