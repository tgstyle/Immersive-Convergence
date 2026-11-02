package com.immersiveconvergence.mixin.integration.immersiveengineering.common.wires;

import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.core.integration.immersiveengineering.IEWireEvents;

import blusunrize.immersiveengineering.common.wires.WireCollisions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WireCollisions.class, remap = false) public abstract class WireCollisionsMixin {
    @Inject(method = "notifyBlockUpdate", at = @At("HEAD")) private static void immersiveconvergence$blocked(Level worldIn, BlockPos pos, BlockState newState, int flags, CallbackInfo ci) {
        if (!worldIn.isClientSide) { IEWireEvents.enter(ICWireCause.OBSTRUCTED); }
    }

    @Inject(method = "notifyBlockUpdate", at = @At("RETURN")) private static void immersiveconvergence$blockedDone(Level worldIn, BlockPos pos, BlockState newState, int flags, CallbackInfo ci) {
        if (!worldIn.isClientSide) { IEWireEvents.exit(); }
    }
}
