package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBottlingMachine;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityBottlingMachine.class)
public abstract class MixinIEPortsBottlingMachine {
    @Redirect(method = "getCapability", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityBottlingMachine;pos:I", remap = true), remap = false)
    private int redirectPosGetCapability(TileEntityBottlingMachine part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "hasCapability", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityBottlingMachine;pos:I", remap = true), remap = false)
    private int redirectPosHasCapability(TileEntityBottlingMachine part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "canFillTankFrom", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityBottlingMachine;pos:I", remap = true), remap = false)
    private int redirectPosCanFillTankFrom(TileEntityBottlingMachine part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "getAccessibleFluidTanks", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityBottlingMachine;pos:I", remap = true), remap = false)
    private int redirectPosGetAccessibleFluidTanks(TileEntityBottlingMachine part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "sigOutputDirections", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityBottlingMachine;pos:I", remap = true), remap = false)
    private int redirectPosSigOutputDirections(TileEntityBottlingMachine part) { return IEMultiblockRegistry.portPos(part); }
}
