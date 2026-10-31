package com.immersiveconvergence.api.wires;

import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;

public record ICTargetingInfo(float hitX, float hitY, float hitZ) {
    public void writeToNBT(@Nonnull CompoundTag tag) {
        tag.putFloat("hitX", hitX);
        tag.putFloat("hitY", hitY);
        tag.putFloat("hitZ", hitZ);
    }

    @Nonnull public static ICTargetingInfo readFromNBT(@Nonnull CompoundTag tag) { return new ICTargetingInfo(tag.getFloat("hitX"), tag.getFloat("hitY"), tag.getFloat("hitZ")); }
}
