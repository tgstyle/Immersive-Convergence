package com.immersiveconvergence.api.wires;

import javax.annotation.Nonnull;

public interface IICEnergyFitting extends IICFitting {
    int demand(@Nonnull ICConnectionPoint point);

    int accept(@Nonnull ICConnectionPoint point, int amount, boolean simulate);

    void onPassthrough(@Nonnull ICConnectionPoint point, int amount);
}
