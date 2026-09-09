package com.immersiveconvergence.mixin.ie.common;

import blusunrize.immersiveengineering.common.blocks.metal.BlockMetalDevice0;
import blusunrize.immersiveengineering.common.blocks.metal.BlockTypes_MetalDevice0;

import com.immersiveconvergence.common.blocks.pipes.TileEntityFluidPumpAlternative;
import com.immersiveconvergence.core.ICMixinConfig;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockMetalDevice0.class)
public abstract class MixinIEBlockMetalDevice0 {
    @Inject(method = "createBasicTE(Lnet/minecraft/world/World;Lblusunrize/immersiveengineering/common/blocks/metal/BlockTypes_MetalDevice0;)Lnet/minecraft/tileentity/TileEntity;", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectCreateTE(World world, BlockTypes_MetalDevice0 type, CallbackInfoReturnable<TileEntity> cir) { if (type == BlockTypes_MetalDevice0.FLUID_PUMP && ICMixinConfig.mixinSettings.replaceIEPipes) { cir.setReturnValue(new TileEntityFluidPumpAlternative()); } }
}