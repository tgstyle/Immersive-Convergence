package com.immersiveconvergence.mixin.ie;

import com.immersiveconvergence.api.fluid.ICPipes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "blusunrize.immersiveengineering.common.blocks.metal.FluidPipeBlockEntity$PipeFluidHandler")
public abstract class PipeFluidHandlerMixin {
    @Redirect(method = "getTransferableAmount(Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraft/world/level/block/entity/BlockEntity;)I", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/fluid/IFluidPipe;getTransferableAmount(Z)I"), remap = false)
    private int ic$configurableTransferRate(boolean pressurized) { return ICPipes.getTransferableAmount(pressurized); }
}
