package com.immersiveconvergence.api.wires.fittings;

import com.immersiveconvergence.api.wires.ICWireCategory;
import com.immersiveconvergence.core.config.ICCommonConfig;

import javax.annotation.Nonnull;
import java.util.function.IntSupplier;

@SuppressWarnings("unused") public final class ICFittingTier {
    public static final ICFittingTier LV = new ICFittingTier(ICWireCategory.LV, () -> ICCommonConfig.lvRate, 0);
    public static final ICFittingTier MV = new ICFittingTier(ICWireCategory.MV, () -> ICCommonConfig.mvRate, 0.25);
    public static final ICFittingTier HV = new ICFittingTier(ICWireCategory.HV, () -> ICCommonConfig.hvRate, 0.375);
    public static final ICFittingTier STRUCTURE = new ICFittingTier(ICWireCategory.STRUCTURE, () -> 0, 0);
    private final ICWireCategory category;
    private final IntSupplier rate;
    private final double deviceInset;

    public ICFittingTier(@Nonnull ICWireCategory category, @Nonnull IntSupplier rate, double deviceInset) {
        this.category = category;
        this.rate = rate;
        this.deviceInset = deviceInset;
    }

    @Nonnull public ICWireCategory getCategory() { return category; }

    public int getRate() { return rate.getAsInt(); }

    public double deviceInset() { return deviceInset; }
}
