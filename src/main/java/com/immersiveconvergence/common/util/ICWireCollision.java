package com.immersiveconvergence.common.util;

import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.ICWireHit;
import com.immersiveconvergence.api.wires.ICWires;
import com.immersiveconvergence.api.wires.IICWireCollision;
import com.immersiveconvergence.api.wires.IICWireNetwork;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public final class ICWireCollision implements IICWireCollision {
    private static final int SAMPLES_PER_BLOCK = 4;
    private static final double RAY_RADIUS = 0.125;

    @Nonnull public static Vec3[] path(@Nonnull ICWire wire) { return wire.getCatenary(Math.max(1, (int)Math.ceil(wire.getLength() * SAMPLES_PER_BLOCK))); }

    @Nonnull public static Set<ICWire> wiresNear(@Nonnull IICWireNetwork network, @Nonnull AABB box) {
        Set<ICWire> near = new LinkedHashSet<>();
        int lastX = Math.floorDiv((int)Math.floor(box.maxX), 16);
        int lastZ = Math.floorDiv((int)Math.floor(box.maxZ), 16);
        for (int x = Math.floorDiv((int)Math.floor(box.minX), 16); x <= lastX; x++) {
            for (int z = Math.floorDiv((int)Math.floor(box.minZ), 16); z <= lastZ; z++) { near.addAll(network.wiresIn(new ChunkPos(x, z))); }
        }
        return near;
    }

    public static boolean hits(@Nonnull ICWire wire, @Nonnull AABB box) {
        Vec3[] path = path(wire);
        for (int i = 0; i < path.length - 1; i++) {
            if (box.contains(path[i]) || box.clip(path[i], path[i + 1]).isPresent()) { return true; }
        }
        return box.contains(path[path.length - 1]);
    }

    @Nonnull @Override public Set<ICWire> wiresHitting(@Nonnull Level level, @Nonnull AABB box) {
        if (level.isClientSide) { return Collections.emptySet(); }
        Set<ICWire> hitting = new LinkedHashSet<>();
        for (ICWire wire : wiresNear(ICWires.network(level), box)) {
            if (hits(wire, box)) { hitting.add(wire); }
        }
        return hitting;
    }

    @Nullable @Override public ICWireHit raytrace(@Nonnull Level level, @Nonnull Vec3 from, @Nonnull Vec3 to) {
        if (level.isClientSide) { return null; }
        ICWireHit nearest = null;
        double best = Double.MAX_VALUE;
        for (ICWire wire : wiresNear(ICWires.network(level), new AABB(from, to))) {
            for (Vec3 sample : path(wire)) {
                AABB box = new AABB(sample, sample).inflate(RAY_RADIUS);
                Optional<Vec3> result = box.contains(from) ? box.clip(to, from) : box.clip(from, to);
                if (result.isEmpty()) { continue; }
                double distance = from.distanceToSqr(result.get());
                if (distance < best) {
                    best = distance;
                    nearest = new ICWireHit(wire, result.get());
                }
            }
        }
        return nearest;
    }
}
