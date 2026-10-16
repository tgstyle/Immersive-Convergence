package com.immersiveconvergence.api.multiblock.process;

import com.immersiveconvergence.api.crafting.ICMultiblockRecipe;
import com.immersiveconvergence.api.energy.ICEnergyStorageAveraging;

import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

public interface ProcessContext<R extends ICMultiblockRecipe> {
    ICEnergyStorageAveraging getEnergy();

    default IItemHandlerModifiable getInventory() { return EMPTY_ITEM_HANDLER; }

    default IFluidTank[] getInternalTanks() { return EMPTY_TANKS; }

    interface ProcessContextInMachine<R extends ICMultiblockRecipe> extends ProcessContext<R> {
        default int[] getOutputTanks() { return EMPTY_INTS; }

        default int[] getOutputSlots() { return EMPTY_INTS; }
    }

    int[] EMPTY_INTS = {};
    IItemHandlerModifiable EMPTY_ITEM_HANDLER = new EmptyItemHandler();
    IFluidTank[] EMPTY_TANKS = {};
}
