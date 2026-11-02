package com.immersiveconvergence.core.network;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.client.util.ICClientWires;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

public final class ICWireDeltaPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ICWireDeltaPacket> TYPE = new CustomPacketPayload.Type<>(ICLib.rl("wire_delta"));
    public static final StreamCodec<FriendlyByteBuf, ICWireDeltaPacket> STREAM_CODEC = StreamCodec.of(ICWireDeltaPacket::write, ICWireDeltaPacket::new);
    private final ResourceKey<Level> dimension;
    private final List<ICWire> added;
    private final List<ICWire> removed;

    private ICWireDeltaPacket(ResourceKey<Level> dimension, List<ICWire> added, List<ICWire> removed) {
        this.dimension = dimension;
        this.added = added;
        this.removed = removed;
    }

    private ICWireDeltaPacket(FriendlyByteBuf buf) { this(buf.readResourceKey(Registries.DIMENSION), ICWireCodec.read(buf), ICWireCodec.read(buf)); }

    public static void added(@Nonnull ServerLevel level, @Nonnull ICWire wire) { send(level, List.of(wire), List.of()); }

    public static void removed(@Nonnull ServerLevel level, @Nonnull Collection<ICWire> wires) { send(level, List.of(), List.copyOf(wires)); }

    private static void send(ServerLevel level, List<ICWire> added, List<ICWire> removed) {
        if (ICMods.immersiveEngineering() || added.isEmpty() && removed.isEmpty()) { return; }
        Set<ServerPlayer> watchers = new LinkedHashSet<>();
        for (ICWire wire : added) { addWatchers(level, wire, watchers); }
        for (ICWire wire : removed) { addWatchers(level, wire, watchers); }
        if (watchers.isEmpty()) { return; }
        ICWireDeltaPacket packet = new ICWireDeltaPacket(level.dimension(), added, removed);
        for (ServerPlayer player : watchers) { PacketHandler.sendToPlayer(player, packet); }
    }

    private static void addWatchers(ServerLevel level, ICWire wire, Set<ServerPlayer> watchers) {
        for (ChunkPos chunk : wire.getChunks()) { watchers.addAll(level.getChunkSource().chunkMap.getPlayers(chunk, false)); }
    }

    private static void write(FriendlyByteBuf buf, ICWireDeltaPacket message) {
        buf.writeResourceKey(message.dimension);
        ICWireCodec.write(buf, message.added);
        ICWireCodec.write(buf, message.removed);
    }

    @Override @Nonnull public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(ICWireDeltaPacket message, IPayloadContext context) { context.enqueueWork(() -> ICClientWires.apply(message.dimension, message.added, message.removed)); }
}
