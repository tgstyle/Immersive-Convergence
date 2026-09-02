package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityRefinery;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityRefinery.class)
public abstract class MixinIEPortsRefinery {
    @Redirect(method = "canDrainTankFrom", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityRefinery;pos:I", remap = true), remap = false)
    private int redirectPosCanDrainTankFrom(TileEntityRefinery part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "canFillTankFrom", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityRefinery;pos:I", remap = true), remap = false)
    private int redirectPosCanFillTankFrom(TileEntityRefinery part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "getAccessibleFluidTanks", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityRefinery;pos:I", remap = true), remap = false)
    private int redirectPosGetAccessibleFluidTanks(TileEntityRefinery part) { return IEMultiblockRegistry.portPos(part); }
}
