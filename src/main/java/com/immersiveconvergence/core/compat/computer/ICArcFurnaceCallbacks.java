package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.core.compat.computer.alias.IndexedStackAlias;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.arcfurnace.ArcFurnaceLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.arcfurnace.ArcFurnaceLogic.State;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.SingleItemCallback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.ArcFurnaceCallbacks;

public class ICArcFurnaceCallbacks extends ArcFurnaceCallbacks {
    public ICArcFurnaceCallbacks() {
        addAdditional(SingleItemCallback.fromHandler(State::getInventory, ArcFurnaceLogic.SLAG_SLOT, "slag stack"));
        addAdditional(new IndexedStackAlias<>((state, index) -> state.getInventory().getStackInSlot(ArcFurnaceLogic.FIRST_ELECTRODE_SLOT + index), ArcFurnaceLogic.ELECTRODE_COUNT, "Electrode slots are 1-3", "getElectrode"));
    }

    @ComputerCallable public boolean isActive(CallbackEnvironment<State> env) { return isRunning(env); }

    @ComputerCallable public boolean hasElectrodes(CallbackEnvironment<State> env) { return env.object().hasElectrodes(); }
}
