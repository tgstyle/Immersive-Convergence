package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICTargetingInfo;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.connectors.ICConnectableBlockEntity;
import com.immersiveconvergence.core.network.ICWireDeltaPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

enum ICWiring implements ICWireCoils.Wiring {
    INSTANCE;

    @Override public boolean missing(@Nonnull Level level, @Nonnull BlockPos pos) { return ICWireRemoval.connectable(level, pos) == null; }

    @Override @Nullable public ICConnectionPoint point(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull ICWireType type, @Nonnull ICTargetingInfo target) {
        ICConnectableBlockEntity here = ICWireRemoval.connectable(level, pos);
        return here == null ? null : here.pointAt(target, Vec3i.ZERO);
    }

    @Override public boolean refuses(@Nonnull ServerLevel level, @Nonnull ICConnectionPoint point, @Nonnull ICWireType type) {
        ICConnectableBlockEntity connectable = ICWireRemoval.connectable(level, point.position());
        return connectable == null || !connectable.canConnect() || !connectable.accepts(point, type);
    }

    @Override public boolean wired(@Nonnull ServerLevel level, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second) { return ICWireSavedData.get(level).wire(first, second) != null; }

    @Override @Nullable public ICWire wireBetween(@Nonnull ServerLevel level, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second, @Nonnull ICWireType type) {
        ICConnectableBlockEntity here = ICWireRemoval.connectable(level, first.position());
        ICConnectableBlockEntity there = ICWireRemoval.connectable(level, second.position());
        return here == null || there == null ? null : new ICWire(first, here.offset(first, type), second, there.offset(second, type), type);
    }

    @Override @Nonnull public Set<BlockPos> ignored(@Nonnull ServerLevel level, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second) {
        Set<BlockPos> ignored = new HashSet<>();
        ICConnectableBlockEntity here = ICWireRemoval.connectable(level, first.position());
        ICConnectableBlockEntity there = ICWireRemoval.connectable(level, second.position());
        if (here != null) { ignored.addAll(here.ignored()); }
        if (there != null) { ignored.addAll(there.ignored()); }
        return ignored;
    }

    @Override public boolean place(@Nonnull ServerLevel level, @Nonnull ICWire wire) {
        ICConnectableBlockEntity here = ICWireRemoval.connectable(level, wire.getA().position());
        ICConnectableBlockEntity there = ICWireRemoval.connectable(level, wire.getB().position());
        if (here == null || there == null || !ICWireSavedData.get(level).addWire(wire)) { return false; }
        ICWireDeltaPacket.added(level, wire);
        here.onWireAdded(wire.getA(), wire);
        there.onWireAdded(wire.getB(), wire);
        here.syncConnectionPoints();
        there.syncConnectionPoints();
        return true;
    }
}
