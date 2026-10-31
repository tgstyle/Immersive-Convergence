package com.immersiveconvergence.common.ie;

import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.api.wires.ICWireEvent;
import com.immersiveconvergence.api.wires.IICConnector;
import com.immersiveconvergence.common.energy.wires.ICWireEventBus;
import com.immersiveconvergence.core.lib.ICLib;

import blusunrize.immersiveengineering.api.IEApiDataComponents;
import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.IWireCoil;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.api.wires.localhandlers.WireDamageHandler.IShockingWire;
import blusunrize.immersiveengineering.api.wires.utils.WireLink;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public final class IEWireEvents {
    private static final Map<Connection, ICWire> PENDING = new IdentityHashMap<>();
    private static final Set<Connection> JOINING = Collections.newSetFromMap(new IdentityHashMap<>());
    private static boolean announced;
    private static ICWireCause cause;
    private static int depth;
    private static float energyScale = 1F;

    private IEWireEvents() {}

    static void announce() { announced = true; }

    static void settle() { announced = false; }

    public static void enter(@Nonnull ICWireCause entered) {
        if (depth++ == 0) { cause = entered; }
    }

    public static void exit() {
        if (depth > 0 && --depth == 0) { cause = null; }
    }

    public static void reset() {
        depth = 0;
        cause = null;
        announced = false;
        PENDING.clear();
        JOINING.clear();
    }

    @Nonnull public static ICWireCause cause() { return cause == null ? ICWireCause.UNKNOWN : cause; }

    public static boolean refusedCoilUse(@Nonnull IWireCoil coil, @Nonnull Player player, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull InteractionHand hand, @Nonnull Direction side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getItemInHand(hand);
        WireType type = coil.getWireType(stack);
        TargetingInfo target = new TargetingInfo(side, hitX - pos.getX(), hitY - pos.getY(), hitZ - pos.getZ());
        if (!(level.getBlockEntity(pos) instanceof IImmersiveConnectable clicked)) { return false; }
        BlockPos master = clicked.getConnectionMaster(type, target);
        if (!(level.getBlockEntity(master) instanceof IImmersiveConnectable here)) { return false; }
        GlobalWireNetwork network = GlobalWireNetwork.getNetwork(level);
        ConnectionPoint point = here.getTargetedPoint(target, pos.subtract(master));
        ConnectionPoint far = linked(stack);
        if (point == null || far == null) { return false; }
        Connection connection = new Connection(type, point, far, network);
        if (uninvolved(network, connection)) { return false; }
        if (!ICWireEventBus.canceled(new ICWireEvent.Placed(level, IEWireBridge.wire(connection), player, true))) {
            announce();
            return false;
        }
        clearLink(stack);
        player.displayClientMessage(Component.translatable("chat." + ICLib.MODID + ".info.wire.canceled"), true);
        return true;
    }

    public static void adding(@Nonnull GlobalWireNetwork network, @Nullable Level level, @Nonnull Connection connection) {
        if (server(level) && !connection.isInternal() && !IEWireBridge.linking() && apart(network, connection)) { JOINING.add(connection); }
    }

    public static void added(@Nonnull GlobalWireNetwork network, @Nullable Level level, @Nonnull Connection connection) {
        if (!server(level) || connection.isInternal()) { return; }
        boolean known = announced;
        announced = false;
        boolean joined = JOINING.remove(connection);
        if (IEWireBridge.linking() || uninvolved(network, connection)) { return; }
        ICWire wire = IEWireBridge.wire(connection);
        if (!known) { ICWireEventBus.post(new ICWireEvent.Placed(level, wire, null, false)); }
        if (joined) { ICWireEventBus.post(new ICWireEvent.ComponentChanged(level, wire.getA(), wire.getB(), true)); }
    }

    public static void removing(@Nonnull GlobalWireNetwork network, @Nullable Level level, @Nonnull Connection connection) {
        if (!server(level) || connection.isInternal() || IEWireBridge.linking() || !present(network, connection) || uninvolved(network, connection)) { return; }
        PENDING.put(connection, IEWireBridge.wire(connection));
    }

    public static void removed(@Nonnull GlobalWireNetwork network, @Nullable Level level, @Nonnull Connection connection) {
        if (!server(level)) { return; }
        ICWire wire = PENDING.remove(connection);
        if (wire == null) { return; }
        ICWireEventBus.post(new ICWireEvent.Removed(level, wire, cause()));
        if (apart(network, connection)) { ICWireEventBus.post(new ICWireEvent.ComponentChanged(level, wire.getA(), wire.getB(), false)); }
    }

    public static float shock(@Nonnull IShockingWire wire, @Nonnull Entity entity, @Nonnull Connection connection, int energy) {
        energyScale = 1F;
        float damage = wire.getDamageAmount(entity, connection, energy);
        Level level = entity.level();
        if (level.isClientSide || damage <= 0 || !(entity instanceof LivingEntity living) || uninvolved(GlobalWireNetwork.getNetwork(level), connection)) { return damage; }
        ICWireEvent.Shock shock = new ICWireEvent.Shock(level, living, IEWireBridge.wire(connection), damage);
        if (ICWireEventBus.canceled(shock)) { return 0; }
        energyScale = damage > 0 ? shock.getDamage() / damage : 1F;
        return shock.getDamage();
    }

    public static double energyFactor(double factor) { return Math.min(1D, factor * energyScale); }

    private static boolean server(@Nullable Level level) { return level != null && !level.isClientSide; }

    private static boolean apart(GlobalWireNetwork network, Connection connection) {
        LocalWireNetwork first = network.getNullableLocalNet(connection.getEndA());
        LocalWireNetwork second = network.getNullableLocalNet(connection.getEndB());
        return first == null || first != second;
    }

    private static boolean present(GlobalWireNetwork network, Connection connection) {
        LocalWireNetwork local = network.getNullableLocalNet(connection.getEndA());
        return local != null && local.getConnections(connection.getEndA()).contains(connection);
    }

    private static boolean uninvolved(GlobalWireNetwork network, Connection connection) { return lacksConnector(network, connection.getEndA()) && lacksConnector(network, connection.getEndB()); }

    private static boolean lacksConnector(GlobalWireNetwork network, ConnectionPoint point) {
        LocalWireNetwork local = network.getNullableLocalNet(point);
        return local == null || !(local.getConnector(point) instanceof IICConnector);
    }

    private static ConnectionPoint linked(ItemStack stack) { WireLink link = stack.get(IEApiDataComponents.WIRE_LINK); return link == null ? null : link.cp(); }

    private static void clearLink(ItemStack stack) { stack.remove(IEApiDataComponents.WIRE_LINK); }
}
