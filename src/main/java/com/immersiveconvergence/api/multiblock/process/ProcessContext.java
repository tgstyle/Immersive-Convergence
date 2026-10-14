package com.immersiveconvergence.api.multiblock.process;

import com.immersiveconvergence.api.crafting.ICMultiblockRecipe;
import com.immersiveconvergence.api.energy.ICEnergyStorageAveraging;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockLevel;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EmptyHandler;

@SuppressWarnings({"unused", "RedundantSuppression"}) public interface ProcessContext<R extends ICMultiblockRecipe> {
    ICEnergyStorageAveraging getEnergy();

    default IItemHandlerModifiable getInventory() {
        return EMPTY_ITEM_HANDLER;
    }

    default IFluidTank[] getInternalTanks() {
        return EMPTY_TANKS;
    }

    default boolean additionalCanProcessCheck(MultiblockProcess<R, ?> process, Level level) {
        return true;
    }

    default void onProcessFinish(MultiblockProcess<R, ?> process, Level level) {
    }

    interface ProcessContextInWorld<R extends ICMultiblockRecipe> extends ProcessContext<R> {
        default void doProcessOutput(ItemStack result, IMultiblockLevel level) {
            throw new RuntimeException("Should be implemented in machines that output items!");
        }

        default void doProcessFluidOutput(FluidStack output) {
            throw new RuntimeException("Should be implemented in machines that output fluids!");
        }
    }

    interface ProcessContextInMachine<R extends ICMultiblockRecipe> extends ProcessContext<R> {
        default int[] getOutputTanks() {
            return EMPTY_INTS;
        }

        default int[] getOutputSlots() {
            return EMPTY_INTS;
        }
    }

    int[] EMPTY_INTS = {};
    IItemHandlerModifiable EMPTY_ITEM_HANDLER = new EmptyHandler();
    IFluidTank[] EMPTY_TANKS = {};
}
