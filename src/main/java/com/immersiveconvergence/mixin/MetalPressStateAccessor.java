package com.immersiveconvergence.mixin;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.MetalPressLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MetalPressLogic.State.class, remap = false)
public interface MetalPressStateAccessor {
    @Accessor(value = "renderAsActive", remap = false) boolean ic$isRenderAsActive();
}
