package com.immersiveconvergence.common.energy;

import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.IICConnector;
import com.immersiveconvergence.api.wires.IICEnergyConnector;
import com.immersiveconvergence.common.energy.wires.ICEnergyNode;
import com.immersiveconvergence.common.energy.wires.ICWireGraph;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class IEWireGraph implements ICWireGraph {
    private final World world;
    private static final int REBUILD_TICKS = 20;
    private static final Map<Integer, Integer> EPOCHS = new HashMap<>();
    private final Map<ICWire, Hop> seen = new HashMap<>();

    public IEWireGraph(@Nonnull World world) { this.world = world; }

    public static void invalidate(@Nonnull World world) {
        if (!world.isRemote) { EPOCHS.merge(world.provider.getDimension(), 1, Integer::sum); }
    }

    static int stamp(@Nonnull World world) { return (int)(world.getTotalWorldTime() / REBUILD_TICKS) + 65537 * EPOCHS.getOrDefault(world.provider.getDimension(), 0); }

    @Nonnull @Override public Set<ICConnectionPoint> pointsAt(@Nonnull BlockPos pos) { return connections(pos).isEmpty() ? Collections.emptySet() : Collections.singleton(new ICConnectionPoint(pos, 0)); }

    @Nonnull @Override public Integer component(@Nonnull ICConnectionPoint point) { return 0; }

    @Override public int version(int component) { return stamp(world); }

    @Override public boolean passes(@Nonnull ICConnectionPoint point) {
        IImmersiveConnectable connectable = ApiUtils.toIIC(point.getPos(), world);
        if (connectable instanceof IICConnector) { return ((IICConnector)connectable).passes(point); }
        return connectable != null && connectable.allowEnergyToPass(null);
    }

    @Nonnull @Override public Collection<ICWire> wiresAt(@Nonnull ICConnectionPoint point) {
        List<ICWire> wires = new ArrayList<>();
        BlockPos pos = point.getPos();
        for (Connection connection : connections(pos)) {
            if (connection.cableType == null || !connection.cableType.isEnergyWire()) { continue; }
            BlockPos far = connection.start.equals(pos) ? connection.end : connection.start;
            ICWire wire = new ICWire(point, Vec3d.ZERO, new ICConnectionPoint(far, 0), Vec3d.ZERO, IEWireBridge.required(connection.cableType));
            if (!seen.containsKey(wire)) { seen.put(wire, hop(connection)); }
            wires.add(wire);
        }
        return wires;
    }

    @Nonnull @Override public ICConnectionPoint flowPoint(@Nonnull ICConnectionPoint point) { return new ICConnectionPoint(point.getPos(), 0); }

    @Override public boolean sameHop(@Nonnull ICConnectionPoint wanted, @Nullable ICConnectionPoint hop) { return hop != null && wanted.getPos().equals(hop.getPos()); }

    @Override public boolean intact(@Nonnull ICWire wire) {
        if (seen.containsKey(wire)) { return true; }
        BlockPos pos = wire.getA().getPos();
        BlockPos far = wire.getB().getPos();
        for (Connection connection : connections(pos)) {
            if (connection.cableType == null || !connection.cableType.isEnergyWire()) { continue; }
            if (!far.equals(connection.start.equals(pos) ? connection.end : connection.start)) { continue; }
            if (!IEWireBridge.required(connection.cableType).equals(wire.getType())) { continue; }
            seen.put(wire, hop(connection));
            return true;
        }
        return false;
    }

    @Nonnull @Override public Map<ICConnectionPoint, Double> getLinks(@Nonnull ICConnectionPoint point) { return Collections.emptyMap(); }

    @Nullable @Override public ICEnergyNode node(@Nonnull World world, @Nonnull ICConnectionPoint point) {
        IImmersiveConnectable connectable = ApiUtils.toIIC(point.getPos(), world);
        if (connectable instanceof TileEntity && ((TileEntity)connectable).isInvalid()) { return null; }
        if (connectable instanceof IICEnergyConnector) { return ICWireGraph.energyNode((IICEnergyConnector)connectable, point); }
        if (connectable instanceof IICConnector) { return null; }
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

    private Hop hop(Connection connection) { return new Hop(connection, ImmersiveNetHandler.INSTANCE.getReverseConnection(world.provider.getDimension(), connection)); }

    private static final class Hop {
        private final Connection connection;
        private final Connection reverse;

        private Hop(Connection connection, @Nullable Connection reverse) {
            this.connection = connection;
            this.reverse = reverse;
        }
    }

    private static final class IENode implements ICEnergyNode {
        private final IImmersiveConnectable connectable;

        private IENode(IImmersiveConnectable connectable) { this.connectable = connectable; }

        @Override public boolean isEnergyOutput() { return connectable.isEnergyOutput(); }

        @Override public int outputEnergy(int amount, boolean simulate) { return connectable.outputEnergy(amount, simulate, 0); }

        @Override public void onEnergyPassthrough(int amount) { connectable.onEnergyPassthrough(amount); }
    }
}
