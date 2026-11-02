package com.immersiveconvergence.mixin.integration.immersiveengineering.api.wires.localhandlers;

import com.immersiveconvergence.core.integration.immersiveengineering.IEWireEvents;

import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.localhandlers.WireDamageHandler;
import blusunrize.immersiveengineering.api.wires.localhandlers.WireDamageHandler.IShockingWire;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WireDamageHandler.class, remap = false) public abstract class WireDamageHandlerMixin {
    @Redirect(method = "onCollided", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/wires/localhandlers/WireDamageHandler$IShockingWire;getDamageAmount(Lnet/minecraft/world/entity/Entity;Lblusunrize/immersiveengineering/api/wires/Connection;I)F")) private float immersiveconvergence$shock(IShockingWire wire, Entity e, Connection c, int energy) { return IEWireEvents.shock(wire, e, c, energy); }

    @ModifyVariable(method = "onCollided", at = @At("STORE"), name = "factor") private double immersiveconvergence$energy(double factor) { return IEWireEvents.energyFactor(factor); }
}
