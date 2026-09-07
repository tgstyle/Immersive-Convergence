package com.immersiveconvergence.mixin.ip.client;

import flaxbeard.immersivepetroleum.client.ClientProxy;
import flaxbeard.immersivepetroleum.common.network.MessageReservoirListSync;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MessageReservoirListSync.Handler.class)
public abstract class MixinIPReservoirSync {
    @Inject(method = "onMessageMain", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectKeepServerReservoirs(MessageReservoirListSync message, CallbackInfo ci) {
        if (!Minecraft.getMinecraft().isIntegratedServerRunning()) { return; }
        ClientProxy.handleReservoirManual();
        ci.cancel();
    }
}
