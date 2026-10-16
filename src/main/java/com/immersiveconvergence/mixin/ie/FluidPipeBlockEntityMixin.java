package com.immersiveconvergence.mixin.ie;

import com.immersiveconvergence.api.fluid.IICFluidPipe;
import com.immersiveconvergence.common.ie.IECapReferenceReset;

import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.blocks.metal.FluidPipeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Map;

@Mixin(FluidPipeBlockEntity.class) public abstract class FluidPipeBlockEntityMixin {
    @Final @Shadow(remap = false) private Map<Direction, CapabilityReference<IFluidHandler>> neighbors;

    @Inject(method = "updateConnectionByte(Lnet/minecraft/core/Direction;)Z", at = @At("HEAD"), remap = false) private void ic$invalidateCache(Direction dir, CallbackInfoReturnable<Boolean> cir) {
        CapabilityReference<IFluidHandler> ref = neighbors.get(dir);
        if (ref != null) { IECapReferenceReset.reset(ref); }
    }

    @Inject(method = "canOutputPressurized(Lnet/minecraft/world/level/block/entity/BlockEntity;Z)Z", at = @At("HEAD"), cancellable = true, remap = false) private void ic$pipeOutputPressurized(BlockEntity output, boolean consumePower, CallbackInfoReturnable<Boolean> cir) {
        if (output instanceof IICFluidPipe pipe) { cir.setReturnValue(pipe.canOutputPressurized()); }
    }
}
