package com.immersiveconvergence.mixin;

import com.immersiveconvergence.api.multiblock.IDisassemblingAware;

import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockBEHelperCommon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MultiblockBEHelperCommon.class)
public abstract class MultiblockBEHelperCommonMixin implements IDisassemblingAware {
    @Shadow(remap = false)
    private boolean beingDisassembled;

    @Override public boolean ic$isDisassembling() { return beingDisassembled; }
}
