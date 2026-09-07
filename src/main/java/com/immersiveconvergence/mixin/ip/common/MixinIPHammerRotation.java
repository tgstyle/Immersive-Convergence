package com.immersiveconvergence.mixin.ip.common;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IDirectionalTile;
import flaxbeard.immersivepetroleum.common.blocks.BlockIPTileProvider;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockIPTileProvider.class)
public abstract class MixinIPHammerRotation {
    @Redirect(method = "onBlockActivated", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/common/blocks/IEBlockInterfaces$IDirectionalTile;setFacing(Lnet/minecraft/util/EnumFacing;)V", remap = false))
    private void redirectHammerRotation(IDirectionalTile tile, EnumFacing facing) {
        EnumFacing previous = tile.getFacing();
        tile.setFacing(facing);
        if (previous != facing) { tile.afterRotation(previous, facing); }
    }
}
