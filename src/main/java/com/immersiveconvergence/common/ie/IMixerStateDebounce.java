package com.immersiveconvergence.common.ie;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public interface IMixerStateDebounce {
    NonNullList<ItemStack> ic$getLastComponents();

    int ic$getStableTicks();

    void ic$setStableTicks(int ticks);
}
