package com.immersiveconvergence.api.wires;

import javax.annotation.Nonnull;

public interface IICEnergySource extends IICConnector {
    boolean isSource(@Nonnull ICConnectionPoint point);

    default boolean isSink(@Nonnull ICConnectionPoint point) { return !isSource(point); }

    int available();

    void extracted(int amount);
}
