package com.immersiveconvergence.mixin.ip.common;

import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityPumpjack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityPumpjack.class)
public abstract class MixinIPPumpjackPipes {
    @Redirect(method = "update(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getX()I", ordinal = 0, remap = true), remap = false)
    private int redirectPipeCheckX(BlockPos pos) { return Math.abs(pos.getX()); }

    @Redirect(method = "update(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getZ()I", ordinal = 0, remap = true), remap = false)
    private int redirectPipeCheckZ(BlockPos pos) { return Math.abs(pos.getZ()); }
}
