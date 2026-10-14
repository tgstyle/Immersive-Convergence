package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;

import com.google.common.base.Preconditions;
import net.minecraft.world.level.block.state.BlockBehaviour;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MultiblockPartBlockNonMirror<S extends IMultiblockState> extends MachineMultiblockPartBlock<S> {
    public MultiblockPartBlockNonMirror(BlockBehaviour.Properties properties, MultiblockRegistration<S> registration) {
        super(properties, registration);
        Preconditions.checkState(!registration.mirrorable());
    }
}
