package com.immersiveconvergence.api.energy;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class ICWireType {
    public static final String LV_CATEGORY = WireType.LV_CATEGORY;
    public static final String MV_CATEGORY = WireType.MV_CATEGORY;
    public static final String HV_CATEGORY = WireType.HV_CATEGORY;
    private static final Map<WireType, ICWireType> CACHE = new ConcurrentHashMap<>();
    private final WireType wire;

    private ICWireType(WireType wire) { this.wire = wire; }

    @Nullable public static ICWireType of(@Nullable WireType wire) { return wire == null ? null : CACHE.computeIfAbsent(wire, ICWireType::new); }

    @Nonnull public static ICWireType required(@Nonnull WireType wire) { return CACHE.computeIfAbsent(wire, ICWireType::new); }

    @Nullable public static ICWireType readFromNBT(NBTTagCompound nbt, String key) { return nbt.hasKey(key) ? of(ApiUtils.getWireTypeFromNBT(nbt, key)) : null; }

    public WireType toIE() { return wire; }

    public String getUniqueName() { return wire.getUniqueName(); }

    public String getCategory() { return wire.getCategory(); }

    public double getRenderDiameter() { return wire.getRenderDiameter(); }

    public int getTransferRate() { return wire.getTransferRate(); }
}
