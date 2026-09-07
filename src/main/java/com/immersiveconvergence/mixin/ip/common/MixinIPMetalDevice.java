package com.immersiveconvergence.mixin.ip.common;

import flaxbeard.immersivepetroleum.common.blocks.BlockIPMetalDevice;
import flaxbeard.immersivepetroleum.common.blocks.metal.BlockTypes_IPMetalDevice;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockIPMetalDevice.class)
public abstract class MixinIPMetalDevice {
    @Inject(method = "canIEBlockBePlaced", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectRejectAtBuildHeight(World world, BlockPos pos, IBlockState newState, EnumFacing side, float hitX, float hitY, float hitZ, EntityPlayer player, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItemDamage() == BlockTypes_IPMetalDevice.AUTOMATIC_LUBRICATOR.getMeta() && !world.isValid(pos.up())) { cir.setReturnValue(false); }
    }
}
