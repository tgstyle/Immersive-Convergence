package com.immersiveconvergence.mixin;

import com.immersiveconvergence.api.client.split.SplitModelProperties;

import flaxbeard.immersivepetroleum.common.blocks.BlockIPTileProvider;
import flaxbeard.immersivepetroleum.common.blocks.BlockIPMetalMultiblocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockIPTileProvider.class)
public abstract class MixinIPBlockSplitOffset {
    @Inject(method = "getExtendedState", at = @At("RETURN"), cancellable = true, remap = false)
    private void injectGetExtendedState(IBlockState state, IBlockAccess world, BlockPos pos, CallbackInfoReturnable<IBlockState> cir) {
        if ((Object)this instanceof BlockIPMetalMultiblocks) { cir.setReturnValue(SplitModelProperties.withOffset(cir.getReturnValue(), world, pos)); }
    }
}
