package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblock;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.client.ClientProxy;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientProxy.class)
public abstract class MixinIEClientProxySound {
    @Redirect(method = "handleTileSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;getPos()Lnet/minecraft/util/math/BlockPos;", remap = true), remap = false)
    private BlockPos redirectSoundPos(TileEntity tile) {
        if (!(tile instanceof TileEntityMultiblockPart)) { return tile.getPos(); }
        TileEntityMultiblockPart<?> part = (TileEntityMultiblockPart<?>)tile;
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        if (template == null || part.pos < 0) { return tile.getPos(); }
        int[] declared = template.positionsNamed("sound");
        return declared == null ? tile.getPos() : part.getBlockPosForPos(declared[0]);
    }
}
