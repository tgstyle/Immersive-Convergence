package com.immersiveconvergence.mixin.integration.immersiveengineering.common.blocks.multiblocks.blockimpl;

import com.immersiveconvergence.api.multiblock.util.IResettableCache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockBEHelperCommon$CachedValue", remap = false) public abstract class MultiblockBEHelperCommonCachedValueMixin implements IResettableCache {
    @Shadow(remap = false) private Object value;

    @Override public void ic$reset() { value = null; }
}
