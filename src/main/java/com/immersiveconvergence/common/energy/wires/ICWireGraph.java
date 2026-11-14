package com.immersiveconvergence.common.energy.wires;

import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWire;
import com.immersiveconvergence.api.wires.IICEnergyFitting;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ICWireGraph {
    @Nonnull Set<ICConnectionPoint> pointsAt(@Nonnull BlockPos pos);

    @Nullable Integer component(@Nonnull ICConnectionPoint point);

    int version(int component);

    @Nonnull Collection<ICWire> wiresAt(@Nonnull ICConnectionPoint point);

    boolean passes(@Nonnull ICConnectionPoint point);

    @Nonnull Map<ICConnectionPoint, Double> getLinks(@Nonnull ICConnectionPoint point);

    @Nullable ICEnergyNode node(@Nonnull World world, @Nonnull ICConnectionPoint point);

    @Nonnull static ICEnergyNode energyNode(@Nonnull IICEnergyFitting fitting, @Nonnull ICConnectionPoint point) {
        return new ICEnergyNode() {
            @Override public boolean valid() { return !(fitting instanceof TileEntity) || !((TileEntity)fitting).isInvalid(); }

            @Override public boolean isEnergyOutput() { return fitting.demand(point) > 0; }

            @Override public int outputEnergy(int amount, boolean simulate) { return fitting.accept(point, amount, simulate); }

            @Override public void onEnergyPassthrough(int amount) { fitting.onPassthrough(point, amount); }
        };
    }

    default double lossOf(@Nonnull ICWire wire) { return Math.min(1, wire.getType().getLossPerBlock() * wire.getLength()); }

    @Nonnull default ICConnectionPoint flowPoint(@Nonnull ICConnectionPoint point) { return point; }

    default boolean sameHop(@Nonnull ICConnectionPoint wanted, @Nullable ICConnectionPoint hop) { return wanted.equals(hop); }

    default int reserved(@Nonnull ICWire wire) { return 0; }

    default void settle(@Nonnull Map<ICWire, Integer> used) {}
}
