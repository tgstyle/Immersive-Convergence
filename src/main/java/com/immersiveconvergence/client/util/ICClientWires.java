package com.immersiveconvergence.client.util;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.client.models.ICWireEnd;

import com.google.common.collect.ImmutableSet;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused") @Mod.EventBusSubscriber(modid = ImmersiveConvergence.MODID, value = Side.CLIENT) public final class ICClientWires {
    private static final Map<List<ICConnectionPoint>, ICWire> WIRES = new HashMap<>();
    private static final Map<ChunkPos, Set<ICWire>> BY_CHUNK = new HashMap<>();
    private static final Map<BlockPos, Set<ICWire>> BY_POS = new HashMap<>();
    private static final Map<List<ICConnectionPoint>, Vec3d[]> CATENARIES = new HashMap<>();
    private static int dimension;

    private ICClientWires() {}

    public static void apply(int packetDimension, @Nonnull List<ICWire> added, @Nonnull List<ICWire> removed) {
        World world = Minecraft.getMinecraft().world;
        if (world == null || world.provider.getDimension() != packetDimension) { return; }
        for (BlockPos pos : update(packetDimension, added, removed)) { markForRender(world, pos); }
    }

    public static void repaint(@Nonnull ICWire wire) {
        World world = Minecraft.getMinecraft().world;
        if (world == null) { return; }
        markForRender(world, wire.getA().getPos());
        markForRender(world, wire.getB().getPos());
    }

    private static void markForRender(World world, BlockPos pos) { world.markBlockRangeForRenderUpdate(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ()); }

    private static synchronized Set<BlockPos> update(int packetDimension, List<ICWire> added, List<ICWire> removed) {
        if (dimension != packetDimension) {
            clear();
            dimension = packetDimension;
        }
        Set<BlockPos> touched = new HashSet<>();
        for (ICWire wire : removed) { remove(key(wire), touched); }
        for (ICWire wire : added) { put(wire, touched); }
        return touched;
    }

    @Nonnull public static synchronized Set<ICWireEnd> getEnds(@Nonnull BlockPos pos) {
        Set<ICWire> wires = BY_POS.get(pos);
        if (wires == null) { return Collections.emptySet(); }
        ImmutableSet.Builder<ICWireEnd> ends = ImmutableSet.builder();
        for (ICWire wire : wires) {
            if (wire.getA().getPos().equals(pos)) { ends.add(new ICWireEnd(wire, true)); }
            if (wire.getB().getPos().equals(pos)) { ends.add(new ICWireEnd(wire, false)); }
        }
        return ends.build();
    }

    @Nonnull public static synchronized Collection<ICWire> getWiresIn(@Nonnull ChunkPos chunk) {
        Set<ICWire> wires = BY_CHUNK.get(chunk);
        return wires == null ? Collections.emptyList() : new ArrayList<>(wires);
    }

    @Nonnull public static synchronized Collection<ICWire> getWiresAt(@Nonnull BlockPos pos) {
        Set<ICWire> wires = BY_POS.get(pos);
        return wires == null ? Collections.emptyList() : new ArrayList<>(wires);
    }

    @Nonnull public static synchronized Vec3d[] getCatenary(@Nonnull ICWire wire, int segments) {
        List<ICConnectionPoint> key = key(wire);
        if (!wire.equals(WIRES.get(key))) { return wire.getCatenary(segments); }
        Vec3d[] cached = CATENARIES.get(key);
        if (cached == null || cached.length != segments + 1) {
            cached = wire.getCatenary(segments);
            CATENARIES.put(key, cached);
        }
        return cached;
    }

    @SubscribeEvent public static synchronized void onChunkUnload(ChunkEvent.Unload event) {
        World world = event.getWorld();
        if (!world.isRemote || world.provider.getDimension() != dimension) { return; }
        ChunkPos unloading = event.getChunk().getPos();
        Set<ICWire> wires = BY_CHUNK.get(unloading);
        if (wires == null) { return; }
        for (ICWire wire : new ArrayList<>(wires)) {
            if (outOfView(world, wire, unloading)) { remove(key(wire), new HashSet<>()); }
        }
    }

    @SubscribeEvent public static void onWorldUnload(WorldEvent.Unload event) { if (event.getWorld().isRemote) { clear(); } }

    @SubscribeEvent public static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) { Minecraft.getMinecraft().addScheduledTask(ICClientWires::clear); }

    private static boolean outOfView(World world, ICWire wire, ChunkPos unloading) {
        for (ChunkPos chunk : wire.getChunks()) {
            if (!chunk.equals(unloading) && world.getChunkProvider().getLoadedChunk(chunk.x, chunk.z) != null) { return false; }
        }
        return true;
    }

    private static void put(ICWire wire, Set<BlockPos> touched) {
        List<ICConnectionPoint> key = key(wire);
        ICWire known = WIRES.get(key);
        if (wire.equals(known)) { return; }
        if (known != null) { remove(key, touched); }
        WIRES.put(key, wire);
        touched.add(wire.getA().getPos());
        touched.add(wire.getB().getPos());
        for (ChunkPos chunk : wire.getChunks()) { BY_CHUNK.computeIfAbsent(chunk, pos -> new HashSet<>()).add(wire); }
        BY_POS.computeIfAbsent(wire.getA().getPos(), pos -> new HashSet<>()).add(wire);
        BY_POS.computeIfAbsent(wire.getB().getPos(), pos -> new HashSet<>()).add(wire);
    }

    private static void remove(List<ICConnectionPoint> key, Set<BlockPos> touched) {
        ICWire wire = WIRES.remove(key);
        if (wire == null) { return; }
        touched.add(wire.getA().getPos());
        touched.add(wire.getB().getPos());
        CATENARIES.remove(key);
        for (ChunkPos chunk : wire.getChunks()) { drop(BY_CHUNK, chunk, wire); }
        drop(BY_POS, wire.getA().getPos(), wire);
        drop(BY_POS, wire.getB().getPos(), wire);
    }

    private static <K> void drop(Map<K, Set<ICWire>> index, K at, ICWire wire) {
        Set<ICWire> wires = index.get(at);
        if (wires == null) { return; }
        wires.remove(wire);
        if (wires.isEmpty()) { index.remove(at); }
    }

    private static List<ICConnectionPoint> key(ICWire wire) { return Arrays.asList(wire.getA(), wire.getB()); }

    private static synchronized void clear() {
        WIRES.clear();
        BY_CHUNK.clear();
        BY_POS.clear();
        CATENARIES.clear();
    }
}
