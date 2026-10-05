package com.immersiveconvergence.mixin.ie.common;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBelljar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TileEntityBelljar.class)
public interface MixinIEComputerBelljar {
    @Accessor(value="growth", remap=false)
    float getGrowth();
}
