package com.immersiveconvergence.mixin.ip.common;

import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityAutoLubricator;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityAutoLubricator.class)
public abstract class MixinIPLubricator {
    @Redirect(method = "update", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lflaxbeard/immersivepetroleum/common/blocks/metal/TileEntityAutoLubricator;predictablyDraining:Z", remap = false))
    private boolean redirectNeverPredictDrain(TileEntityAutoLubricator lubricator) { return false; }
}
