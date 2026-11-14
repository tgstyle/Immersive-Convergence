package com.immersiveconvergence.common.energy;

import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.IICEnergyFitting;
import com.immersiveconvergence.api.wires.IICFitting;
import com.immersiveconvergence.common.energy.wires.ICEnergyNode;
import com.immersiveconvergence.common.energy.wires.ICNodeMemo;
import com.immersiveconvergence.common.energy.wires.ICWireGraph;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class IEWireGraph implements ICWireGraph {
    private final World world;
    private static final Map<Integer, Integer> EPOCHS = new HashMap<>();
    private static final Map<Integer, IEWireGraph> GRAPHS = new HashMap<>();
    private final int stamp;
    private final Map<ICWire, Hop> seen = new HashMap<>();
    private final Map<Connection, Hop> hops = new IdentityHashMap<>();
    private final ICNodeMemo nodes = new ICNodeMemo();

    private IEWireGraph(@Nonnull World world) {
        this.world = world;
        this.stamp = stamp(world);
    }

    public static void invalidate(@Nonnull World world) {
        if (!world.isRemote) { EPOCHS.merge(world.provider.getDimension(), 1, Integer::sum); }
    }

    @Nonnull public static IEWireGraph of(@Nonnull World world) {
        int dimension = world.provider.getDimension();
        IEWireGraph graph = GRAPHS.get(dimension);
        if (graph == null || graph.world != world || graph.stamp != stamp(world)) {
            graph = new IEWireGraph(world);
            GRAPHS.put(dimension, graph);
        }
        return graph;
    }

    public static void forget(@Nonnull World world) { GRAPHS.remove(world.provider.getDimension()); }

    static int stamp(@Nonnull World world) { return EPOCHS.getOrDefault(world.provider.getDimension(), 0); }

    @Nonnull @Override public Set<ICConnectionPoint> pointsAt(@Nonnull BlockPos pos) { return connections(pos).isEmpty() ? Collections.emptySet() : Collections.singleton(new ICConnectionPoint(pos, 0)); }

    @Nonnull @Override public Integer component(@Nonnull ICConnectionPoint point) { return 0; }

    @Override public int version(int component) { return stamp(world); }

    @Override public boolean passes(@Nonnull ICConnectionPoint point) {
        IImmersiveConnectable connectable = ApiUtils.toIIC(point.getPos(), world);
        if (connectable instanceof IICFitting) { return ((IICFitting)connectable).passes(point); }
        return connectable != null && connectable.allowEnergyToPass(null);
    }

    @Nonnull @Override public Collection<ICWire> wiresAt(@Nonnull ICConnectionPoint point) {
        List<ICWire> wires = new ArrayList<>();
        BlockPos pos = point.getPos();
        boolean canonical = point.getIndex() == 0;
        for (Connection connection : connections(pos)) {
            if (connection.cableType == null || !connection.cableType.isEnergyWire()) { continue; }
            Hop hop = canonical ? hops.get(connection) : null;
            if (hop == null) {
                BlockPos far = connection.start.equals(pos) ? connection.end : connection.start;
                ICWire wire = new ICWire(point, Vec3d.ZERO, new ICConnectionPoint(far, 0), Vec3d.ZERO, IEWireBridge.required(connection.cableType));
                hop = seen.get(wire);
                if (hop == null) {
                    hop = hop(connection, wire);
                    seen.put(wire, hop);
                }
                if (canonical) { hops.put(connection, hop); }
            }
            wires.add(hop.wire);
        }
        return wires;
    }

    @Nonnull @Override public ICConnectionPoint flowPoint(@Nonnull ICConnectionPoint point) { return new ICConnectionPoint(point.getPos(), 0); }

    @Override public boolean sameHop(@Nonnull ICConnectionPoint wanted, @Nullable ICConnectionPoint hop) { return hop != null && wanted.getPos().equals(hop.getPos()); }

    @Nonnull @Override public Map<ICConnectionPoint, Double> getLinks(@Nonnull ICConnectionPoint point) { return Collections.emptyMap(); }

    @Nullable @Override public ICEnergyNode node(@Nonnull World world, @Nonnull ICConnectionPoint point) { return nodes.resolve(world, point, IEWireGraph::load); }

    @Nullable private static ICEnergyNode load(World world, ICConnectionPoint point) {
        IImmersiveConnectable connectable = ApiUtils.toIIC(point.getPos(), world);
        if (connectable instanceof TileEntity && ((TileEntity)connectable).isInvalid()) { return null; }
        if (connectable instanceof IICEnergyFitting) { return ICWireGraph.energyNode((IICEnergyFitting)connectable, point); }
        if (connectable instanceof IICFitting) { return null; }
        return connectable == null ? null : new IENode(connectable);
    }

    @Override public double lossOf(@Nonnull ICWire wire) {
        Hop hop = seen.get(wire);
        return hop == null ? ICWireGraph.super.lossOf(wire) : Math.min(1, hop.connection.getBaseLoss());
    }

    @Override public int reserved(@Nonnull ICWire wire) {
        Hop hop = seen.get(wire);
        if (hop == null) { return 0; }
        Map<Connection, Integer> rates = ImmersiveNetHandler.INSTANCE.getTransferedRates(world.provider.getDimension());
        return rates.getOrDefault(hop.connection, 0) + (hop.reverse == null ? 0 : rates.getOrDefault(hop.reverse, 0));
    }

    @Override public void settle(@Nonnull Map<ICWire, Integer> used) {
        Map<Connection, Integer> rates = ImmersiveNetHandler.INSTANCE.getTransferedRates(world.provider.getDimension());
        for (Map.Entry<ICWire, Integer> entry : used.entrySet()) {
            Hop hop = seen.get(entry.getKey());
            if (hop != null) { rates.merge(hop.connection, entry.getValue(), Integer::sum); }
        }
    }

    @Nonnull private Set<Connection> connections(BlockPos pos) {
        Set<Connection> connections = ImmersiveNetHandler.INSTANCE.getConnections(world, pos);
        return connections == null ? Collections.emptySet() : connections;
    }

    private Hop hop(Connection connection, ICWire wire) { return new Hop(connection, ImmersiveNetHandler.INSTANCE.getReverseConnection(world.provider.getDimension(), connection), wire); }

    private static final class Hop {
        private final Connection connection;
        private final Connection reverse;
        private final ICWire wire;

        private Hop(Connection connection, @Nullable Connection reverse, ICWire wire) {
            this.connection = connection;
            this.reverse = reverse;
            this.wire = wire;
        }
    }

    private static final class IENode implements ICEnergyNode {
        private final IImmersiveConnectable connectable;

        private IENode(IImmersiveConnectable connectable) { this.connectable = connectable; }

        @Override public boolean valid() { return !(connectable instanceof TileEntity) || !((TileEntity)connectable).isInvalid(); }

        @Override public boolean isEnergyOutput() { return connectable.isEnergyOutput(); }

        @Override public int outputEnergy(int amount, boolean simulate) { return connectable.outputEnergy(amount, simulate, 0); }

        @Override public void onEnergyPassthrough(int amount) { connectable.onEnergyPassthrough(amount); }
    }
}
