package com.immersiveconvergence.api.energy;

import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class ICWireType {
    public static final String LV_CATEGORY = "LV";
    public static final String MV_CATEGORY = "MV";
    public static final String HV_CATEGORY = "HV";
    public static final String REDSTONE_CATEGORY = "REDSTONE";

    private static final Map<String, ICWireType> REGISTRY = new ConcurrentHashMap<>();

    private final String uniqueName;
    private final String category;
    private final double renderDiameter;
    private final int transferRate;

    public ICWireType(String uniqueName, String category, double renderDiameter, int transferRate) {
        this.uniqueName = uniqueName;
        this.category = category;
        this.renderDiameter = renderDiameter;
        this.transferRate = transferRate;
    }

    @Nonnull public static ICWireType register(@Nonnull ICWireType type) {
        REGISTRY.putIfAbsent(type.uniqueName, type);
        return REGISTRY.get(type.uniqueName);
    }

    @Nullable public static ICWireType byName(@Nullable String name) { return name == null ? null : REGISTRY.get(name); }

    @Nonnull public static Collection<ICWireType> values() { return Collections.unmodifiableCollection(REGISTRY.values()); }

    @Nullable public static ICWireType readFromNBT(NBTTagCompound nbt, String key) { return nbt.hasKey(key) ? byName(nbt.getString(key)) : null; }

    public void writeToNBT(NBTTagCompound nbt, String key) { nbt.setString(key, uniqueName); }

    public String getUniqueName() { return uniqueName; }

    public String getCategory() { return category; }

    public static boolean canMix(@Nullable ICWireType first, @Nullable ICWireType second) {
        if (first == null || second == null) { return false; }
        String category = first.getCategory();
        return category != null && category.equals(second.getCategory());
    }

    public double getRenderDiameter() { return renderDiameter; }

    public int getTransferRate() { return transferRate; }

    @Override public String toString() { return uniqueName; }
}
