package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.wires.ICConnectResult;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICTargetingInfo;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireEvent;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.IICConnector;
import com.immersiveconvergence.common.util.ICWireCollision;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.IntPredicate;

public final class ICWireWriter {
    private ICWireWriter() {}

    @Nonnull public static ICConnectResult connect(@Nonnull World world, @Nonnull ICWireCoils.Wiring wiring, @Nonnull ICConnectionPoint first, @Nonnull ICTargetingInfo firstTarget, @Nonnull ICConnectionPoint second, @Nonnull ICTargetingInfo secondTarget, @Nonnull ICWireType type) {
        ICConnectResult result = attempt(world, wiring, first, firstTarget, second, secondTarget, type, null, length -> true);
        return result == null ? ICConnectResult.OK : result;
    }

    @Nullable public static ICConnectResult attempt(@Nonnull World world, @Nonnull ICWireCoils.Wiring wiring, @Nonnull ICConnectionPoint first, @Nonnull ICTargetingInfo firstTarget, @Nonnull ICConnectionPoint second, @Nonnull ICTargetingInfo secondTarget, @Nonnull ICWireType type, @Nullable EntityPlayer placer, @Nonnull IntPredicate affordable) {
        if (world.isRemote) { throw new IllegalStateException("Wires are written on the server"); }
        if (first.getPos().equals(second.getPos())) { return ICConnectResult.SELF; }
        if (!world.isBlockLoaded(first.getPos()) || !world.isBlockLoaded(second.getPos())) { return ICConnectResult.UNLOADED; }
        if (wiring.missing(world, first.getPos()) || wiring.missing(world, second.getPos())) { return ICConnectResult.NO_CONNECTOR; }
        if (!wiring.accepts(world, first, type, firstTarget) || !wiring.accepts(world, second, type, secondTarget)) { return ICConnectResult.WRONG_TYPE; }
        if (wiring.wired(world, first, second)) { return ICConnectResult.DUPLICATE; }
        ICWire wire = wiring.wireBetween(world, first, firstTarget, second, secondTarget, type);
        if (wire == null) { return ICConnectResult.NO_CONNECTOR; }
        if (wire.getLength() > type.getMaxLength()) { return ICConnectResult.TOO_LONG; }
        if (!affordable.test((int)Math.ceil(wire.getLength()))) { return null; }
        ICConnectResult blocked = obstruction(world, wire, wiring.ignored(world, first, second));
        if (blocked != null) { return blocked; }
        if (refused(world, wire, placer)) { return ICConnectResult.CANCELED; }
        return wiring.place(world, wire, first, firstTarget, second, secondTarget) ? ICConnectResult.OK : ICConnectResult.DUPLICATE;
    }

    private static boolean refused(World world, ICWire wire, @Nullable EntityPlayer placer) {
        boolean carriesConnector = world.getTileEntity(wire.getA().getPos()) instanceof IICConnector || world.getTileEntity(wire.getB().getPos()) instanceof IICConnector;
        return carriesConnector && MinecraftForge.EVENT_BUS.post(new ICWireEvent.Placed(world, wire, placer, true));
    }

    @Nullable private static ICConnectResult obstruction(World world, ICWire wire, Set<BlockPos> ignored) {
        BlockPos endA = wire.getA().getPos();
        BlockPos endB = wire.getB().getPos();
        for (Vec3d sample : ICWireCollision.path(wire)) {
            BlockPos block = new BlockPos(sample);
            if (block.equals(endA) || block.equals(endB) || ignored.contains(block)) { continue; }
            if (!world.isBlockLoaded(block)) { return ICConnectResult.UNLOADED; }
            IBlockState state = world.getBlockState(block);
            AxisAlignedBB box = state.getCollisionBoundingBox(world, block);
            if (box != null && box.offset(block).contains(sample)) { return ICConnectResult.OBSTRUCTED; }
        }
        return null;
    }
}
