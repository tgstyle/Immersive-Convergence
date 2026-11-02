package com.immersiveconvergence.mixin.integration.immersiveengineering.api.wires;

import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.core.integration.immersiveengineering.IEWireEvents;

import blusunrize.immersiveengineering.api.wires.ConnectorBlockEntityHelper;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ConnectorBlockEntityHelper.class, remap = false) public abstract class ConnectorBlockEntityHelperMixin {
    @Inject(method = "remove", at = @At("HEAD")) private static void immersiveconvergence$broken(Level world, IImmersiveConnectable iic, CallbackInfo ci) {
        if (!world.isClientSide) { IEWireEvents.enter(ICWireCause.BLOCK_BROKEN); }
    }

    @Inject(method = "remove", at = @At("RETURN")) private static void immersiveconvergence$brokenDone(Level world, IImmersiveConnectable iic, CallbackInfo ci) {
        if (!world.isClientSide) { IEWireEvents.exit(); }
    }
}
