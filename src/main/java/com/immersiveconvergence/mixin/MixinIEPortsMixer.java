package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMixer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityMixer.class)
public abstract class MixinIEPortsMixer {
    @Redirect(method = "getCapability", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityMixer;pos:I", remap = true), remap = false)
    private int redirectPosGetCapability(TileEntityMixer part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "hasCapability", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityMixer;pos:I", remap = true), remap = false)
    private int redirectPosHasCapability(TileEntityMixer part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "getAccessibleFluidTanks", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityMixer;pos:I", remap = true), remap = false)
    private int redirectPosGetAccessibleFluidTanks(TileEntityMixer part) { return IEMultiblockRegistry.portPos(part); }
}
