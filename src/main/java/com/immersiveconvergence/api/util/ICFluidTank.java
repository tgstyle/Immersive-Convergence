package com.immersiveconvergence.api.util;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class ICFluidTank extends FluidTank {
    public interface TankListener { void TankContentsChanged(); }

    TankListener listener;

    public ICFluidTank(int capacity, @Nonnull TankListener listener) {
        this(null, capacity, listener);
    }

    public ICFluidTank(@Nullable FluidStack fluidStack, int capacity, @Nonnull TankListener listener) {
        super(fluidStack, capacity);
        this.listener = listener;
    }

    public ICFluidTank(Fluid fluid, int amount, int capacity, @Nonnull TankListener listener) {
        this(new FluidStack(fluid, amount), capacity, listener);
    }

    @Override protected void onContentsChanged() {
        listener.TankContentsChanged();
        super.onContentsChanged();
    }
}
