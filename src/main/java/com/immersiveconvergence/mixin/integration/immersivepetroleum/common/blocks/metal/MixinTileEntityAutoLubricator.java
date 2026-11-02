package com.immersiveconvergence.mixin.integration.immersivepetroleum.common.blocks.metal;

import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityAutoLubricator;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityAutoLubricator.class) public abstract class MixinTileEntityAutoLubricator {
    @Redirect(method = "update", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lflaxbeard/immersivepetroleum/common/blocks/metal/TileEntityAutoLubricator;predictablyDraining:Z", remap = false), require = 0, expect = 0) private boolean redirectNeverPredictDrain(TileEntityAutoLubricator lubricator) { return false; }
}
