package com.immersiveconvergence.mixin.integration.immersivepetroleum.common.blocks;

import com.immersiveconvergence.core.integration.immersiveengineering.IEClearTanks;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IDirectionalTile;
import flaxbeard.immersivepetroleum.common.blocks.BlockIPTileProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockIPTileProvider.class) public abstract class MixinBlockIPTileProviderLate {
    @Inject(method = "onBlockActivated", at = @At("HEAD"), cancellable = true, require = 1) private void injectClearTanks(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (IEClearTanks.handle(world, pos, player, hand)) { cir.setReturnValue(true); }
    }

    @Redirect(method = "onBlockActivated", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/common/blocks/IEBlockInterfaces$IDirectionalTile;setFacing(Lnet/minecraft/util/EnumFacing;)V", remap = false), require = 1) private void redirectHammerRotation(IDirectionalTile tile, EnumFacing facing) {
        EnumFacing previous = tile.getFacing();
        tile.setFacing(facing);
        if (previous != facing) { tile.afterRotation(previous, facing); }
    }
}
