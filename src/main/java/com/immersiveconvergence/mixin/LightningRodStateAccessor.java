package com.immersiveconvergence.mixin;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.LightningRodLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = LightningRodLogic.State.class, remap = false)
public interface LightningRodStateAccessor {
    @Accessor(value = "energy", remap = false) MutableEnergyStorage ic$getEnergy();
}
