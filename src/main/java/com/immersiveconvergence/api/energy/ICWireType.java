package com.immersiveconvergence.api.energy;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.common.ie.IEWireBridge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

@SuppressWarnings({"unused", "RedundantSuppression"}) public final class ICWireType {
    public static final String LV_CATEGORY = "LV";
    public static final String MV_CATEGORY = "MV";
    public static final String HV_CATEGORY = "HV";
    public static final String REDSTONE_CATEGORY = "REDSTONE";
    private static final Map<String, ICWireType> REGISTRY = new ConcurrentHashMap<>();
    private final String uniqueName;
    private final String category;
    private final DoubleSupplier renderDiameter;
    private final IntSupplier transferRate;

    public ICWireType(String uniqueName, String category, DoubleSupplier renderDiameter, IntSupplier transferRate) {
        this.uniqueName = uniqueName;
        this.category = category;
        this.renderDiameter = renderDiameter;
        this.transferRate = transferRate;
    }

    @Nonnull public static ICWireType register(@Nonnull ICWireType type) { return REGISTRY.computeIfAbsent(type.uniqueName, name -> type); }

    @Nullable public static ICWireType byName(@Nullable String name) {
        if (name == null) { return null; }
        ICWireType type = REGISTRY.get(name);
        if (type == null && ICMods.immersiveEngineering()) { return IEWireBridge.byName(name); }
        return type;
    }

    public String getUniqueName() { return uniqueName; }

    public String getCategory() { return category; }

    public double getRenderDiameter() { return renderDiameter.getAsDouble(); }

    public int getTransferRate() { return transferRate.getAsInt(); }

    @Override public String toString() { return uniqueName; }
}
