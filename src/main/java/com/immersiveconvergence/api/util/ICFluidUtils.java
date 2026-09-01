package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.api.fluid.IFluidPipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class ICFluidUtils {
    public static FluidStack copyFluidStackWithAmount(FluidStack stack, int amount, boolean stripPressure) {
        if (stack.isEmpty()) { return FluidStack.EMPTY; }
        FluidStack fs = new FluidStack(stack, amount);
        if (stripPressure && fs.hasTag()) {
            CompoundTag tag = fs.getTag();
            tag.remove(IFluidPipe.NBT_PRESSURIZED);
            if (tag.isEmpty()) { fs.setTag(null); }
        }
        return fs;
    }

    public static boolean isFluidRelatedItemStack(ItemStack stack) {
        if (stack.isEmpty()) { return false; }
        return FluidUtil.getFluidHandler(stack).isPresent();
    }
}
