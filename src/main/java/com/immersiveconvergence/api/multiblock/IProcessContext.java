package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.crafting.ICMultiblockRecipe;
import com.immersiveconvergence.api.energy.ICEnergyStorageAveraging;
import com.immersiveconvergence.api.multiblock.process.ProcessContext;

@SuppressWarnings({"unused", "RedundantSuppression"}) public interface IProcessContext<R extends ICMultiblockRecipe> extends ProcessContext<R> {
    ICEnergyStorageAveraging getEnergyStorage();

    @Override default ICEnergyStorageAveraging getEnergy() { return getEnergyStorage(); }

    interface ProcessContextInMachine<R extends ICMultiblockRecipe> extends IProcessContext<R>, ProcessContext.ProcessContextInMachine<R> { }
}
