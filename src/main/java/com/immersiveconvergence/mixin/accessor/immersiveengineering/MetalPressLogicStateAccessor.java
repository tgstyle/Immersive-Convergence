package com.immersiveconvergence.mixin.accessor.immersiveengineering;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.MetalPressLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MetalPressLogic.State.class, remap = false) public interface MetalPressLogicStateAccessor {
    @Accessor(value = "renderAsActive", remap = false) boolean ic$isRenderAsActive();
}
