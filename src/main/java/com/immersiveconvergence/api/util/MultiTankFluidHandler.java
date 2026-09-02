package com.immersiveconvergence.api.util;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

@SuppressWarnings({"unused", "RedundantSuppression"}) public record MultiTankFluidHandler(IFluidTank[] internal, boolean allowDrain, boolean allowFill, Runnable afterTransfer) implements IFluidHandler {
    public MultiTankFluidHandler(IFluidTank internal, boolean allowDrain, boolean allowFill, Runnable afterTransfer) { this(new IFluidTank[]{internal}, allowDrain, allowFill, afterTransfer); }

    public static MultiTankFluidHandler drainOnly(IFluidTank internal, Runnable afterTransfer) { return new MultiTankFluidHandler(internal, true, false, afterTransfer); }

    public static MultiTankFluidHandler fillOnly(IFluidTank internal, Runnable afterTransfer) { return new MultiTankFluidHandler(internal, false, true, afterTransfer); }

    @Override public int getTanks() { return this.internal.length; }

    @Override @Nonnull public FluidStack getFluidInTank(int tank) { return this.internal[tank].getFluid(); }

    @Override public int getTankCapacity(int tank) { return this.internal[tank].getCapacity(); }

    @Override public boolean isFluidValid(int tank, @Nonnull FluidStack stack) { return this.internal[tank].isFluidValid(stack); }

    @Override public int fill(@Nonnull FluidStack resource, @Nonnull IFluidHandler.FluidAction action) {
        if (!this.allowFill || resource.isEmpty()) { return 0; }
        FluidStack remaining = resource.copy();
        IFluidTank existing = null;
        for (IFluidTank tank : this.internal) {
            if (FluidStack.isSameFluidSameComponents(tank.getFluid(), remaining)) {
                existing = tank;
                break;
            }
        }
        if (existing != null) { remaining.shrink(existing.fill(remaining, action)); }
        else {
            for (IFluidTank tank : this.internal) {
                int filledHere = tank.fill(remaining, action);
                remaining.shrink(filledHere);
                if (filledHere > 0) { break; }
            }
        }
        int filled = resource.getAmount() - remaining.getAmount();
        if (filled > 0 && action.execute() && afterTransfer != null) { afterTransfer.run(); }
        return filled;
    }

    @Override @Nonnull public FluidStack drain(@Nonnull FluidStack resource, @Nonnull IFluidHandler.FluidAction action) {
        if (!this.allowDrain) { return FluidStack.EMPTY; }
        for (IFluidTank tank : this.internal) {
            FluidStack drainedHere = tank.drain(resource, action);
            if (!drainedHere.isEmpty()) {
                if (action.execute() && afterTransfer != null) { afterTransfer.run(); }
                return drainedHere;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override @Nonnull public FluidStack drain(int maxDrain, @Nonnull IFluidHandler.FluidAction action) {
        if (!this.allowDrain) { return FluidStack.EMPTY; }
        for (IFluidTank tank : this.internal) {
            FluidStack drainedHere = tank.drain(maxDrain, action);
            if (!drainedHere.isEmpty()) {
                if (action.execute() && afterTransfer != null) { afterTransfer.run(); }
                return drainedHere;
            }
        }
        return FluidStack.EMPTY;
    }
}
