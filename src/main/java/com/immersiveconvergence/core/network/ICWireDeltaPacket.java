package com.immersiveconvergence.core.network;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.network.INetworkMessage;
import com.immersiveconvergence.client.ICClientWires;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public final class ICWireDeltaPacket implements INetworkMessage {
    private final ResourceKey<Level> dimension;
    private final List<ICWire> added;
    private final List<ICWire> removed;

    private ICWireDeltaPacket(ResourceKey<Level> dimension, List<ICWire> added, List<ICWire> removed) {
        this.dimension = dimension;
        this.added = added;
        this.removed = removed;
    }

    public ICWireDeltaPacket(FriendlyByteBuf buf) { this(buf.readResourceKey(Registries.DIMENSION), ICWireCodec.read(buf), ICWireCodec.read(buf)); }

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

    @Override public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        ICWireCodec.write(buf, added);
        ICWireCodec.write(buf, removed);
    }

    @Override public void process(Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection().getReceptionSide().isServer()) { return; }
        context.get().enqueueWork(() -> ICClientWires.apply(dimension, added, removed));
    }
}
