package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration.ExtraComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.MultiblockCallbackWrapper;
import net.minecraft.core.BlockPos;

import java.util.function.Predicate;

public class MultiblockComputerOwner<S extends IMultiblockState> extends MultiblockCallbackWrapper<S> {
    private final MultiblockRegistration<S> multiblock;
    private final Predicate<BlockPos> attach;

    public MultiblockComputerOwner(Callback<S> callback, Callback<IMultiblockBEHelper<S>> helperCallback, MultiblockRegistration<S> multiblock, String name, Predicate<BlockPos> attach) {
        super(callback, multiblock, name);
        this.multiblock = multiblock;
        this.attach = attach;
        addAdditional(helperCallback, IMultiblockBE::getHelper);
        for (ExtraComponent<S, ?> component : multiblock.extraComponents()) {
            if (component.makeWrapper() instanceof RedstoneControl<?> control && control.allowComputerControl()) { addControl(control); }
        }
    }

    @SuppressWarnings("unchecked") private void addControl(RedstoneControl<?> control) {
        RedstoneControl<S> typed = (RedstoneControl<S>) control;
        addAdditional(ComputerControlCallbacks.INSTANCE, be -> typed.wrapState(be.getHelper().getState()));
    }

    @Override public boolean canAttachTo(IMultiblockBE<S> candidate) {
        IMultiblockBEHelper<S> helper = candidate.getHelper();
        return helper.getMultiblock() == multiblock && attach.test(helper.getPositionInMB());
    }
}
