package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.google.common.base.Preconditions;
import net.minecraft.world.level.block.state.BlockBehaviour;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MultiblockPartBlockNonMirror<S extends IMultiblockState> extends MachineMultiblockPartBlock<S> {
    public MultiblockPartBlockNonMirror(BlockBehaviour.Properties properties, MultiblockRegistration<S> registration) {
        super(properties, registration);
        Preconditions.checkState(!registration.mirrorable());
    }
}
