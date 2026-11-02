package com.immersiveconvergence.mixin.accessor.immersiveengineering;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityThermoelectricGen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TileEntityThermoelectricGen.class) public interface TileEntityThermoelectricGenAccessor {
    @Accessor(value="energyOutput", remap=false) int getEnergyOutput();
}
