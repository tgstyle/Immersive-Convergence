package com.immersiveconvergence.core.network;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.network.INetworkMessage;
import com.immersiveconvergence.client.ICClientWires;
import com.immersiveconvergence.common.energy.wires.ICWireSavedData;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = ICLib.MODID)
public final class ICWireSyncPacket implements INetworkMessage {
    private final ResourceKey<Level> dimension;
    private final List<ICWire> wires;

    private ICWireSyncPacket(ResourceKey<Level> dimension, Collection<ICWire> wires) {
        this.dimension = dimension;
        this.wires = List.copyOf(wires);
    }

    public ICWireSyncPacket(FriendlyByteBuf buf) { this(buf.readResourceKey(Registries.DIMENSION), ICWireCodec.read(buf)); }

    @SubscribeEvent public static void onChunkWatch(ChunkWatchEvent.Watch event) {
        if (ICMods.immersiveEngineering()) { return; }
        ServerLevel level = event.getLevel();
        Collection<ICWire> watched = ICWireSavedData.get(level).wiresIn(event.getPos());
        if (!watched.isEmpty()) { PacketHandler.sendToPlayer(event.getPlayer(), new ICWireSyncPacket(level.dimension(), watched)); }
    }

    @Override public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        ICWireCodec.write(buf, wires);
    }

    @Override public void process(Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection().getReceptionSide().isServer()) { return; }
        context.get().enqueueWork(() -> ICClientWires.apply(dimension, wires, Collections.emptyList()));
    }
}
