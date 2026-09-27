package com.immersiveconvergence.mixin;

import com.immersiveconvergence.api.multiblock.IResettableCache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockBEHelperCommon$CachedValue", remap = false)
public abstract class CachedValueMixin implements IResettableCache {
    @Shadow(remap = false) private Object value;

    @Override public void ic$reset() { value = null; }
}
