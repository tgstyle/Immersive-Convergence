package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySheetmetalTank;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntitySheetmetalTank.class)
public abstract class MixinIEPortsSheetmetalTank {
    @Redirect(method = "canDrainTankFrom", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntitySheetmetalTank;pos:I", remap = true), remap = false)
    private int redirectPosCanDrainTankFrom(TileEntitySheetmetalTank part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "canFillTankFrom", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntitySheetmetalTank;pos:I", remap = true), remap = false)
    private int redirectPosCanFillTankFrom(TileEntitySheetmetalTank part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "getAccessibleFluidTanks", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntitySheetmetalTank;pos:I", remap = true), remap = false)
    private int redirectPosGetAccessibleFluidTanks(TileEntitySheetmetalTank part) { return IEMultiblockRegistry.portPos(part); }
}
