package com.immersiveconvergence.mixin.ie;

import com.immersiveconvergence.api.fluid.ICPipes;

import blusunrize.immersiveengineering.common.blocks.metal.FluidPumpBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FluidPumpBlockEntity.class) public abstract class FluidPumpBlockEntityMixin {
    @Redirect(method = "tickServer()V", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/fluid/IFluidPipe;getTransferableAmount(Z)I"), remap = false) private int ic$configurableTransferRate(boolean pressurized) { return ICPipes.getTransferableAmount(pressurized); }
}
