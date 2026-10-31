package com.immersiveconvergence.common.ie;

import com.immersiveconvergence.api.wires.ICConnectResult;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICTargetingInfo;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.IICConnector;
import com.immersiveconvergence.api.wires.IICWireNetwork;
import com.immersiveconvergence.common.energy.wires.ICWireNetwork;
import com.immersiveconvergence.common.energy.wires.ICWireWriter;

import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;
import blusunrize.immersiveengineering.api.wires.WireType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class IEWireView implements IICWireNetwork {
    private static final Map<GlobalWireNetwork, Components> COMPONENTS = Collections.synchronizedMap(new WeakHashMap<>());
    private final Level level;
    private final GlobalWireNetwork network;

    public IEWireView(@Nonnull Level level) {
        this.level = level;
        this.network = GlobalWireNetwork.getNetwork(level);
    }

    @Nonnull @Override public Set<ICConnectionPoint> pointsAt(@Nonnull BlockPos pos) {
        Set<ICConnectionPoint> points = new HashSet<>();
        for (ConnectionPoint point : network.getAllConnectorsIn(new ChunkPos(pos))) {
            if (point.position().equals(pos) && local(point) != null) { points.add(IEWireBridge.point(point)); }
        }
        return points;
    }

    @Nonnull @Override public Collection<ICWire> wiresAt(@Nonnull ICConnectionPoint point) {
        List<ICWire> wires = new ArrayList<>();
        collect(IEWireBridge.ie(point), wires);
        return wires;
    }

    @Nullable @Override public ICWire wire(@Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second) { return IEWireBridge.wire(level, first, second); }

    @Nonnull @Override public Collection<ICWire> wiresIn(@Nonnull ChunkPos chunk) {
        int reach = (longestWire() + 2) / 16 + 1;
        Set<ICWire> wires = new HashSet<>();
        for (int x = chunk.x - reach; x <= chunk.x + reach; x++) {
            for (int z = chunk.z - reach; z <= chunk.z + reach; z++) {
                for (ConnectionPoint point : network.getAllConnectorsIn(new ChunkPos(x, z))) { collect(point, wires); }
            }
        }
        wires.removeIf(wire -> !wire.getChunks().contains(chunk));
        return wires;
    }

    @Nullable @Override public Integer component(@Nonnull ICConnectionPoint point) {
        LocalWireNetwork local = local(IEWireBridge.ie(point));
        return local == null ? null : components().id(local);
    }

    @Nonnull @Override public Set<ICConnectionPoint> componentPoints(int component) {
        Set<ICConnectionPoint> points = new HashSet<>();
        LocalWireNetwork local = components().net(component);
        if (local == null) { return points; }
        for (ConnectionPoint point : local.getConnectionPoints()) {
            if (!local.getConnections(point).isEmpty()) { points.add(IEWireBridge.point(point)); }
        }
        return points;
    }

    @Override public int version(int component) {
        LocalWireNetwork local = components().net(component);
        return local == null ? 0 : local.getVersion();
    }

    @Nullable @Override public IICConnector connector(@Nonnull ICConnectionPoint point) { return ICWireNetwork.connectorAt(level, point.position()); }

    @Nullable @Override public ICConnectionPoint pointFor(@Nonnull BlockPos pos, @Nonnull ICWireType type, @Nonnull ICTargetingInfo target) { return IEWireBridge.coilWiring().point(level, pos, type, target); }

    @Nonnull @Override public ICConnectResult connect(@Nonnull ICConnectionPoint first, @Nonnull ICTargetingInfo firstTarget, @Nonnull ICConnectionPoint second, @Nonnull ICTargetingInfo secondTarget, @Nonnull ICWireType type) { return ICWireWriter.connect(level, IEWireBridge.coilWiring(), first, second, type); }

    @Override public boolean cut(@Nonnull ICWire wire, @Nonnull ICWireCause cause) {
        requireServer();
        IEWireEvents.enter(cause);
        try { return IEWireBridge.unlink(level, wire); }
        finally { IEWireEvents.exit(); }
    }

    @Override public void removeAt(@Nonnull BlockPos pos, @Nonnull ICWireCause cause) {
        requireServer();
        IEWireEvents.enter(cause);
        try {
            for (ConnectionPoint point : network.getAllConnectorsIn(new ChunkPos(pos))) {
                if (!point.position().equals(pos)) { continue; }
                for (Connection connection : new ArrayList<>(IEWireBridge.connections(level, point))) {
                    if (!connection.isInternal()) { network.removeAndDropConnection(connection, pos, level); }
                }
            }
        }
        finally { IEWireEvents.exit(); }
    }

    private void requireServer() {
        if (level.isClientSide) { throw new IllegalStateException("Wires are written on the server"); }
    }

    @Nullable private LocalWireNetwork local(ConnectionPoint point) {
        LocalWireNetwork local = network.getNullableLocalNet(point);
        return local == null || local.getConnections(point).isEmpty() ? null : local;
    }

    private void collect(ConnectionPoint point, Collection<ICWire> into) {
        for (Connection connection : IEWireBridge.connections(level, point)) {
            if (!connection.isInternal()) { into.add(IEWireBridge.wire(connection)); }
        }
    }

    private static int longestWire() {
        int longest = 0;
        for (WireType wire : WireType.getValues()) {
            if (wire != null) { longest = Math.max(longest, wire.getMaxLength()); }
        }
        return longest;
    }

    private Components components() { return COMPONENTS.computeIfAbsent(network, key -> new Components()); }

    private static final class Components {
        private final Map<LocalWireNetwork, Integer> ids = new WeakHashMap<>();
        private final Map<Integer, WeakReference<LocalWireNetwork>> nets = new HashMap<>();
        private int next;

        int id(LocalWireNetwork local) {
            Integer known = ids.get(local);
            if (known != null) { return known; }
            nets.values().removeIf(reference -> reference.get() == null);
            int created = next++;
            ids.put(local, created);
            nets.put(created, new WeakReference<>(local));
            return created;
        }

        @Nullable LocalWireNetwork net(int id) {
            WeakReference<LocalWireNetwork> reference = nets.get(id);
            LocalWireNetwork local = reference == null ? null : reference.get();
            return local == null || !local.isValid() ? null : local;
        }
    }
}
