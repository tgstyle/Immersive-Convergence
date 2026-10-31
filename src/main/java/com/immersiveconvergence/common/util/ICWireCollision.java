package com.immersiveconvergence.common.util;

import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireHit;
import com.immersiveconvergence.api.wires.ICWires;
import com.immersiveconvergence.api.wires.IICWireCollision;
import com.immersiveconvergence.api.wires.IICWireNetwork;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ICWireCollision implements IICWireCollision {
    private static final int SAMPLES_PER_BLOCK = 4;
    private static final double RAY_RADIUS = 0.125;

    @Nonnull public static Vec3d[] path(@Nonnull ICWire wire) { return wire.getCatenary(Math.max(1, (int)Math.ceil(wire.getLength() * SAMPLES_PER_BLOCK))); }

    @Nonnull public static Set<ICWire> wiresNear(@Nonnull IICWireNetwork network, @Nonnull AxisAlignedBB box) {
        Set<ICWire> near = new LinkedHashSet<>();
        int lastX = Math.floorDiv((int)Math.floor(box.maxX), 16);
        int lastZ = Math.floorDiv((int)Math.floor(box.maxZ), 16);
        for (int x = Math.floorDiv((int)Math.floor(box.minX), 16); x <= lastX; x++) {
            for (int z = Math.floorDiv((int)Math.floor(box.minZ), 16); z <= lastZ; z++) { near.addAll(network.wiresIn(new ChunkPos(x, z))); }
        }
        return near;
    }

    public static boolean hits(@Nonnull ICWire wire, @Nonnull AxisAlignedBB box) {
        Vec3d[] path = path(wire);
        for (int i = 0; i < path.length - 1; i++) {
            if (box.contains(path[i]) || box.calculateIntercept(path[i], path[i + 1]) != null) { return true; }
        }
        return box.contains(path[path.length - 1]);
    }

    @Nonnull @Override public Set<ICWire> wiresHitting(@Nonnull World world, @Nonnull AxisAlignedBB box) {
        if (world.isRemote) { return Collections.emptySet(); }
        Set<ICWire> hitting = new LinkedHashSet<>();
        for (ICWire wire : wiresNear(ICWires.network(world), box)) {
            if (hits(wire, box)) { hitting.add(wire); }
        }
        return hitting;
    }

    @Nullable @Override public ICWireHit raytrace(@Nonnull World world, @Nonnull Vec3d from, @Nonnull Vec3d to) {
        if (world.isRemote) { return null; }
        ICWireHit nearest = null;
        double best = Double.MAX_VALUE;
        for (ICWire wire : wiresNear(ICWires.network(world), new AxisAlignedBB(from.x, from.y, from.z, to.x, to.y, to.z))) {
            for (Vec3d sample : path(wire)) {
                RayTraceResult result = new AxisAlignedBB(sample.x, sample.y, sample.z, sample.x, sample.y, sample.z).grow(RAY_RADIUS).calculateIntercept(from, to);
                if (result == null) { continue; }
                double distance = from.squareDistanceTo(result.hitVec);
                if (distance < best) {
                    best = distance;
                    nearest = new ICWireHit(wire, result.hitVec);
                }
            }
        }
        return nearest;
    }
}
