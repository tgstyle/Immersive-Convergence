package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.stone.TileEntityBlastFurnaceAdvanced;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityBlastFurnaceAdvanced.class)
public abstract class MixinIEPortsBlastFurnaceAdvanced {
    @Redirect(method = "getCapability", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/stone/TileEntityBlastFurnaceAdvanced;pos:I", remap = true), remap = false)
    private int redirectPosGetCapability(TileEntityBlastFurnaceAdvanced part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "hasCapability", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/stone/TileEntityBlastFurnaceAdvanced;pos:I", remap = true), remap = false)
    private int redirectPosHasCapability(TileEntityBlastFurnaceAdvanced part) { return IEMultiblockRegistry.portPos(part); }
}
