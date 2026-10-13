package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.api.fluid.FluidUtils;
import blusunrize.immersiveengineering.api.fluid.IFluidPipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    public static Component formatFluidStack(FluidStack fluid) {
        if (fluid.isEmpty()) { return Component.translatable("gui.immersiveconvergence.empty"); }
        return Component.literal(fluid.getDisplayName().getString() + ": " + fluid.getAmount() + "mB");
    }

    public static boolean isFluidRelatedItemStack(ItemStack stack) {
        if (stack.isEmpty()) { return false; }
        return FluidUtil.getFluidHandler(stack).isPresent();
    }

    public static FluidActionResult tryEmptyContainer(@Nonnull ItemStack container, IFluidHandler fluidDestination, int maxAmount, IFluidHandler.FluidAction doDrain) { return FluidUtils.tryEmptyContainer(container, fluidDestination, maxAmount, doDrain); }

    public static boolean fillFluidContainer(IFluidHandler handler, int slotIn, int slotOut, IItemHandlerModifiable inv) { return FluidUtils.fillFluidContainer(handler, slotIn, slotOut, inv); }

    public static ItemStack fillFluidContainer(IFluidHandler handler, ItemStack containerIn, ItemStack containerOut, @Nullable Player player) { return FluidUtils.fillFluidContainer(handler, containerIn, containerOut, player); }

    public static boolean interactWithFluidHandler(Player player, InteractionHand hand, IFluidHandler handler) { return FluidUtils.interactWithFluidHandler(player, hand, handler); }
}
