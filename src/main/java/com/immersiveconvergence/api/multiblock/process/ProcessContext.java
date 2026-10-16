package com.immersiveconvergence.api.multiblock.process;

import com.immersiveconvergence.api.crafting.ICMultiblockRecipe;
import com.immersiveconvergence.api.energy.ICEnergyStorageAveraging;

import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EmptyHandler;

public interface ProcessContext<R extends ICMultiblockRecipe> {
    ICEnergyStorageAveraging getEnergy();

    default IItemHandlerModifiable getInventory() { return EMPTY_ITEM_HANDLER; }

    default IFluidTank[] getInternalTanks() { return EMPTY_TANKS; }

    interface ProcessContextInMachine<R extends ICMultiblockRecipe> extends ProcessContext<R> {
        default int[] getOutputTanks() { return EMPTY_INTS; }

        default int[] getOutputSlots() { return EMPTY_INTS; }
    }

    int[] EMPTY_INTS = {};
    IItemHandlerModifiable EMPTY_ITEM_HANDLER = new EmptyHandler();
    IFluidTank[] EMPTY_TANKS = {};
}
