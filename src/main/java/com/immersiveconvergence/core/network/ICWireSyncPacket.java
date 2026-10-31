package com.immersiveconvergence.core.network;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.client.ICClientWires;
import com.immersiveconvergence.common.energy.wires.ICWireSavedData;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@EventBusSubscriber(modid = ICLib.MODID)
public final class ICWireSyncPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ICWireSyncPacket> TYPE = new CustomPacketPayload.Type<>(ICLib.rl("wire_sync"));
    public static final StreamCodec<FriendlyByteBuf, ICWireSyncPacket> STREAM_CODEC = StreamCodec.of(ICWireSyncPacket::write, ICWireSyncPacket::new);
    private final ResourceKey<Level> dimension;
    private final List<ICWire> wires;

    private ICWireSyncPacket(ResourceKey<Level> dimension, Collection<ICWire> wires) {
        this.dimension = dimension;
        this.wires = List.copyOf(wires);
    }

    private ICWireSyncPacket(FriendlyByteBuf buf) { this(buf.readResourceKey(Registries.DIMENSION), ICWireCodec.read(buf)); }

    @SubscribeEvent public static void onChunkSent(ChunkWatchEvent.Sent event) {
        if (ICMods.immersiveEngineering()) { return; }
        ServerLevel level = event.getLevel();
        Collection<ICWire> watched = ICWireSavedData.get(level).wiresIn(event.getPos());
        if (!watched.isEmpty()) { PacketHandler.sendToPlayer(event.getPlayer(), new ICWireSyncPacket(level.dimension(), watched)); }
    }

    private static void write(FriendlyByteBuf buf, ICWireSyncPacket message) {
        buf.writeResourceKey(message.dimension);
        ICWireCodec.write(buf, message.wires);
    }

    @Override @Nonnull public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(ICWireSyncPacket message, IPayloadContext context) { context.enqueueWork(() -> ICClientWires.apply(message.dimension, message.wires, Collections.emptyList())); }
}
