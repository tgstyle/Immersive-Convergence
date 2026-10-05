package com.immersiveconvergence.mixin.ie.common;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityThermoelectricGen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TileEntityThermoelectricGen.class)
public interface MixinIEComputerThermoelectricGen {
    @Accessor(value="energyOutput", remap=false)
    int getEnergyOutput();
}
