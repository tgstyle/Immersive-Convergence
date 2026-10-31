package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.wires.ICConnectResult;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireEvent;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.IICConnector;
import com.immersiveconvergence.common.util.ICWireCollision;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.IntPredicate;

public final class ICWireWriter {
    private ICWireWriter() {}

    @Nonnull public static ICConnectResult connect(@Nonnull Level level, @Nonnull ICWireCoils.Wiring wiring, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second, @Nonnull ICWireType type) {
        if (!(level instanceof ServerLevel server)) { throw new IllegalStateException("Wires are written on the server"); }
        ICConnectResult result = attempt(server, wiring, first, second, type, null, length -> true);
        return result == null ? ICConnectResult.OK : result;
    }

    @Nullable public static ICConnectResult attempt(@Nonnull ServerLevel level, @Nonnull ICWireCoils.Wiring wiring, @Nonnull ICConnectionPoint first, @Nonnull ICConnectionPoint second, @Nonnull ICWireType type, @Nullable Player placer, @Nonnull IntPredicate affordable) {
        if (first.position().equals(second.position())) { return ICConnectResult.SELF; }
        if (!level.isLoaded(first.position()) || !level.isLoaded(second.position())) { return ICConnectResult.UNLOADED; }
        if (wiring.missing(level, first.position()) || wiring.missing(level, second.position())) { return ICConnectResult.NO_CONNECTOR; }
        if (wiring.refuses(level, first, type) || wiring.refuses(level, second, type)) { return ICConnectResult.WRONG_TYPE; }
        if (wiring.wired(level, first, second)) { return ICConnectResult.DUPLICATE; }
        ICWire wire = wiring.wireBetween(level, first, second, type);
        if (wire == null) { return ICConnectResult.NO_CONNECTOR; }
        if (wire.getLength() > type.getMaxLength()) { return ICConnectResult.TOO_LONG; }
        if (!affordable.test((int)Math.ceil(wire.getLength()))) { return null; }
        ICConnectResult blocked = obstruction(level, wire, wiring.ignored(level, first, second));
        if (blocked != null) { return blocked; }
        if (refused(level, wire, placer)) { return ICConnectResult.CANCELED; }
        return wiring.place(level, wire) ? ICConnectResult.OK : ICConnectResult.DUPLICATE;
    }

    private static boolean refused(ServerLevel level, ICWire wire, @Nullable Player placer) {
        boolean carriesConnector = level.getBlockEntity(wire.getA().position()) instanceof IICConnector || level.getBlockEntity(wire.getB().position()) instanceof IICConnector;
        return carriesConnector && ICWireEventBus.canceled(new ICWireEvent.Placed(level, wire, placer, true));
    }

    @Nullable private static ICConnectResult obstruction(ServerLevel level, ICWire wire, Set<BlockPos> ignored) {
        BlockPos endA = wire.getA().position();
        BlockPos endB = wire.getB().position();
        for (Vec3 sample : ICWireCollision.path(wire)) {
            BlockPos block = BlockPos.containing(sample);
            if (block.equals(endA) || block.equals(endB) || ignored.contains(block)) { continue; }
            if (!level.isLoaded(block)) { return ICConnectResult.UNLOADED; }
            BlockState state = level.getBlockState(block);
            for (AABB box : state.getCollisionShape(level, block).toAabbs()) {
                if (box.move(block).contains(sample)) { return ICConnectResult.OBSTRUCTED; }
            }
        }
        return null;
    }
}
