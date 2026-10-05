package com.immersiveconvergence.core.compat.computer;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.FurnaceHandler.IFurnaceEnvironment;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.FurnaceHandler.StateView;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.SingleItemCallback;
import net.minecraft.world.inventory.ContainerData;

import java.util.function.Function;

@SuppressWarnings("unused") public class FurnaceCallbacks<S extends IMultiblockState & IFurnaceEnvironment<?>> extends Callback<S> {
    private final Function<S, ContainerData> view;

    public FurnaceCallbacks(Function<S, ContainerData> view, int fuelSlot, int outputSlot) {
        this.view = view;
        addAdditional(SingleItemCallback.fromHandler(IFurnaceEnvironment::getInventory, fuelSlot, "fuel stack"));
        addAdditional(SingleItemCallback.fromHandler(IFurnaceEnvironment::getInventory, outputSlot, "output stack"));
    }

    @ComputerCallable public int getProcess(CallbackEnvironment<S> env) { return StateView.getProcess(view.apply(env.object())); }

    @ComputerCallable public int getMaxProcess(CallbackEnvironment<S> env) { return StateView.getMaxProcess(view.apply(env.object())); }

    @ComputerCallable public int getBurnTime(CallbackEnvironment<S> env) { return StateView.getBurnTime(view.apply(env.object())); }
}
