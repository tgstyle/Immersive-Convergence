package com.immersiveconvergence.mixin.accessor.immersiveengineering;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBelljar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TileEntityBelljar.class) public interface TileEntityBelljarAccessor {
    @Accessor(value="growth", remap=false) float getGrowth();
}
