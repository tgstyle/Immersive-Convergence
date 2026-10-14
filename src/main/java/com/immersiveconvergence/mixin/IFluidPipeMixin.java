package com.immersiveconvergence.mixin;

import com.immersiveconvergence.api.fluid.ICPipes;

import blusunrize.immersiveengineering.api.fluid.IFluidPipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IFluidPipe.class)
public interface IFluidPipeMixin {
    @Inject(method = "getTransferableAmount(Z)I", at = @At("HEAD"), cancellable = true, remap = false)
    private static void ic$configurableTransferRate(boolean pressurized, CallbackInfoReturnable<Integer> cir) { cir.setReturnValue(ICPipes.getTransferableAmount(pressurized)); }
}
