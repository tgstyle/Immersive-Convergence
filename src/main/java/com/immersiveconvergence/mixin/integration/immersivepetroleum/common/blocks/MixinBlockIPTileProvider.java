package com.immersiveconvergence.mixin.integration.immersivepetroleum.common.blocks;

import com.immersiveconvergence.api.client.split.SplitModelProperties;
import com.immersiveconvergence.api.util.ICUtils;
import flaxbeard.immersivepetroleum.common.blocks.BlockIPMetalMultiblocks;
import flaxbeard.immersivepetroleum.common.blocks.BlockIPTileProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockIPTileProvider.class) public abstract class MixinBlockIPTileProvider {
    @Redirect(method = "getLightValue(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getTileEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/tileentity/TileEntity;", remap = true), remap = false, require = 1) private TileEntity redirectLightValueTile(IBlockAccess world, BlockPos pos) { return ICUtils.getExistingTileEntity(world, pos); }

    @Inject(method = "getExtendedState", at = @At("RETURN"), cancellable = true, remap = false, require = 1) private void injectGetExtendedState(IBlockState state, IBlockAccess world, BlockPos pos, CallbackInfoReturnable<IBlockState> cir) { if ((Object)this instanceof BlockIPMetalMultiblocks) { cir.setReturnValue(SplitModelProperties.withOffset(cir.getReturnValue(), world, pos)); } }
}
