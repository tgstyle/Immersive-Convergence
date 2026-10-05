package com.immersiveconvergence.core.compat.computer;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.AlloySmelterLogic.State;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.InventoryCallbacks;

public class AlloySmelterCallbacks extends FurnaceCallbacks<State> {
    public AlloySmelterCallbacks() {
        super(State::getStateView, 2, 3);
        addAdditional(InventoryCallbacks.fromHandler(State::getInventory, 0, 2, "input"));
    }
}
