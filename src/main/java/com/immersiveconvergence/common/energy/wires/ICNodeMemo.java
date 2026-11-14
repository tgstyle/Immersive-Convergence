package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.wires.ICConnectionPoint;

import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public final class ICNodeMemo {
    private final Map<ICConnectionPoint, ICEnergyNode> nodes = new HashMap<>();
    private final Set<ICConnectionPoint> missing = new HashSet<>();
    private long tick = -1;

    @Nullable public ICEnergyNode resolve(@Nonnull World world, @Nonnull ICConnectionPoint point, @Nonnull BiFunction<World, ICConnectionPoint, ICEnergyNode> loader) {
        long now = world.getTotalWorldTime();
        if (now != tick) {
            clear();
            tick = now;
        }
        ICEnergyNode node = nodes.get(point);
        if (node != null && node.valid()) { return node; }
        if (node == null && missing.contains(point)) { return null; }
        nodes.remove(point);
        node = loader.apply(world, point);
        if (node == null) { missing.add(point); }
        else { nodes.put(point, node); }
        return node;
    }

    public void clear() {
        nodes.clear();
        missing.clear();
        tick = -1;
    }
}
