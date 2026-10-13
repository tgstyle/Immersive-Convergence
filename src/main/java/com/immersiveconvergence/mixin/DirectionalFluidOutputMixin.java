package com.immersiveconvergence.mixin;

import com.immersiveconvergence.api.fluid.IICFluidPipe;

import blusunrize.immersiveengineering.common.blocks.metal.FluidPipeBlockEntity.DirectionalFluidOutput;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DirectionalFluidOutput.class, remap = false)
public abstract class DirectionalFluidOutputMixin {
    @Final @Shadow(remap = false) private BlockEntity containingTile;

    @Inject(method = "stripPressure()Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void ic$pipeStripPressure(CallbackInfoReturnable<Boolean> cir) {
        if (containingTile instanceof IICFluidPipe pipe) { cir.setReturnValue(pipe.stripPressureTag()); }
    }
}
