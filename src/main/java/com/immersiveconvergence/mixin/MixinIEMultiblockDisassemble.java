package com.immersiveconvergence.mixin;

import com.immersiveconvergence.api.multiblock.QueueProcessor;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityMultiblockPart.class, remap = false)
public abstract class MixinIEMultiblockDisassemble {
    @Shadow @Final protected int[] structureDimensions;

    @Inject(method = "disassemble", at = @At("HEAD"), cancellable = true, remap = false)
    private void immersiveconvergence$queueDisassembly(CallbackInfo ci) {
        if (QueueProcessor.handleDisassembly((TileEntityMultiblockPart<?>)(Object)this, structureDimensions, true) != QueueProcessor.Result.FALLBACK) { ci.cancel(); }
    }
}
