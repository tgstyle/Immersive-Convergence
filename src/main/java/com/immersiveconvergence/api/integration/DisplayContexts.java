package com.immersiveconvergence.api.integration;

import com.immersiveconvergence.api.multiblock.IDisplayContext;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public class DisplayContexts {
    @Nullable public static IDisplayContext of(@Nullable BlockEntity be) {
        if (be instanceof IMultiblockBE<?> multiblockBE) {
            IMultiblockState state = multiblockBE.getHelper().getState();
            return state instanceof IDisplayContext dc ? dc : null;
        }
        return be instanceof IDisplayContext dc ? dc : null;
    }
}
