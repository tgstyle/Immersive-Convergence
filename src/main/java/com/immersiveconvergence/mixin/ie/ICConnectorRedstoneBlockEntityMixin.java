package com.immersiveconvergence.mixin.ie;

import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.connectors.ICConnectorRedstoneBlockEntity;
import com.immersiveconvergence.common.ie.IEWireBridge;

import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.redstone.IRedstoneConnector;
import blusunrize.immersiveengineering.api.wires.redstone.RedstoneNetworkHandler;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

@Mixin(value = ICConnectorRedstoneBlockEntity.class, remap = false) public abstract class ICConnectorRedstoneBlockEntityMixin implements IRedstoneConnector {
    @Shadow(remap = false) public abstract void redstoneInput(byte[] signals, @Nonnull ICConnectionPoint cp);
    @Shadow(remap = false) public abstract void redstoneChanged();

    @Override public Collection<ResourceLocation> getRequestedHandlers() { return List.of(RedstoneNetworkHandler.ID); }

    @Override public void onChange(ConnectionPoint cp, RedstoneNetworkHandler handler) { redstoneChanged(); }

    @Override public void updateInput(byte[] signals, ConnectionPoint cp) { redstoneInput(signals, IEWireBridge.point(cp)); }
}
