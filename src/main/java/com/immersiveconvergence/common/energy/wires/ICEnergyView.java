package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.IICEnergyNetwork;

import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public final class ICEnergyView implements IICEnergyNetwork {
    private final World world;

    public ICEnergyView(@Nonnull World world) { this.world = world; }

    @Override public void offer(@Nonnull ICConnectionPoint point, @Nullable ICConnectionPoint firstHop, @Nonnull IntSupplier available, @Nonnull IntConsumer onMoved) { ICEnergyTransfer.offer(world, point, firstHop, available, onMoved); }

    @Override public boolean energized(@Nonnull ICWire wire) { return ICEnergyTransfer.energized(world, wire); }

    @Override public int throughput(@Nonnull ICWire wire) { return ICEnergyTransfer.throughput(world, wire); }

    @Override public int throughput(@Nonnull ICConnectionPoint point) { return ICEnergyTransfer.throughput(world, point); }
}
