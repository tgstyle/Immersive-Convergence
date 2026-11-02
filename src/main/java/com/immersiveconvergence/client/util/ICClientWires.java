package com.immersiveconvergence.client.util;

import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.client.renderer.ICWireGeometry;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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

@SuppressWarnings("unused") @Mod.EventBusSubscriber(modid = ICLib.MODID, value = Dist.CLIENT) public final class ICClientWires {
    private static final Map<List<ICConnectionPoint>, ICWire> WIRES = new HashMap<>();
    private static final Map<ChunkPos, Set<ICWire>> BY_CHUNK = new HashMap<>();
    private static final Map<BlockPos, Set<ICWire>> BY_POS = new HashMap<>();
    private static final Map<List<ICConnectionPoint>, Vec3[]> CATENARIES = new HashMap<>();
    private static final Map<List<ICConnectionPoint>, List<ICWireGeometry.Segment>> SEGMENTS = new HashMap<>();
    private static final Map<SectionPos, Set<ICWireGeometry.Segment>> BY_SECTION = new HashMap<>();
    private static ResourceKey<Level> dimension;

    private ICClientWires() {}

    public static void apply(@Nonnull ResourceKey<Level> packetDimension, @Nonnull List<ICWire> added, @Nonnull List<ICWire> removed) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || !minecraft.level.dimension().equals(packetDimension)) { return; }
        for (SectionPos section : update(packetDimension, added, removed)) { minecraft.levelRenderer.setSectionDirty(section.x(), section.y(), section.z()); }
    }

    private static synchronized Set<SectionPos> update(ResourceKey<Level> packetDimension, List<ICWire> added, List<ICWire> removed) {
        if (!packetDimension.equals(dimension)) {
            clear();
            dimension = packetDimension;
        }
        Set<SectionPos> touched = new HashSet<>();
        for (ICWire wire : removed) { remove(key(wire), touched); }
        for (ICWire wire : added) { put(wire, touched); }
        return touched;
    }

    public static void repaint(@Nonnull ICWire wire) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) { return; }
        for (SectionPos section : rebuild(wire)) { minecraft.levelRenderer.setSectionDirty(section.x(), section.y(), section.z()); }
    }

    private static synchronized Set<SectionPos> rebuild(ICWire wire) {
        Set<SectionPos> touched = new HashSet<>();
        List<ICConnectionPoint> key = key(wire);
        ICWire known = WIRES.get(key);
        if (known == null) { return touched; }
        remove(key, touched);
        put(known, touched);
        return touched;
    }

    @Nonnull public static synchronized Collection<ICWire> getWiresIn(@Nonnull ChunkPos chunk) {
        Set<ICWire> wires = BY_CHUNK.get(chunk);
        return wires == null ? Collections.emptySet() : List.copyOf(wires);
    }

    @Nonnull public static synchronized Collection<ICWire> getWiresAt(@Nonnull BlockPos pos) {
        Set<ICWire> wires = BY_POS.get(pos);
        return wires == null ? Collections.emptySet() : List.copyOf(wires);
    }

    @Nonnull public static synchronized List<ICWireGeometry.Segment> getSegmentsIn(@Nonnull SectionPos section) {
        Set<ICWireGeometry.Segment> segments = BY_SECTION.get(section);
        return segments == null ? Collections.emptyList() : List.copyOf(segments);
    }

    @Nonnull public static synchronized Vec3[] getCatenary(@Nonnull ICWire wire, int segments) {
        List<ICConnectionPoint> key = key(wire);
        if (!wire.equals(WIRES.get(key))) { return wire.getCatenary(segments); }
        Vec3[] cached = CATENARIES.get(key);
        if (cached == null || cached.length != segments + 1) {
            cached = wire.getCatenary(segments);
            CATENARIES.put(key, cached);
        }
        return cached;
    }

    @SubscribeEvent public static synchronized void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof Level level) || !level.isClientSide() || !level.dimension().equals(dimension)) { return; }
        ChunkPos unloading = event.getChunk().getPos();
        Set<ICWire> wires = BY_CHUNK.get(unloading);
        if (wires == null) { return; }
        for (ICWire wire : new ArrayList<>(wires)) {
            if (outOfView(level, wire, unloading)) { remove(key(wire), new HashSet<>()); }
        }
    }

    @SubscribeEvent public static void onLevelUnload(LevelEvent.Unload event) { if (event.getLevel().isClientSide()) { clear(); } }

    @SubscribeEvent public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) { clear(); }

    private static boolean outOfView(Level level, ICWire wire, ChunkPos unloading) {
        for (ChunkPos chunk : wire.getChunks()) {
            if (!chunk.equals(unloading) && level.getChunkSource().hasChunk(chunk.x, chunk.z)) { return false; }
        }
        return true;
    }

    private static void put(ICWire wire, Set<SectionPos> touched) {
        List<ICConnectionPoint> key = key(wire);
        ICWire known = WIRES.get(key);
        if (wire.equals(known)) { return; }
        if (known != null) { remove(key, touched); }
        WIRES.put(key, wire);
        List<ICWireGeometry.Segment> segments = ICWireGeometry.build(wire, getCatenary(wire, ICWireGeometry.segments(wire)));
        SEGMENTS.put(key, segments);
        for (ICWireGeometry.Segment segment : segments) {
            BY_SECTION.computeIfAbsent(segment.getSection(), pos -> new HashSet<>()).add(segment);
            touched.add(segment.getSection());
        }
        for (ChunkPos chunk : wire.getChunks()) { BY_CHUNK.computeIfAbsent(chunk, pos -> new HashSet<>()).add(wire); }
        BY_POS.computeIfAbsent(wire.getA().position(), pos -> new HashSet<>()).add(wire);
        BY_POS.computeIfAbsent(wire.getB().position(), pos -> new HashSet<>()).add(wire);
    }

    private static void remove(List<ICConnectionPoint> key, Set<SectionPos> touched) {
        ICWire wire = WIRES.remove(key);
        if (wire == null) { return; }
        CATENARIES.remove(key);
        for (ICWireGeometry.Segment segment : SEGMENTS.remove(key)) {
            drop(BY_SECTION, segment.getSection(), segment);
            touched.add(segment.getSection());
        }
        for (ChunkPos chunk : wire.getChunks()) { drop(BY_CHUNK, chunk, wire); }
        drop(BY_POS, wire.getA().position(), wire);
        drop(BY_POS, wire.getB().position(), wire);
    }

    private static <K, V> void drop(Map<K, Set<V>> index, K at, V value) {
        Set<V> values = index.get(at);
        if (values == null) { return; }
        values.remove(value);
        if (values.isEmpty()) { index.remove(at); }
    }

    private static List<ICConnectionPoint> key(ICWire wire) { return Arrays.asList(wire.getA(), wire.getB()); }

    private static synchronized void clear() {
        WIRES.clear();
        BY_CHUNK.clear();
        BY_POS.clear();
        CATENARIES.clear();
        SEGMENTS.clear();
        BY_SECTION.clear();
        dimension = null;
    }
}
