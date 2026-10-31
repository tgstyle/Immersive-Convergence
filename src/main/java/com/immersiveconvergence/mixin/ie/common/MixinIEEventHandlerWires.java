package com.immersiveconvergence.mixin.ie.common;

import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.common.energy.IEWireBridge;
import com.immersiveconvergence.common.energy.IEWireEvents;

import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.common.EventHandler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EventHandler.class, remap = false)
public abstract class MixinIEEventHandlerWires {
    @Redirect(method = "onWorldTick", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/energy/wires/ImmersiveNetHandler;removeConnection(Lnet/minecraft/world/World;Lblusunrize/immersiveengineering/api/energy/wires/ImmersiveNetHandler$Connection;)V", ordinal = 0))
    private void immersiveconvergence$validate(ImmersiveNetHandler handler, World world, Connection con) { IEWireBridge.quietly(() -> handler.removeConnection(world, con)); }

    @Redirect(method = "onWorldTick", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/energy/wires/ImmersiveNetHandler;removeConnection(Lnet/minecraft/world/World;Lblusunrize/immersiveengineering/api/energy/wires/ImmersiveNetHandler$Connection;)V", ordinal = 1))
    private void immersiveconvergence$burn(ImmersiveNetHandler handler, World world, Connection con) {
        IEWireEvents.enter(ICWireCause.OVERLOAD);
        try { handler.removeConnection(world, con); }
        finally { IEWireEvents.exit(); }
    }
}
