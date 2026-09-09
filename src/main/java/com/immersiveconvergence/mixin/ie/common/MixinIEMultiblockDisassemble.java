package com.immersiveconvergence.mixin.ie.common;

import com.immersiveconvergence.api.multiblock.ICMultiblockPart;
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
        ICMultiblockPart part = ICMultiblockPart.of((TileEntityMultiblockPart<?>)(Object)this);
        if (part != null && QueueProcessor.handleDisassembly(part, structureDimensions, true) != QueueProcessor.Result.FALLBACK) { ci.cancel(); }
    }
}
