package com.immersiveconvergence.api.wires;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

@SuppressWarnings("unused")
public interface IICEnergyNetwork {
    void offer(@Nonnull ICConnectionPoint point, @Nullable ICConnectionPoint firstHop, @Nonnull IntSupplier available, @Nonnull IntConsumer onMoved);

    boolean energized(@Nonnull ICWire wire);

    int throughput(@Nonnull ICWire wire);

    int throughput(@Nonnull ICConnectionPoint point);
}
