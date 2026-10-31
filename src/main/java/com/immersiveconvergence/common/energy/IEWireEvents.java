package com.immersiveconvergence.common.energy;

import com.immersiveconvergence.api.ICLib;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICInternalLink;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireCause;
import com.immersiveconvergence.api.wires.ICWireEvent;
import com.immersiveconvergence.api.wires.IICConnector;

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

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public final class IEWireEvents {
    private static final Map<Connection, ICWire> PENDING = new IdentityHashMap<>();
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

    public static void removing(World world, Connection connection) {
        if (world == null || connection == null || world.isRemote || IEWireBridge.linking() || !present(world, connection) || uninvolved(world, connection.start, connection.end)) { return; }
        ICWire wire = IEWireView.wireOf(world, connection);
        if (wire != null) { PENDING.put(connection, wire); }
    }

    public static void removed(World world, Connection connection) {
        if (world == null || world.isRemote) { return; }
        ICWire wire = PENDING.remove(connection);
        if (wire == null) { return; }
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

    private static boolean present(World world, Connection connection) {
        Set<Connection> connections = ImmersiveNetHandler.INSTANCE.getConnections(world, connection.start);
        if (connections == null) { return false; }
        for (Connection known : connections) {
            if (known.hasSameConnectors(connection)) { return true; }
        }
        return false;
    }

    private static boolean uninvolved(World world, BlockPos first, BlockPos second) { return lacksConnector(world, first) && lacksConnector(world, second); }

    private static boolean lacksConnector(World world, BlockPos pos) { return !(ApiUtils.toIIC(pos, world, false) instanceof IICConnector); }

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
        IICConnector connector = view.connector(point);
        if (connector == null) { return false; }
        for (ICInternalLink link : connector.links()) {
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
}
