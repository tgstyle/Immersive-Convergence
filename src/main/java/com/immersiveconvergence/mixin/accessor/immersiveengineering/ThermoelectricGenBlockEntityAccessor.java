package com.immersiveconvergence.mixin.accessor.immersiveengineering;

import blusunrize.immersiveengineering.common.blocks.metal.ThermoelectricGenBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ThermoelectricGenBlockEntity.class, remap = false) public interface ThermoelectricGenBlockEntityAccessor {
    @Accessor(value = "energyOutput", remap = false) int ic$getEnergyOutput();
}
