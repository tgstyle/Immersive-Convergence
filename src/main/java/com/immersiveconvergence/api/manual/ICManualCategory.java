package com.immersiveconvergence.api.manual;

import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nullable;

public record ICManualCategory(@Nullable ICManualCategory parent, ResourceLocation id, int priority) {
    public ICManualCategory sub(ResourceLocation subId, int subPriority) { return new ICManualCategory(this, subId, subPriority); }
}
