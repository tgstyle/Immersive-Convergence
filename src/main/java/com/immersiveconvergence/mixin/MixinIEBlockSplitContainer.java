package com.immersiveconvergence.mixin;

import com.immersiveconvergence.api.client.split.SplitModelProperties;

import blusunrize.immersiveengineering.common.blocks.BlockIEBase;
import blusunrize.immersiveengineering.common.blocks.metal.BlockMetalMultiblocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockIEBase.class)
public abstract class MixinIEBlockSplitContainer {
    @Inject(method = "createBlockState", at = @At("RETURN"), cancellable = true, require = 0, remap = false)
    private void injectCreateBlockStateDev(CallbackInfoReturnable<BlockStateContainer> cir) { immersiveconvergence$append(cir); }

    @Inject(method = "func_180661_e", at = @At("RETURN"), cancellable = true, require = 0, remap = false)
    private void injectCreateBlockStateProduction(CallbackInfoReturnable<BlockStateContainer> cir) { immersiveconvergence$append(cir); }

    private void immersiveconvergence$append(CallbackInfoReturnable<BlockStateContainer> cir) {
        if ((Object)this instanceof BlockMetalMultiblocks) { cir.setReturnValue(SplitModelProperties.withOffset((Block)(Object)this, cir.getReturnValue())); }
    }
}
