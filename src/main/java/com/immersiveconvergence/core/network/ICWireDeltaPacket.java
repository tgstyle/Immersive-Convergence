package com.immersiveconvergence.core.network;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.client.ICClientWires;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ICWireDeltaPacket implements IMessage {
    private int dimension;
    private List<ICWire> added;
    private List<ICWire> removed;

    public ICWireDeltaPacket() {}

    private ICWireDeltaPacket(int dimension, List<ICWire> added, List<ICWire> removed) {
        this.dimension = dimension;
        this.added = added;
        this.removed = removed;
    }

    public static void added(@Nonnull World world, @Nonnull ICWire wire) { send(world, Collections.singletonList(wire), Collections.emptyList()); }

    public static void removed(@Nonnull World world, @Nonnull Collection<ICWire> wires) { send(world, Collections.emptyList(), new ArrayList<>(wires)); }

    private static void send(World world, List<ICWire> added, List<ICWire> removed) {
        if (ICMods.immersiveEngineering() || !(world instanceof WorldServer) || added.isEmpty() && removed.isEmpty()) { return; }
        PlayerChunkMap chunkMap = ((WorldServer)world).getPlayerChunkMap();
        Set<EntityPlayerMP> watchers = new HashSet<>();
        for (ICWire wire : added) { addWatchers(chunkMap, wire, watchers); }
        for (ICWire wire : removed) { addWatchers(chunkMap, wire, watchers); }
        if (watchers.isEmpty()) { return; }
        ICWireDeltaPacket packet = new ICWireDeltaPacket(world.provider.getDimension(), added, removed);
        for (EntityPlayerMP player : watchers) { ImmersiveConvergence.packetHandler.sendTo(packet, player); }
    }

    private static void addWatchers(PlayerChunkMap chunkMap, ICWire wire, Set<EntityPlayerMP> watchers) {
        for (ChunkPos chunk : wire.getChunks()) {
            PlayerChunkMapEntry entry = chunkMap.getEntry(chunk.x, chunk.z);
            if (entry != null) { watchers.addAll(entry.getWatchingPlayers()); }
        }
    }

    @Override public void fromBytes(ByteBuf buf) {
        dimension = buf.readInt();
        added = ICWireSyncPacket.readWires(buf);
        removed = ICWireSyncPacket.readWires(buf);
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(dimension);
        ICWireSyncPacket.writeWires(buf, added);
        ICWireSyncPacket.writeWires(buf, removed);
    }

    public static class Handler implements IMessageHandler<ICWireDeltaPacket, IMessage> {
        @Override public IMessage onMessage(ICWireDeltaPacket message, MessageContext ctx) {
            ImmersiveConvergence.proxy.scheduleClient(() -> ICClientWires.apply(message.dimension, message.added, message.removed));
            return null;
        }
    }
}
