package com.immersiveconvergence.api.energy;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.common.ie.IEWireBridge;

import net.minecraft.world.item.ItemStack;

@SuppressWarnings({"unused", "RedundantSuppression"}) public final class ICWires {
    private ICWires() {}

    public static boolean isWireCoil(ItemStack stack) { return !stack.isEmpty() && ICMods.immersiveEngineering() && IEWireBridge.isWireCoil(stack); }
}
