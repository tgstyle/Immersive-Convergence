package com.immersiveconvergence.common.energy;

import com.immersiveconvergence.api.ICLib;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICInternalLink;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.api.wires.ICWireEvent;
import com.immersiveconvergence.api.wires.IICFitting;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.IWireCoil;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class IEWireEvents {
    private static final Map<Connection, ICWire> PENDING = new IdentityHashMap<>();
    private static final ArrayDeque<Clear> CLEARING = new ArrayDeque<>();
    private static boolean announced;
    private static ICWireCause cause;
    private static int depth;

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
        CLEARING.clear();
    }

    @Nonnull public static ICWireCause cause() { return cause == null ? ICWireCause.UNKNOWN : cause; }

    public static boolean refusedCoilUse(@Nonnull IWireCoil coil, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        WireType type = coil.getWireType(stack);
        int[] link = ItemNBTHelper.getIntArray(stack, "linkingPos");
        BlockPos there = new BlockPos(link[1], link[2], link[3]);
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof IImmersiveConnectable)) { return false; }
        BlockPos here = ((IImmersiveConnectable)tile).getConnectionMaster(type, new TargetingInfo(side, hitX, hitY, hitZ));
        if (uninvolved(world, here, there)) { return false; }
        ICWire wire = IEWireView.wireOf(world, new Connection(here, there, type, (int)Math.sqrt(here.distanceSq(there))));
        if (wire == null) { return false; }
        if (!MinecraftForge.EVENT_BUS.post(new ICWireEvent.Placed(world, wire, player, true))) {
            announce();
            return false;
        }
        ItemNBTHelper.remove(stack, "linkingPos");
        ItemNBTHelper.remove(stack, "targettingInfo");
        player.sendStatusMessage(new TextComponentTranslation(ICLib.CHAT_INFO + "wire.canceled"), true);
        return true;
    }

    public static void added(@Nonnull World world, @Nonnull Connection connection) {
        if (world.isRemote) { return; }
        boolean known = announced;
        announced = false;
        if (IEWireBridge.linking() || uninvolved(world, connection.start, connection.end)) { return; }
        ICWire wire = IEWireView.wireOf(world, connection);
        if (wire == null) { return; }
        if (!known) { MinecraftForge.EVENT_BUS.post(new ICWireEvent.Placed(world, wire, null, false)); }
        if (bridge(world, wire)) { MinecraftForge.EVENT_BUS.post(new ICWireEvent.ComponentChanged(world, wire.getA(), wire.getB(), true)); }
    }

    public static boolean cuttable(@Nonnull World world, @Nonnull BlockPos node) { return ApiUtils.toIIC(node, world) != null && ImmersiveNetHandler.INSTANCE.getConnections(world, node) != null; }

    public static void beginClear(@Nonnull World world, @Nonnull BlockPos node, @Nonnull ICWireCause entered) {
        long tick = world.getTotalWorldTime();
        Clear stale = null;
        while (!CLEARING.isEmpty() && CLEARING.peekFirst().tick != tick) { stale = CLEARING.pollFirst(); }
        if (stale != null) {
            depth = stale.depth;
            cause = stale.cause;
        }
        Clear clear = new Clear(node, tick, depth, cause);
        enter(entered);
        Set<Connection> connections = ImmersiveNetHandler.INSTANCE.getConnections(world, node);
        if (connections != null) {
            for (Connection connection : connections) {
                ICWire wire = claimed(connection) ? null : tracked(world, connection);
                if (wire != null) { clear.wires.put(connection, wire); }
            }
        }
        CLEARING.push(clear);
    }

    public static void endClear(@Nonnull World world, @Nonnull BlockPos node) {
        Clear clear = null;
        for (Clear open : CLEARING) {
            if (open.node.equals(node)) {
                clear = open;
                break;
            }
        }
        if (clear == null) { return; }
        Clear top = CLEARING.pop();
        while (top != clear) { top = CLEARING.pop(); }
        for (Map.Entry<Connection, ICWire> entry : clear.wires.entrySet()) {
            if (gone(world, entry.getKey())) { post(world, entry.getValue()); }
        }
        depth = clear.depth;
        cause = clear.cause;
    }

    public static void removing(World world, Connection connection) {
        if (world == null || connection == null || world.isRemote || claimed(connection)) { return; }
        ICWire wire = tracked(world, connection);
        if (wire != null) { PENDING.put(connection, wire); }
    }

    public static void removed(World world, Connection connection) {
        if (world == null || world.isRemote) { return; }
        ICWire wire = PENDING.remove(connection);
        if (wire != null) { post(world, wire); }
    }

    @Nullable private static ICWire tracked(World world, Connection connection) {
        if (IEWireBridge.linking() || orphaned(world, connection) || gone(world, connection) || uninvolved(world, connection.start, connection.end)) { return null; }
        return IEWireView.wireOf(world, connection);
    }

    private static boolean claimed(Connection connection) {
        for (Clear clear : CLEARING) {
            for (Connection held : clear.wires.keySet()) {
                if (held.hasSameConnectors(connection)) { return true; }
            }
        }
        return false;
    }

    private static boolean orphaned(World world, Connection connection) { return cause == null && (unbacked(world, connection.start) || unbacked(world, connection.end)); }

    private static boolean unbacked(World world, BlockPos pos) { return world.isBlockLoaded(pos) && !(world.getTileEntity(pos) instanceof IImmersiveConnectable); }

    private static void post(World world, ICWire wire) {
        MinecraftForge.EVENT_BUS.post(new ICWireEvent.Removed(world, wire, cause()));
        if (bridge(world, wire)) { MinecraftForge.EVENT_BUS.post(new ICWireEvent.ComponentChanged(world, wire.getA(), wire.getB(), false)); }
    }

    public static float shock(@Nonnull IImmersiveConnectable source, @Nonnull Entity entity, @Nonnull Connection connection) {
        float damage = source.getDamageAmount(entity, connection);
        World world = entity.world;
        if (world.isRemote || damage == 0 || !(entity instanceof EntityLivingBase) || uninvolved(world, connection.start, connection.end)) { return damage; }
        ICWire wire = IEWireView.wireOf(world, connection);
        if (wire == null) { return damage; }
        ICWireEvent.Shock shock = new ICWireEvent.Shock(world, (EntityLivingBase)entity, wire, damage);
        return MinecraftForge.EVENT_BUS.post(shock) ? 0 : shock.getDamage();
    }

    private static boolean gone(World world, Connection connection) {
        Set<Connection> connections = ImmersiveNetHandler.INSTANCE.getConnections(world, connection.start);
        if (connections == null) { return true; }
        for (Connection known : connections) {
            if (known.hasSameConnectors(connection)) { return false; }
        }
        return true;
    }

    private static boolean uninvolved(World world, BlockPos first, BlockPos second) { return lacksFitting(world, first) && lacksFitting(world, second); }

    private static boolean lacksFitting(World world, BlockPos pos) { return !(ApiUtils.toIIC(pos, world, false) instanceof IICFitting); }

    private static boolean bridge(World world, ICWire wire) {
        IEWireView view = new IEWireView(world);
        Set<ICConnectionPoint> seenA = new HashSet<>();
        Set<ICConnectionPoint> seenB = new HashSet<>();
        ArrayDeque<ICConnectionPoint> queueA = new ArrayDeque<>();
        ArrayDeque<ICConnectionPoint> queueB = new ArrayDeque<>();
        seenA.add(wire.getA());
        queueA.add(wire.getA());
        seenB.add(wire.getB());
        queueB.add(wire.getB());
        while (!queueA.isEmpty() && !queueB.isEmpty()) {
            if (expand(view, wire, queueA, seenA, seenB) || expand(view, wire, queueB, seenB, seenA)) { return false; }
        }
        return true;
    }

    private static boolean expand(IEWireView view, ICWire skipped, ArrayDeque<ICConnectionPoint> queue, Set<ICConnectionPoint> seen, Set<ICConnectionPoint> other) {
        ICConnectionPoint point = queue.remove();
        for (ICWire near : view.wiresAt(point)) {
            if (near.getA().equals(skipped.getA()) && near.getB().equals(skipped.getB())) { continue; }
            if (reach(near.other(point), queue, seen, other)) { return true; }
        }
        IICFitting fitting = view.fitting(point);
        if (fitting == null) { return false; }
        for (ICInternalLink link : fitting.links()) {
            if (link.getFirst().equals(point) && reach(link.getSecond(), queue, seen, other)) { return true; }
            if (link.getSecond().equals(point) && reach(link.getFirst(), queue, seen, other)) { return true; }
        }
        return false;
    }

    private static boolean reach(ICConnectionPoint next, ArrayDeque<ICConnectionPoint> queue, Set<ICConnectionPoint> seen, Set<ICConnectionPoint> other) {
        if (other.contains(next)) { return true; }
        if (seen.add(next)) { queue.add(next); }
        return false;
    }

    private static final class Clear {
        private final Map<Connection, ICWire> wires = new IdentityHashMap<>();
        private final BlockPos node;
        private final long tick;
        private final int depth;
        private final ICWireCause cause;

        private Clear(BlockPos node, long tick, int depth, ICWireCause cause) {
            this.node = node;
            this.tick = tick;
            this.depth = depth;
            this.cause = cause;
        }
    }
}
