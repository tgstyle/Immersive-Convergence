package com.immersiveconvergence.common.ie;

import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICTargetingInfo;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCategory;
import com.immersiveconvergence.api.wires.ICWireRole;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.common.energy.wires.ICWireCoils;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;
import blusunrize.immersiveengineering.api.wires.WireApi;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.api.wires.localhandlers.EnergyTransferHandler;
import blusunrize.immersiveengineering.api.wires.localhandlers.EnergyTransferHandler.IEnergyWire;
import blusunrize.immersiveengineering.api.wires.redstone.RedstoneNetworkHandler;
import blusunrize.immersiveengineering.api.wires.utils.WireUtils;
import blusunrize.immersiveengineering.common.items.WireCoilItem;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class IEWireBridge {
    private static final Map<String, ICWireType> LATE = new ConcurrentHashMap<>();
    private static boolean linking;

    private IEWireBridge() {}

    public static boolean linking() { return linking; }

    public static void registerWireTypes() {
        for (ICWireType type : ICWireType.values()) {
            if (toIE(type) == null) { WireApi.registerWireType(ICIEWireType.of(type)); }
        }
    }

    public static void adoptWireTypes() {
        for (WireType wire : new ArrayList<>(WireType.getValues())) {
            if (wire != null) { of(wire); }
        }
    }

    @Nullable public static WireType toIE(@Nonnull ICWireType type) {
        for (WireType wire : WireType.getValues()) {
            if (wire != null && wire.getUniqueName().equals(type.getUniqueName())) { return wire; }
        }
        return null;
    }

    @Nonnull public static ICWireType of(@Nonnull WireType wire) {
        if (wire instanceof ICIEWireType ic) { return ic.icType(); }
        ICWireType known = ICWireType.byName(wire.getUniqueName());
        if (known != null) { return known; }
        if (ICWireType.frozen()) { return LATE.computeIfAbsent(wire.getUniqueName(), name -> new Mirror(wire)); }
        return ICWireType.register(new Mirror(wire));
    }

    @Nullable private static ICWireCategory category(@Nonnull WireType wire) {
        String name = wire.getCategory();
        ICWireCategory known = ICWireCategory.byName(name);
        if (known != null || name == null || ICWireType.frozen()) { return known; }
        return ICWireCategory.register(name, wire instanceof IEnergyWire ? ICWireRole.ENERGY : ICWireRole.STRUCTURE, 0);
    }

    private static float shock(@Nonnull WireType wire, @Nullable ICWireCategory category) {
        if (category == null || wire == WireType.COPPER_INSULATED || wire == WireType.ELECTRUM_INSULATED) { return 0; }
        return category.getDefaultShock();
    }

    private static final class Mirror extends ICWireType {
        private final WireType wire;

        private Mirror(@Nonnull WireType wire) { this(wire, category(wire)); }

        private Mirror(@Nonnull WireType wire, @Nullable ICWireCategory category) {
            super(wire.getUniqueName(), category, wire instanceof IEnergyWire energy ? energy::getTransferRate : () -> Integer.MAX_VALUE, 0, 0, wire::getRenderDiameter, 0, 0xFFFFFF, () -> ItemStack.EMPTY, shock(wire, category));
            this.wire = wire;
        }

        @Override public int getMaxLength() { return wire.getMaxLength(); }

        @Override public double getSlack() { return wire.getSlack(); }

        @Override public double getLossPerBlock() {
            if (!(wire instanceof IEnergyWire energy) || wire.getMaxLength() <= 0) { return 0; }
            Connection probe = new Connection(wire, new ConnectionPoint(BlockPos.ZERO, 0), new ConnectionPoint(BlockPos.ZERO.east(), 0), Vec3.ZERO, Vec3.ZERO);
            return energy.getBasicLossRate(probe) / probe.getLength();
        }
    }

    @Nonnull public static ICConnectionPoint point(@Nonnull ConnectionPoint cp) { return new ICConnectionPoint(cp.position(), cp.index()); }

    @Nullable public static ConnectionPoint toIE(@Nullable ICConnectionPoint cp) { return cp == null ? null : ie(cp); }

    @Nonnull static ConnectionPoint ie(@Nonnull ICConnectionPoint cp) { return new ConnectionPoint(cp.position(), cp.index()); }

    @Nullable public static ICWire wire(@Nonnull Level level, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second) {
        Connection connection = connection(level, first, second);
        return connection == null ? null : wire(connection);
    }

    @Nullable private static Connection connection(Level level, ICConnectionPoint first, ICConnectionPoint second) {
        ConnectionPoint start = ie(first);
        ConnectionPoint end = ie(second);
        for (Connection connection : connections(level, start)) {
            if (!connection.isInternal() && connection.getOtherEnd(start).equals(end)) { return connection; }
        }
        return null;
    }

    public static boolean unlinked(@Nonnull Level level, @Nonnull ICWire wire) { return wire(level, wire.getA(), wire.getB()) == null; }

    @Nonnull public static List<ICWire> wires(@Nonnull Level level, @Nonnull BlockPos from, @Nonnull BlockPos to) { return between(level, from, to).stream().map(IEWireBridge::wire).toList(); }

    @Nonnull public static List<BlockPos[]> pairs(@Nonnull Level level) {
        Set<List<BlockPos>> found = new LinkedHashSet<>();
        for (Tag local : GlobalWireNetwork.getNetwork(level).writeToNBT().getList("locals", Tag.TAG_COMPOUND)) {
            for (Tag tag : ((CompoundTag)local).getList("wires", Tag.TAG_COMPOUND)) {
                Connection connection = new Connection((CompoundTag)tag);
                BlockPos a = connection.getEndA().position();
                BlockPos b = connection.getEndB().position();
                if (!connection.isInternal() && !a.equals(b)) { found.add(a.compareTo(b) < 0 ? List.of(a, b) : List.of(b, a)); }
            }
        }
        List<BlockPos[]> pairs = new ArrayList<>();
        for (List<BlockPos> pair : found) { pairs.add(new BlockPos[]{pair.get(0), pair.get(1)}); }
        return pairs;
    }

    public static void link(@Nonnull Level level, @Nonnull ICWire wire) {
        linking = true;
        try { connect(level, wire); }
        finally { linking = false; }
    }

    private static boolean connect(Level level, ICWire wire) {
        WireType type = toIE(wire.getType());
        ConnectionPoint a = ie(wire.getA());
        ConnectionPoint b = ie(wire.getB());
        if (type == null || !(level.getBlockEntity(a.position()) instanceof IImmersiveConnectable first) || !(level.getBlockEntity(b.position()) instanceof IImmersiveConnectable second)) { return false; }
        GlobalWireNetwork network = GlobalWireNetwork.getNetwork(level);
        network.addConnection(new Connection(type, a, b, network));
        first.connectCable(type, a, second, b);
        second.connectCable(type, b, first, a);
        sync(level, first);
        sync(level, second);
        return true;
    }

    @Nonnull public static ICWireCoils.Wiring coilWiring() { return CoilWiring.INSTANCE; }

    @Nullable private static IImmersiveConnectable connector(Level level, BlockPos pos) { return level.getBlockEntity(pos) instanceof IImmersiveConnectable connector && connector.canConnect() ? connector : null; }

    private enum CoilWiring implements ICWireCoils.Wiring {
        INSTANCE;

        @Override public boolean missing(@Nonnull Level level, @Nonnull BlockPos pos) { return connector(level, pos) == null; }

        @Override @Nullable public ICConnectionPoint point(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull ICWireType type, @Nonnull ICTargetingInfo target) {
            IImmersiveConnectable clicked = IEWireBridge.connector(level, pos);
            WireType wire = toIE(type);
            if (clicked == null || wire == null) { return null; }
            TargetingInfo info = new TargetingInfo(Direction.UP, target.hitX(), target.hitY(), target.hitZ());
            BlockPos master = clicked.getConnectionMaster(wire, info);
            IImmersiveConnectable connector = IEWireBridge.connector(level, master);
            ConnectionPoint point = connector == null ? null : connector.getTargetedPoint(info, pos.subtract(master));
            return point == null ? null : IEWireBridge.point(point);
        }

        @Override public boolean refuses(@Nonnull ServerLevel level, @Nonnull ICConnectionPoint point, @Nonnull ICWireType type) {
            IImmersiveConnectable connector = IEWireBridge.connector(level, point.position());
            WireType wire = toIE(type);
            return connector == null || wire == null || !connector.canConnectCable(wire, ie(point), Vec3i.ZERO);
        }

        @Override public boolean wired(@Nonnull ServerLevel level, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second) { return wire(level, first, second) != null; }

        @Override @Nullable public ICWire wireBetween(@Nonnull ServerLevel level, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second, @Nonnull ICWireType type) {
            IImmersiveConnectable here = IEWireBridge.connector(level, first.position());
            IImmersiveConnectable there = IEWireBridge.connector(level, second.position());
            WireType wire = toIE(type);
            if (here == null || there == null || wire == null) { return null; }
            return new ICWire(first, here.getConnectionOffset(ie(first), ie(second), wire), second, there.getConnectionOffset(ie(second), ie(first), wire), type);
        }

        @Override @Nonnull public Set<BlockPos> ignored(@Nonnull ServerLevel level, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second) {
            IImmersiveConnectable here = IEWireBridge.connector(level, first.position());
            IImmersiveConnectable there = IEWireBridge.connector(level, second.position());
            Set<BlockPos> ignored = new HashSet<>();
            if (here == null || there == null) { return ignored; }
            ignored.addAll(here.getIgnored(there));
            ignored.addAll(there.getIgnored(here));
            return ignored;
        }

        @Override public boolean place(@Nonnull ServerLevel level, @Nonnull ICWire wire) {
            IEWireEvents.announce();
            try { return connect(level, wire); }
            finally { IEWireEvents.settle(); }
        }
    }

    private static void sync(Level level, IImmersiveConnectable connectable) {
        if (!(connectable instanceof BlockEntity tile)) { return; }
        tile.setChanged();
        BlockState state = level.getBlockState(tile.getBlockPos());
        level.sendBlockUpdated(tile.getBlockPos(), state, state, 3);
    }

    public static void prune(@Nonnull Level level, @Nonnull BlockPos first, @Nonnull BlockPos second) {
        GlobalWireNetwork network = GlobalWireNetwork.getNetwork(level);
        linking = true;
        try {
            for (Connection connection : between(level, first, second)) { network.removeConnection(connection); }
        }
        finally { linking = false; }
    }

    public static boolean unlink(@Nonnull Level level, @Nonnull ICWire wire) {
        Connection connection = connection(level, wire.getA(), wire.getB());
        if (connection == null) { return false; }
        GlobalWireNetwork.getNetwork(level).removeConnection(connection);
        return true;
    }

    @Nonnull private static List<Connection> between(Level level, BlockPos from, BlockPos to) {
        List<Connection> found = new ArrayList<>();
        if (!(level.getBlockEntity(from) instanceof IImmersiveConnectable connector)) { return found; }
        for (ConnectionPoint point : connector.getConnectionPoints()) {
            for (Connection connection : connections(level, point)) {
                if (!connection.isInternal() && connection.getOtherEnd(point).position().equals(to)) { found.add(connection); }
            }
        }
        return found;
    }

    @Nonnull static Collection<Connection> connections(Level level, ConnectionPoint point) {
        LocalWireNetwork local = GlobalWireNetwork.getNetwork(level).getNullableLocalNet(point);
        return local == null ? List.of() : local.getConnections(point);
    }

    public static int throughput(@Nonnull Level level, @Nonnull ICWire wire) {
        Connection connection = connection(level, wire.getA(), wire.getB());
        return connection == null ? 0 : (int)Math.ceil(transferred(level, connection.getEndA()).getDouble(connection));
    }

    public static int throughput(@Nonnull Level level, @Nonnull ICConnectionPoint point) {
        ConnectionPoint start = ie(point);
        Object2DoubleMap<Connection> flows = transferred(level, start);
        double total = 0;
        int wires = 0;
        for (Connection connection : connections(level, start)) {
            if (!connection.isInternal()) {
                total += flows.getDouble(connection);
                wires++;
            }
        }
        return (int)Math.ceil(wires > 1 ? total / 2 : total);
    }

    @Nonnull private static Object2DoubleMap<Connection> transferred(Level level, ConnectionPoint point) {
        LocalWireNetwork local = GlobalWireNetwork.getNetwork(level).getNullableLocalNet(point);
        EnergyTransferHandler handler = local == null ? null : local.getHandler(EnergyTransferHandler.ID, EnergyTransferHandler.class);
        return handler == null ? Object2DoubleMaps.emptyMap() : handler.getTransferredLastTick();
    }

    @Nonnull public static Set<SectionPos> sections(@Nonnull Level level, @Nonnull ICWire wire) {
        Set<SectionPos> sections = new HashSet<>();
        Connection connection = connection(level, wire.getA(), wire.getB());
        if (connection != null) { WireUtils.forEachRenderPoint(connection, (index, relative, section) -> sections.add(section)); }
        return sections;
    }

    @Nonnull static ICWire wire(Connection connection) { return new ICWire(point(connection.getEndA()), connection.getEndAOffset(), point(connection.getEndB()), connection.getEndBOffset(), of(connection.type)); }

    @Nonnull public static ICTargetingInfo targeting(@Nonnull TargetingInfo target) { return new ICTargetingInfo(target.hitX, target.hitY, target.hitZ); }

    public static boolean isWireCoil(ItemStack stack) { return stack.getItem() instanceof WireCoilItem; }

    public static boolean hasNoExternalConnections(Level level, ICConnectionPoint cp) {
        ConnectionPoint point = ie(cp);
        return GlobalWireNetwork.getNetwork(level).getLocalNet(point).getConnections(point).stream().allMatch(Connection::isInternal);
    }

    public static int redstoneValue(Level level, BlockPos pos, int channel) {
        RedstoneNetworkHandler handler = redstoneHandler(level, pos);
        return handler != null ? handler.getValue(channel) : 0;
    }

    public static void updateRedstoneValues(Level level, BlockPos pos) {
        RedstoneNetworkHandler handler = redstoneHandler(level, pos);
        if (handler != null) { handler.updateValues(); }
    }

    @Nullable private static RedstoneNetworkHandler redstoneHandler(Level level, BlockPos pos) { return GlobalWireNetwork.getNetwork(level).getLocalNet(pos).getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class); }
}
