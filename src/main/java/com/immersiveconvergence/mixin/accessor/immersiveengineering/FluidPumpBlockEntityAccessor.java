package com.immersiveconvergence.mixin.accessor.immersiveengineering;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.blocks.metal.FluidPumpBlockEntity;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = FluidPumpBlockEntity.class, remap = false) public interface FluidPumpBlockEntityAccessor {
    @Accessor(value = "tank", remap = false) FluidTank ic$getTank();

    @Accessor(value = "energyStorage", remap = false) MutableEnergyStorage ic$getEnergyStorage();

    @Accessor(value = "placeCobble", remap = false) boolean ic$isPlaceCobble();
}
