package com.immersiveconvergence.mixin.ie;

import com.immersiveconvergence.api.fluid.IICFluidPipe;

import blusunrize.immersiveengineering.common.blocks.metal.FluidPipeBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidPipeBlockEntity.class) public abstract class FluidPipeBlockEntityMixin {
    @Inject(method = "canOutputPressurized(Lnet/minecraft/world/level/block/entity/BlockEntity;Z)Z", at = @At("HEAD"), cancellable = true, remap = false) private void ic$pipeOutputPressurized(BlockEntity output, boolean consumePower, CallbackInfoReturnable<Boolean> cir) {
        if (output instanceof IICFluidPipe pipe) { cir.setReturnValue(pipe.canOutputPressurized()); }
    }
}
