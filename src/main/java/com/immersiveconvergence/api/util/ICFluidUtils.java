package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.api.IEApiDataComponents;
import blusunrize.immersiveengineering.api.fluid.FluidUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class ICFluidUtils {
    public static FluidStack copyFluidStackWithAmount(FluidStack stack, int amount, boolean stripPressure) {
        if (stack == null || stack.isEmpty()) { return FluidStack.EMPTY; }
        FluidStack fs = stack.copyWithAmount(amount);
        if (stripPressure) { fs.remove(IEApiDataComponents.FLUID_PRESSURIZED); }
        return fs;
    }

    public static Component formatFluidStack(FluidStack fluid) {
        if (fluid.isEmpty()) { return Component.translatable("gui.immersiveconvergence.empty"); }
        return Component.literal(fluid.getHoverName().getString() + ": " + fluid.getAmount() + "mB");
    }

    public static boolean isFluidRelatedItemStack(ItemStack stack) {
        if (stack.isEmpty()) { return false; }
        return stack.getCapability(Capabilities.FluidHandler.ITEM) != null;
    }

    public static FluidActionResult tryEmptyContainer(@Nonnull ItemStack container, IFluidHandler fluidDestination, int maxAmount, IFluidHandler.FluidAction doDrain) { return FluidUtils.tryEmptyContainer(container, fluidDestination, maxAmount, doDrain); }

    public static boolean fillFluidContainer(IFluidHandler handler, int slotIn, int slotOut, IItemHandlerModifiable inv) { return FluidUtils.fillFluidContainer(handler, slotIn, slotOut, inv); }

    public static ItemStack fillFluidContainer(IFluidHandler handler, ItemStack containerIn, ItemStack containerOut, @Nullable Player player) { return FluidUtils.fillFluidContainer(handler, containerIn, containerOut, player); }

    public static boolean interactWithFluidHandler(Player player, InteractionHand hand, IFluidHandler handler) { return FluidUtils.interactWithFluidHandler(player, hand, handler); }
}
