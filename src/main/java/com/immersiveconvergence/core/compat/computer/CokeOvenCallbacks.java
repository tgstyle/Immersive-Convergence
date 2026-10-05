package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.TankInfoCallbacks;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.CokeOvenLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.CokeOvenLogic.State;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.SingleItemCallback;

@SuppressWarnings("unused") public class CokeOvenCallbacks extends Callback<State> {
    public CokeOvenCallbacks() {
        addAdditional(new TankInfoCallbacks<>(State::getTank, ""));
        addAdditional(SingleItemCallback.fromHandler(State::getInventory, CokeOvenLogic.INPUT_SLOT, "input stack"));
        addAdditional(SingleItemCallback.fromHandler(State::getInventory, CokeOvenLogic.OUTPUT_SLOT, "output stack"));
        addAdditional(SingleItemCallback.fromHandler(State::getInventory, CokeOvenLogic.EMPTY_CONTAINER_SLOT, "empty cannisters"));
        addAdditional(SingleItemCallback.fromHandler(State::getInventory, CokeOvenLogic.FULL_CONTAINER_SLOT, "filled cannisters"));
    }

    @ComputerCallable public int getProcess(CallbackEnvironment<State> env) { return env.object().get(State.BURN_TIME); }

    @ComputerCallable public int getMaxProcess(CallbackEnvironment<State> env) { return env.object().get(State.MAX_BURN_TIME); }
}
