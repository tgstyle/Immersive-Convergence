package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICTargetingInfo;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.IICConnector;
import com.immersiveconvergence.core.network.ICWireDeltaPacket;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

enum ICWiring implements ICWireCoils.Wiring {
    INSTANCE;

    @Override public boolean missing(@Nonnull World world, @Nonnull BlockPos pos) { return ICWireRemoval.connectable(world, pos) == null; }

    @Override @Nullable public ICConnectionPoint point(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ICWireType type, @Nonnull ICTargetingInfo target) {
        IICConnector here = ICWireRemoval.connectable(world, pos);
        return here == null ? null : here.pointAt(target, Vec3i.NULL_VECTOR);
    }

    @Override public boolean accepts(@Nonnull World world, @Nonnull ICConnectionPoint point, @Nonnull ICWireType type, @Nonnull ICTargetingInfo target) {
        IICConnector connector = ICWireRemoval.connectable(world, point.getPos());
        return connector != null && connector.accepts(point, type);
    }

    @Override public boolean wired(@Nonnull World world, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second) { return ICWireSavedData.get(world).wire(first, second) != null; }

    @Override @Nullable public ICWire wireBetween(@Nonnull World world, @Nonnull ICConnectionPoint first, @Nonnull ICTargetingInfo firstTarget, @Nonnull ICConnectionPoint second, @Nonnull ICTargetingInfo secondTarget, @Nonnull ICWireType type) {
        IICConnector here = ICWireRemoval.connectable(world, first.getPos());
        IICConnector there = ICWireRemoval.connectable(world, second.getPos());
        return here == null || there == null ? null : new ICWire(first, here.offset(first, type), second, there.offset(second, type), type);
    }

    @Override @Nonnull public Set<BlockPos> ignored(@Nonnull World world, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second) {
        Set<BlockPos> ignored = new HashSet<>();
        IICConnector here = ICWireRemoval.connectable(world, first.getPos());
        IICConnector there = ICWireRemoval.connectable(world, second.getPos());
        if (here != null) { ignored.addAll(here.ignored()); }
        if (there != null) { ignored.addAll(there.ignored()); }
        return ignored;
    }

    @Override public boolean place(@Nonnull World world, @Nonnull ICWire wire, @Nonnull ICConnectionPoint first, @Nonnull ICTargetingInfo firstTarget, @Nonnull ICConnectionPoint second, @Nonnull ICTargetingInfo secondTarget) {
        IICConnector here = ICWireRemoval.connectable(world, first.getPos());
        IICConnector there = ICWireRemoval.connectable(world, second.getPos());
        ICWireNetwork network = ICWireSavedData.get(world);
        if (here == null || there == null || !network.addWire(wire)) { return false; }
        ICWireDeltaPacket.added(world, wire);
        here.onWireAdded(first, wire);
        there.onWireAdded(second, wire);
        network.sync(here);
        network.sync(there);
        return true;
    }
}
