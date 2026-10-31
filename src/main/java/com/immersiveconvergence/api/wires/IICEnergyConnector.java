package com.immersiveconvergence.api.wires;

import javax.annotation.Nonnull;

public interface IICEnergyConnector extends IICConnector {
    int demand(@Nonnull ICConnectionPoint point);

    int accept(@Nonnull ICConnectionPoint point, int amount, boolean simulate);

    void onPassthrough(@Nonnull ICConnectionPoint point, int amount);
}
