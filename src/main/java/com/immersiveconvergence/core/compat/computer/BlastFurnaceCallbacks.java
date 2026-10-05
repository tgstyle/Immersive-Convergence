package com.immersiveconvergence.core.compat.computer;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.FurnaceHandler.IFurnaceEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.SingleItemCallback;
import net.minecraft.world.inventory.ContainerData;

import java.util.function.Function;

public class BlastFurnaceCallbacks<S extends IMultiblockState & IFurnaceEnvironment<?>> extends FurnaceCallbacks<S> {
    public BlastFurnaceCallbacks(Function<S, ContainerData> view) {
        super(view, 1, 2);
        addAdditional(SingleItemCallback.fromHandler(IFurnaceEnvironment::getInventory, 0, "input stack"));
        addAdditional(SingleItemCallback.fromHandler(IFurnaceEnvironment::getInventory, 3, "slag stack"));
    }
}
