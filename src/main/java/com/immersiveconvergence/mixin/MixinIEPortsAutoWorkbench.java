package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAutoWorkbench;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityAutoWorkbench.class)
public abstract class MixinIEPortsAutoWorkbench {
    @Redirect(method = "getCapability", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityAutoWorkbench;pos:I", remap = true), remap = false)
    private int redirectPosGetCapability(TileEntityAutoWorkbench part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "hasCapability", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityAutoWorkbench;pos:I", remap = true), remap = false)
    private int redirectPosHasCapability(TileEntityAutoWorkbench part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "sigOutputDirections", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityAutoWorkbench;pos:I", remap = true), remap = false)
    private int redirectPosSigOutputDirections(TileEntityAutoWorkbench part) { return IEMultiblockRegistry.portPos(part); }
}
