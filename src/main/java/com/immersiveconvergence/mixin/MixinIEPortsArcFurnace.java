package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityArcFurnace;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityArcFurnace.class)
public abstract class MixinIEPortsArcFurnace {
    @Redirect(method = "getCapability", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityArcFurnace;pos:I", remap = true), remap = false)
    private int redirectPosGetCapability(TileEntityArcFurnace part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "hasCapability", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityArcFurnace;pos:I", remap = true), remap = false)
    private int redirectPosHasCapability(TileEntityArcFurnace part) { return IEMultiblockRegistry.portPos(part); }
}
