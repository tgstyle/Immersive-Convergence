package com.immersiveconvergence.api.capability;

import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nullable;

public interface IExhaustReceiver {
    void acceptExhaust(@Nullable FluidStack flue, int co2);
}
