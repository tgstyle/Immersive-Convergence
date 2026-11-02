package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.IICEnergyNetwork;
import com.immersiveconvergence.core.integration.immersiveengineering.IEWireBridge;

import net.minecraft.world.level.Level;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ICEnergyView implements IICEnergyNetwork {
    private final Level level;

    public ICEnergyView(@Nonnull Level level) { this.level = level; }

    @Override public void offer(@Nonnull ICConnectionPoint point, @Nullable ICConnectionPoint firstHop, @Nonnull IntSupplier available, @Nonnull IntConsumer onMoved) { ICEnergyTransfer.offer(level, point, firstHop, available, onMoved); }

    @Override public boolean energized(@Nonnull ICWire wire) { return ICMods.immersiveEngineering() ? IEWireBridge.throughput(level, wire) > 0 : ICEnergyTransfer.energized(level, wire); }

    @Override public int throughput(@Nonnull ICWire wire) { return ICMods.immersiveEngineering() ? IEWireBridge.throughput(level, wire) : ICEnergyTransfer.throughput(level, wire); }

    @Override public int throughput(@Nonnull ICConnectionPoint point) { return ICMods.immersiveEngineering() ? IEWireBridge.throughput(level, point) : ICEnergyTransfer.throughput(level, point); }
}
