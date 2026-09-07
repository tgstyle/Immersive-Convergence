package com.immersiveconvergence.mixin.ip.common;

import flaxbeard.immersivepetroleum.common.network.ConsumeBoatFuelPacket;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ConsumeBoatFuelPacket.Handler.class, remap = false)
public abstract class MixinIPBoatFuel {
    @Inject(method = "onMessage(Lflaxbeard/immersivepetroleum/common/network/ConsumeBoatFuelPacket;Lnet/minecraftforge/fml/common/network/simpleimpl/MessageContext;)Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;", at = @At("HEAD"), remap = false)
    private void injectClampConsumedAmount(ConsumeBoatFuelPacket message, MessageContext ctx, CallbackInfoReturnable<IMessage> cir) {
        if (message.amount < 0) { message.amount = 0; }
    }
}
