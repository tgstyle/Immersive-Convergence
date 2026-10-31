package com.immersiveconvergence.common.energy;

import com.immersiveconvergence.ImmersiveConvergence;
import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICTargetingInfo;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCategory;
import com.immersiveconvergence.api.wires.ICWireRole;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.connectors.ICTileEntityConnectable;
import com.immersiveconvergence.common.energy.wires.ICWireCoils;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.WireApi;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public final class IEWireBridge {
    private static final Map<String, ICWireType> LATE = new ConcurrentHashMap<>();
    private static boolean linking;

    private IEWireBridge() {}

    public static boolean linking() { return linking; }

    public static void quietly(@Nonnull Runnable change) {
        linking = true;
        try { change.run(); }
        finally { linking = false; }
    }

    public static boolean prune(@Nonnull World world, @Nonnull BlockPos first, @Nonnull BlockPos second) {
        linking = true;
        try { return unlink(world, first, second); }
        finally { linking = false; }
    }

    public static void adoptWireTypes() {
        for (WireType wire : new ArrayList<>(WireType.getValues())) {
            if (wire != null) { required(wire); }
        }
    }

    public static void mirrorWireTypes() {
        adoptWireTypes();
        for (ICWireType type : ICWireType.values()) {
            if (!hasIEType(type.getUniqueName())) { WireApi.registerWireType(new ICIEWireType(type)); }
        }
    }

    private static boolean hasIEType(String name) {
        for (WireType wire : WireType.getValues()) {
            if (wire != null && wire.getUniqueName().equals(name)) { return true; }
        }
        return false;
    }

    @Nullable public static ICWireType of(@Nullable WireType wire) { return wire == null ? null : required(wire); }

    @Nonnull public static ICWireType required(@Nonnull WireType wire) {
        if (wire instanceof ICIEWireType) { return ((ICIEWireType)wire).icType(); }
        ICWireType known = ICWireType.byName(wire.getUniqueName());
        if (known != null) { return known; }
        if (ICWireType.frozen()) { return LATE.computeIfAbsent(wire.getUniqueName(), name -> mirror(wire)); }
        return ICWireType.register(mirror(wire));
    }

    @Nonnull private static ICWireType mirror(@Nonnull WireType wire) {
        int maxLength = wire.getMaxLength();
        double lossPerBlock = maxLength > 0 ? wire.getLossRatio() / maxLength : 0;
        return new ICWireType(wire.getUniqueName(), category(wire), wire::getTransferRate, maxLength, lossPerBlock,
                wire::getRenderDiameter, wire.getSlack(), wire.getColour(null), wire::getWireCoil);
    }

    @Nullable private static ICWireCategory category(@Nonnull WireType wire) {
        String name = wire.getCategory();
        ICWireCategory known = ICWireCategory.byName(name);
        if (known != null || name == null || ICWireType.frozen()) { return known; }
        return ICWireCategory.register(name, wire.isEnergyWire() ? ICWireRole.ENERGY : ICWireRole.STRUCTURE, 0);
    }

    @Nullable public static WireType toIE(@Nullable ICWireType type) { return type == null ? null : WireType.getValue(type.getUniqueName()); }

    public static ICTargetingInfo targeting(@Nonnull TargetingInfo target) { return new ICTargetingInfo(target.side, target.hitX, target.hitY, target.hitZ); }

    public static boolean unlinked(@Nonnull World world, @Nonnull BlockPos from, @Nonnull BlockPos to) { return connection(world, from, to) == null; }

    @Nonnull public static List<BlockPos[]> pairs(@Nonnull World world) {
        List<BlockPos[]> pairs = new ArrayList<>();
        for (Connection connection : ImmersiveNetHandler.INSTANCE.getAllConnections(world)) {
            if (connection.start.compareTo(connection.end) < 0) { pairs.add(new BlockPos[]{connection.start, connection.end}); }
        }
        return pairs;
    }

    @Nullable public static ICWireType typeOf(@Nonnull World world, @Nonnull BlockPos from, @Nonnull BlockPos to) {
        Connection connection = connection(world, from, to);
        return connection == null ? null : of(connection.cableType);
    }

    public static boolean link(@Nonnull World world, @Nonnull ICWire wire) {
        linking = true;
        try {
            return connect(world, wire, wire.getA().getPos(), wire.getB().getPos(), (first, second) -> {
                restore(first, wire.getA(), wire);
                restore(second, wire.getB(), wire);
            });
        }
        finally { linking = false; }
    }

    private static void restore(IImmersiveConnectable end, ICConnectionPoint point, ICWire wire) {
        if (end instanceof ICTileEntityConnectable) { ((ICTileEntityConnectable)end).onWireAdded(point, wire); }
    }

    private static boolean connect(World world, ICWire wire, BlockPos a, BlockPos b, BiConsumer<IImmersiveConnectable, IImmersiveConnectable> ends) {
        WireType type = toIE(wire.getType());
        IImmersiveConnectable first = ApiUtils.toIIC(a, world, false);
        IImmersiveConnectable second = ApiUtils.toIIC(b, world, false);
        if (type == null || first == null || second == null) { return false; }
        Connection connection = ImmersiveNetHandler.INSTANCE.addAndGetConnection(world, a, b, (int)Math.sqrt(a.distanceSq(b)), type);
        ends.accept(first, second);
        ImmersiveNetHandler.INSTANCE.addBlockData(world, connection);
        sync(world, first);
        sync(world, second);
        return true;
    }

    @Nonnull public static ICWireCoils.Wiring coilWiring() { return CoilWiring.INSTANCE; }

    private static TargetingInfo target(ICTargetingInfo target) { return new TargetingInfo(target.side, target.hitX, target.hitY, target.hitZ); }

    @Nullable private static IImmersiveConnectable connector(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof IImmersiveConnectable && ((IImmersiveConnectable)tile).canConnect() ? (IImmersiveConnectable)tile : null;
    }

    private enum CoilWiring implements ICWireCoils.Wiring {
        INSTANCE;

        @Override public boolean missing(@Nonnull World world, @Nonnull BlockPos pos) { return connector(world, pos) == null; }

        @Override @Nullable public ICConnectionPoint point(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ICWireType type, @Nonnull ICTargetingInfo target) {
            IImmersiveConnectable clicked = IEWireBridge.connector(world, pos);
            if (clicked == null) { return null; }
            BlockPos master = clicked.getConnectionMaster(toIE(type), target(target));
            return IEWireBridge.connector(world, master) == null ? null : new ICConnectionPoint(master, 0);
        }

        @Override public boolean accepts(@Nonnull World world, @Nonnull ICConnectionPoint point, @Nonnull ICWireType type, @Nonnull ICTargetingInfo target) {
            IImmersiveConnectable connector = IEWireBridge.connector(world, point.getPos());
            WireType wire = toIE(type);
            return connector != null && wire != null && connector.canConnectCable(wire, target(target), Vec3i.NULL_VECTOR);
        }

        @Override public boolean wired(@Nonnull World world, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second) { return connection(world, first.getPos(), second.getPos()) != null || connection(world, second.getPos(), first.getPos()) != null; }

        @Override @Nullable public ICWire wireBetween(@Nonnull World world, @Nonnull ICConnectionPoint first, @Nonnull ICTargetingInfo firstTarget, @Nonnull ICConnectionPoint second, @Nonnull ICTargetingInfo secondTarget, @Nonnull ICWireType type) {
            IImmersiveConnectable here = IEWireBridge.connector(world, first.getPos());
            IImmersiveConnectable there = IEWireBridge.connector(world, second.getPos());
            WireType wire = toIE(type);
            if (here == null || there == null || wire == null) { return null; }
            Connection connection = new Connection(first.getPos(), second.getPos(), wire, (int)Math.sqrt(first.getPos().distanceSq(second.getPos())));
            return new ICWire(first, here.getConnectionOffset(connection, target(firstTarget), Vec3i.NULL_VECTOR), second, there.getConnectionOffset(connection, target(secondTarget), Vec3i.NULL_VECTOR), type);
        }

        @Override @Nonnull public Set<BlockPos> ignored(@Nonnull World world, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second) {
            IImmersiveConnectable here = IEWireBridge.connector(world, first.getPos());
            IImmersiveConnectable there = IEWireBridge.connector(world, second.getPos());
            Set<BlockPos> ignored = new HashSet<>();
            if (here == null || there == null) { return ignored; }
            ignored.addAll(here.getIgnored(there));
            ignored.addAll(there.getIgnored(here));
            return ignored;
        }

        @Override public boolean place(@Nonnull World world, @Nonnull ICWire wire, @Nonnull ICConnectionPoint first, @Nonnull ICTargetingInfo firstTarget, @Nonnull ICConnectionPoint second, @Nonnull ICTargetingInfo secondTarget) {
            IEWireEvents.announce();
            try {
                return connect(world, wire, first.getPos(), second.getPos(), (here, there) -> {
                    WireType cable = toIE(wire.getType());
                    here.connectCable(cable, target(firstTarget), there);
                    there.connectCable(cable, target(secondTarget), here);
                });
            }
            finally { IEWireEvents.settle(); }
        }
    }

    private static void sync(World world, IImmersiveConnectable connectable) {
        if (!(connectable instanceof TileEntity)) { return; }
        TileEntity tile = (TileEntity)connectable;
        tile.markDirty();
        IBlockState state = world.getBlockState(tile.getPos());
        world.notifyBlockUpdate(tile.getPos(), state, state, 3);
    }

    public static boolean unlink(@Nonnull World world, @Nonnull BlockPos first, @Nonnull BlockPos second) {
        Connection connection = connection(world, first, second);
        if (connection == null || connection(world, second, first) == null) { return false; }
        ImmersiveNetHandler.INSTANCE.removeConnection(world, connection);
        return true;
    }

    @Nullable private static Connection connection(World world, BlockPos from, BlockPos to) {
        Set<Connection> connections = ImmersiveNetHandler.INSTANCE.getConnections(world, from);
        if (connections == null) { return null; }
        for (Connection connection : connections) {
            if (connection.end.equals(to)) { return connection; }
        }
        return null;
    }

    public static int connectionCount(@Nullable World world, @Nullable BlockPos pos) {
        if (world == null || pos == null || !ICMods.immersiveEngineering()) { return 0; }
        Set<Connection> connections = ImmersiveNetHandler.INSTANCE.getConnections(world, pos);
        return connections == null ? 0 : connections.size();
    }

    public static void onConnectableLoad(@Nonnull ICTileEntityConnectable tile) {
        if (tile.getWorld().isRemote) { ImmersiveConvergence.proxy.scheduleClient(() -> refreshWires(tile)); }
    }

    private static void refreshWires(ICTileEntityConnectable tile) {
        World world = tile.getWorld();
        if (tile.isInvalid()) { return; }
        Set<Connection> connections = ImmersiveNetHandler.INSTANCE.getConnections(world, tile.getPos());
        if (connections == null || connections.isEmpty()) { return; }
        for (Connection connection : connections) {
            if (!world.isBlockLoaded(connection.end)) { continue; }
            connection.catenaryVertices = null;
            world.markBlockRangeForRenderUpdate(connection.end, connection.end);
        }
        world.markBlockRangeForRenderUpdate(tile.getPos(), tile.getPos());
    }

    public static void onConnectableInvalidate(@Nonnull ICTileEntityConnectable tile) {
        World world = tile.getWorld();
        if (world.isRemote && ImmersiveConvergence.proxy.isMultiplayerClient()) { ImmersiveNetHandler.INSTANCE.clearAllConnectionsFor(tile.getPos(), world, (IImmersiveConnectable)tile, false); }
    }
}
