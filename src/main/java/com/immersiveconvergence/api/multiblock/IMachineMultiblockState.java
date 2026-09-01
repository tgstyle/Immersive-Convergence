package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.util.ConstrainedItemHandler;
import com.immersiveconvergence.api.util.TankPair;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;

@SuppressWarnings({"unused", "RedundantSuppression"}) public interface IMachineMultiblockState extends IMultiblockState {
    ConstrainedItemHandler getInventory();
    TankPair getTanks();
    double getHeatLevel();
    int getProcessProgress();
}
