package com.immersiveconvergence.mixin;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.GasGeneratorTileEntity;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GasGeneratorTileEntity.class, remap = false)
public interface GasGeneratorAccessor {
    @Accessor(value = "isActive", remap = false) boolean ic$isActive();

    @Accessor(value = "tank", remap = false) FluidTank ic$getTank();

    @Accessor(value = "energyStorage", remap = false) MutableEnergyStorage ic$getEnergyStorage();
}
