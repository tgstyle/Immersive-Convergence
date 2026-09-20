package com.immersiveconvergence.mixin.ie.common;

import com.immersiveconvergence.api.util.ICUtils;

import blusunrize.immersiveengineering.common.blocks.metal.BlockConnector;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockConnector.class)
public abstract class MixinIEConnectorLightTile {
    @Redirect(method = "getLightOpacity(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getTileEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/tileentity/TileEntity;", remap = true), remap = false, require = 1)
    private TileEntity redirectLightOpacityTile(IBlockAccess world, BlockPos pos) { return ICUtils.getExistingTileEntity(world, pos); }
}
