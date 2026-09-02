package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityDieselGenerator;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityDieselGenerator.class)
public abstract class MixinIEPortsDieselGenerator {
    @Redirect(method = "getAccessibleFluidTanks", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lblusunrize/immersiveengineering/common/blocks/metal/TileEntityDieselGenerator;pos:I", remap = true), remap = false)
    private int redirectPosGetAccessibleFluidTanks(TileEntityDieselGenerator part) { return IEMultiblockRegistry.portPos(part); }
}
