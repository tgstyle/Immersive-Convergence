package com.immersiveconvergence.common.energy;

import com.immersiveconvergence.api.wires.ICConnectResult;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICInternalLink;
import com.immersiveconvergence.api.wires.ICTargetingInfo;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.IICConnector;
import com.immersiveconvergence.api.wires.IICWireNetwork;
import com.immersiveconvergence.api.wires.connectors.ICTileEntityConnectable;
import com.immersiveconvergence.common.energy.wires.ICWireWriter;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class IEWireView implements IICWireNetwork {
    private static final Map<World, IEWireLayout> LAYOUTS = Collections.synchronizedMap(new WeakHashMap<>());
    private final World world;

    public IEWireView(@Nonnull World world) { this.world = world; }

    @Nonnull @Override public Set<ICConnectionPoint> pointsAt(@Nonnull BlockPos pos) {
        Set<ICConnectionPoint> points = new HashSet<>();
        for (Connection connection : connections(pos)) {
            if (connection.cableType != null) { points.add(pointOf(world, pos, connection.start.equals(pos) ? connection.end : connection.start, IEWireBridge.required(connection.cableType))); }
        }
        IICConnector connector = connectorAt(world, pos);
        if (connector != null) {
            for (ICInternalLink link : connector.links()) {
                points.add(link.getFirst());
                points.add(link.getSecond());
            }
        }
        return points;
    }

    @Nonnull @Override public Collection<ICWire> wiresAt(@Nonnull ICConnectionPoint point) {
        List<ICWire> wires = new ArrayList<>();
        for (Connection connection : connections(point.getPos())) {
            ICWire wire = wireOf(world, connection);
            if (wire != null && wire.touches(point)) { wires.add(wire); }
        }
        return wires;
    }

    @Nullable @Override public ICWire wire(@Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second) {
        for (ICWire wire : wiresAt(first)) {
            if (wire.touches(second)) { return wire; }
        }
        return null;
    }

    @Nonnull @Override public Collection<ICWire> wiresIn(@Nonnull ChunkPos chunk) { return layout().wiresIn(chunk); }

    @Nullable @Override public Integer component(@Nonnull ICConnectionPoint point) { return layout().component(point); }

    @Nonnull @Override public Set<ICConnectionPoint> componentPoints(int component) { return layout().componentPoints(component); }

    @Override public int version(int component) { return IEWireGraph.stamp(world); }

    @Nullable @Override public IICConnector connector(@Nonnull ICConnectionPoint point) { return connectorAt(world, point.getPos()); }

    @Nullable @Override public ICConnectionPoint pointFor(@Nonnull BlockPos pos, @Nonnull ICWireType type, @Nonnull ICTargetingInfo target) { return IEWireBridge.coilWiring().point(world, pos, type, target); }

    @Nonnull @Override public ICConnectResult connect(@Nonnull ICConnectionPoint first, @Nonnull ICTargetingInfo firstTarget, @Nonnull ICConnectionPoint second, @Nonnull ICTargetingInfo secondTarget, @Nonnull ICWireType type) { return ICWireWriter.connect(world, IEWireBridge.coilWiring(), first, firstTarget, second, secondTarget, type); }

    @Override public boolean cut(@Nonnull ICWire wire, @Nonnull ICWireCause cause) {
        requireServer();
        IEWireEvents.enter(cause);
        try { return IEWireBridge.unlink(world, wire.getA().getPos(), wire.getB().getPos()); }
        finally { IEWireEvents.exit(); }
    }

    @Override public void removeAt(@Nonnull BlockPos pos, @Nonnull ICWireCause cause) {
        requireServer();
        IEWireEvents.enter(cause);
        try { ImmersiveNetHandler.INSTANCE.clearAllConnectionsFor(pos, world, true); }
        finally { IEWireEvents.exit(); }
    }

    private void requireServer() {
        if (world.isRemote) { throw new IllegalStateException("Wires are written on the server"); }
    }

    @Nullable public static ICWire wireOf(@Nonnull World world, @Nonnull Connection connection) {
        if (connection.cableType == null) { return null; }
        ICWireType type = IEWireBridge.required(connection.cableType);
        ICConnectionPoint first = pointOf(world, connection.start, connection.end, type);
        ICConnectionPoint second = pointOf(world, connection.end, connection.start, type);
        if (first.equals(second)) { return null; }
        return new ICWire(first, offsetOf(world, connection.start, connection), second, offsetOf(world, connection.end, connection), type);
    }

    @Nonnull private static ICConnectionPoint pointOf(World world, BlockPos pos, BlockPos far, ICWireType type) {
        IImmersiveConnectable connectable = ApiUtils.toIIC(pos, world, false);
        return connectable instanceof ICTileEntityConnectable ? ((ICTileEntityConnectable)connectable).pointToward(far, type) : new ICConnectionPoint(pos, 0);
    }

    @Nonnull private static Vec3d offsetOf(World world, BlockPos pos, Connection connection) {
        IImmersiveConnectable connectable = ApiUtils.toIIC(pos, world, false);
        return connectable == null ? Vec3d.ZERO : connectable.getConnectionOffset(connection);
    }

    @Nullable private static IICConnector connectorAt(World world, BlockPos pos) {
        IImmersiveConnectable connectable = ApiUtils.toIIC(pos, world, false);
        boolean valid = connectable instanceof IICConnector && !(connectable instanceof TileEntity && ((TileEntity)connectable).isInvalid());
        return valid ? (IICConnector)connectable : null;
    }

    @Nonnull private Set<Connection> connections(BlockPos pos) {
        Set<Connection> connections = ImmersiveNetHandler.INSTANCE.getConnections(world, pos);
        return connections == null ? Collections.emptySet() : connections;
    }

    private IEWireLayout layout() {
        int stamp = IEWireGraph.stamp(world);
        IEWireLayout layout = LAYOUTS.get(world);
        if (layout == null || layout.stamp() != stamp) {
            layout = new IEWireLayout(world, stamp);
            LAYOUTS.put(world, layout);
        }
        return layout;
    }
}
