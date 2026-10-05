package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import net.minecraft.world.level.block.state.BlockState;

public class ActiveBlockCallbacks<S extends IMultiblockState> extends Callback<IMultiblockBEHelper<S>> {
    @ComputerCallable public boolean isActive(CallbackEnvironment<IMultiblockBEHelper<S>> env) {
        IMultiblockBEHelper<S> helper = env.object();
        IMultiblockContext<S> context = helper.getContext();
        if (context == null) { return false; }
        BlockState state = context.getLevel().getBlockState(helper.getPositionInMB());
        return state.hasProperty(IEProperties.ACTIVE) && state.getValue(IEProperties.ACTIVE);
    }
}
