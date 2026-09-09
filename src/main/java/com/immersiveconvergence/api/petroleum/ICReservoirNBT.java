package com.immersiveconvergence.api.petroleum;

import net.minecraft.nbt.NBTTagCompound;

public final class ICReservoirNBT {
    public static final String CONTENT = "ic_reservoirContent";
    public static final String PUMP_SPEED = "ic_pumpSpeed";
    public static final String POWER_TIER = "ic_powerTier";
    public static final String DRAIN_CHANCE = "ic_drainChance";

    private static final String FOREIGN_CONTENT = "reservoirContent";
    private static final String FOREIGN_PUMP_SPEED = "pumpSpeed";
    private static final String FOREIGN_POWER_TIER = "powerTier";
    private static final String FOREIGN_DRAIN_CHANCE = "drainChance";
    private static final ICReservoirContent[] FOREIGN_ORDER = {ICReservoirContent.LIQUID, ICReservoirContent.GAS, ICReservoirContent.EMPTY, ICReservoirContent.DEFAULT};

    private ICReservoirNBT() {}

    public static void write(NBTTagCompound tag, ICReservoirData data) {
        tag.setString(CONTENT, data.content.name());
        tag.setInteger(PUMP_SPEED, data.pumpSpeed);
        tag.setInteger(POWER_TIER, data.powerTier);
        tag.setFloat(DRAIN_CHANCE, data.drainChance);
    }

    public static void read(NBTTagCompound tag, ICReservoirData data) {
        if (tag.hasKey(CONTENT)) {
            data.content = ICReservoirContent.byName(tag.getString(CONTENT));
            data.pumpSpeed = tag.getInteger(PUMP_SPEED);
            data.powerTier = tag.getInteger(POWER_TIER);
            data.drainChance = tag.getFloat(DRAIN_CHANCE);
            return;
        }
        if (!tag.hasKey(FOREIGN_CONTENT)) { return; }
        int ordinal = tag.getByte(FOREIGN_CONTENT);
        data.content = ordinal >= 0 && ordinal < FOREIGN_ORDER.length ? FOREIGN_ORDER[ordinal] : ICReservoirContent.DEFAULT;
        data.pumpSpeed = tag.getInteger(FOREIGN_PUMP_SPEED);
        data.powerTier = tag.getInteger(FOREIGN_POWER_TIER);
        data.drainChance = tag.hasKey(FOREIGN_DRAIN_CHANCE) ? tag.getFloat(FOREIGN_DRAIN_CHANCE) : 1F;
    }
}
