package com.immersiveconvergence.common.energy;

import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICInternalLink;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.IICConnector;

import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

final class IEWireLayout {
    private final Map<ChunkPos, Set<ICWire>> byChunk = new HashMap<>();
    private final Map<ICConnectionPoint, Integer> componentOf = new HashMap<>();
    private final Map<Integer, Set<ICConnectionPoint>> components = new HashMap<>();
    private final Map<ICConnectionPoint, Set<ICConnectionPoint>> adjacent = new TreeMap<>();
    private final int stamp;

    IEWireLayout(@Nonnull World world, int stamp) {
        this.stamp = stamp;
        for (Connection connection : ImmersiveNetHandler.INSTANCE.getAllConnections(world)) {
            ICWire wire = IEWireView.wireOf(world, connection);
            if (wire == null) { continue; }
            for (ChunkPos chunk : wire.getChunks()) { byChunk.computeIfAbsent(chunk, pos -> new HashSet<>()).add(wire); }
            join(wire.getA(), wire.getB());
        }
        for (TileEntity tile : new ArrayList<>(world.loadedTileEntityList)) {
            if (!(tile instanceof IICConnector) || tile.isInvalid()) { continue; }
            for (ICInternalLink link : ((IICConnector)tile).links()) { join(link.getFirst(), link.getSecond()); }
        }
        label();
    }

    int stamp() { return stamp; }

    @Nonnull Collection<ICWire> wiresIn(@Nonnull ChunkPos chunk) {
        Set<ICWire> wires = byChunk.get(chunk);
        return wires == null ? Collections.emptySet() : Collections.unmodifiableSet(wires);
    }

    @Nullable Integer component(@Nonnull ICConnectionPoint point) { return componentOf.get(point); }

    @Nonnull Set<ICConnectionPoint> componentPoints(int component) {
        Set<ICConnectionPoint> points = components.get(component);
        return points == null ? Collections.emptySet() : Collections.unmodifiableSet(points);
    }

    private void join(ICConnectionPoint first, ICConnectionPoint second) {
        adjacent.computeIfAbsent(first, point -> new HashSet<>()).add(second);
        adjacent.computeIfAbsent(second, point -> new HashSet<>()).add(first);
    }

    private void label() {
        int next = 0;
        for (ICConnectionPoint start : adjacent.keySet()) {
            if (componentOf.containsKey(start)) { continue; }
            Set<ICConnectionPoint> members = new HashSet<>();
            ArrayDeque<ICConnectionPoint> queue = new ArrayDeque<>();
            componentOf.put(start, next);
            members.add(start);
            queue.add(start);
            while (!queue.isEmpty()) {
                for (ICConnectionPoint near : adjacent.get(queue.poll())) {
                    if (componentOf.putIfAbsent(near, next) == null) {
                        members.add(near);
                        queue.add(near);
                    }
                }
            }
            components.put(next++, members);
        }
    }
}
