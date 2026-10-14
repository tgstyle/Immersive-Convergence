package com.immersiveconvergence.api.util;

import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration.ExtraComponent;
import com.immersiveconvergence.api.multiblock.blocks.component.RedstoneControl;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelper;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockBE;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;

import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackOwner;
import net.minecraft.core.BlockPos;

import java.util.function.Predicate;

public class ICMultiblockComputerOwner<S extends IMultiblockState> extends CallbackOwner<IMultiblockBE<S>> {
    private final MultiblockRegistration<S> multiblock;
    private final Predicate<BlockPos> attach;

    @SuppressWarnings("unchecked") public ICMultiblockComputerOwner(Callback<S> callback, Callback<IMultiblockBEHelper<S>> helperCallback, MultiblockRegistration<S> multiblock, String name, Predicate<BlockPos> attach) {
        super((Class<IMultiblockBE<S>>) (Class<?>) IMultiblockBE.class, name);
        this.multiblock = multiblock;
        this.attach = attach;
        addAdditional(callback, be -> be.getHelper().getState());
        addAdditional(helperCallback, IMultiblockBE::getHelper);
        for (ExtraComponent<S, ?> component : multiblock.extraComponents()) {
            if (component.makeWrapper() instanceof RedstoneControl<?> control && control.allowComputerControl()) { addControl(control); }
        }
    }

    @SuppressWarnings("unchecked") private void addControl(RedstoneControl<?> control) {
        RedstoneControl<S> typed = (RedstoneControl<S>) control;
        addAdditional(ICComputerControlCallbacks.INSTANCE, be -> typed.wrapState(be.getHelper().getState()));
    }

    @Override public boolean canAttachTo(IMultiblockBE<S> candidate) {
        IMultiblockBEHelper<S> helper = candidate.getHelper();
        return helper.getMultiblock() == multiblock && attach.test(helper.getPositionInMB());
    }
}
