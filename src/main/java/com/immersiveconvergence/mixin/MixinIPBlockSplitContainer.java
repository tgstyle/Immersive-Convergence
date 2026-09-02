package com.immersiveconvergence.mixin;

import com.immersiveconvergence.api.client.split.SplitModelProperties;

import flaxbeard.immersivepetroleum.common.blocks.BlockIPBase;
import flaxbeard.immersivepetroleum.common.blocks.BlockIPMetalMultiblocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockIPBase.class)
public abstract class MixinIPBlockSplitContainer {
    @Inject(method = "createBlockState", at = @At("RETURN"), cancellable = true)
    private void injectCreateBlockState(CallbackInfoReturnable<BlockStateContainer> cir) {
        if ((Object)this instanceof BlockIPMetalMultiblocks) { cir.setReturnValue(SplitModelProperties.withOffset((Block)(Object)this, cir.getReturnValue())); }
    }
}
