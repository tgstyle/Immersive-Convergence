package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.api.IEApiDataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class ICFluidUtils {
    public static FluidStack copyFluidStackWithAmount(FluidStack stack, int amount, boolean stripPressure) {
        if (stack == null || stack.isEmpty()) { return FluidStack.EMPTY; }
        FluidStack fs = stack.copyWithAmount(amount);
        if (stripPressure) { fs.remove(IEApiDataComponents.FLUID_PRESSURIZED); }
        return fs;
    }

    public static boolean isFluidRelatedItemStack(ItemStack stack) {
        if (stack.isEmpty()) { return false; }
        return stack.getCapability(Capabilities.FluidHandler.ITEM) != null;
    }
}
