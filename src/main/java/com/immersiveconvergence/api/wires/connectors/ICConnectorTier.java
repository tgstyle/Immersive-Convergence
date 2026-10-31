package com.immersiveconvergence.api.wires.connectors;

import com.immersiveconvergence.api.wires.ICWireCategory;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public final class ICConnectorTier {
    public static final ICConnectorTier LV = new ICConnectorTier(ICWireCategory.LV, 2048, 0, 0);
    public static final ICConnectorTier MV = new ICConnectorTier(ICWireCategory.MV, 8192, 0.0625, 0.0625);
    public static final ICConnectorTier HV = new ICConnectorTier(ICWireCategory.HV, 32768, 0.25, 0.375);
    public static final ICConnectorTier STRUCTURE = new ICConnectorTier(ICWireCategory.STRUCTURE, 0, 0, 0);
    private final ICWireCategory category;
    private final int rate;
    private final double connectorInset;
    private final double relayInset;

    public ICConnectorTier(@Nonnull ICWireCategory category, int rate, double connectorInset, double relayInset) {
        this.category = category;
        this.rate = rate;
        this.connectorInset = connectorInset;
        this.relayInset = relayInset;
    }

    @Nonnull public ICWireCategory getCategory() { return category; }

    public int getRate() { return rate; }

    public double inset(boolean relay) { return relay ? relayInset : connectorInset; }
}
