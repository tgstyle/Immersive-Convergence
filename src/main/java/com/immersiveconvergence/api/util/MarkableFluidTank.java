package com.immersiveconvergence.api.util;

import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.function.Consumer;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MarkableFluidTank extends FluidTank {
    private final Consumer<Void> markDirty;

    public MarkableFluidTank(int capacity, Consumer<Void> markDirty) {
        super(capacity);
        this.markDirty = markDirty;
    }

    @Override protected void onContentsChanged() {
        markDirty.accept(null);
    }

    public static MarkableFluidTank makeClient(int capacity, Consumer<Void> markDirty) {
        return new MarkableFluidTank(capacity, markDirty);
    }
}
