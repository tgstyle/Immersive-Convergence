package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMetalPress;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityMetalPress.class)
public abstract class MixinIEPortsMetalPress {
    @Redirect(method = "getCapability", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityMetalPress;pos:I", remap = true), remap = false)
    private int redirectPosGetCapability(TileEntityMetalPress part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "hasCapability", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityMetalPress;pos:I", remap = true), remap = false)
    private int redirectPosHasCapability(TileEntityMetalPress part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "sigOutputDirections", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityMetalPress;pos:I", remap = true), remap = false)
    private int redirectPosSigOutputDirections(TileEntityMetalPress part) { return IEMultiblockRegistry.portPos(part); }
}
