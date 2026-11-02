package com.immersiveconvergence.mixin.integration.immersiveengineering.api.wires.localhandlers;

import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.core.integration.immersiveengineering.IEWireEvents;

import blusunrize.immersiveengineering.api.wires.localhandlers.EnergyTransferHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EnergyTransferHandler.class, remap = false) public abstract class EnergyTransferHandlerMixin {
    @Inject(method = "burnOverloaded", at = @At("HEAD")) private void immersiveconvergence$burning(CallbackInfo ci) { IEWireEvents.enter(ICWireCause.OVERLOAD); }

    @Inject(method = "burnOverloaded", at = @At("RETURN")) private void immersiveconvergence$burned(CallbackInfo ci) { IEWireEvents.exit(); }
}
