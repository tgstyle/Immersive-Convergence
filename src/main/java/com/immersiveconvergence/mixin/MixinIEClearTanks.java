package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEClearTanks;

import blusunrize.immersiveengineering.common.blocks.BlockIETileProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockIETileProvider.class)
public abstract class MixinIEClearTanks {
    @Inject(method = "onBlockActivated", at = @At("HEAD"), cancellable = true)
    private void injectClearTanks(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (IEClearTanks.handle(world, pos, player, hand)) { cir.setReturnValue(true); }
    }
}
