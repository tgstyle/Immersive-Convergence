package com.immersiveconvergence.api.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ICFluidUtils {
    public static FluidStack copyFluidStackWithAmount(FluidStack stack, int amount) {
        if (stack.isEmpty()) { return FluidStack.EMPTY; }
        return new FluidStack(stack, amount);
    }

    public static Component formatFluidStack(FluidStack fluid) {
        if (fluid.isEmpty()) { return Component.translatable("gui.immersiveconvergence.empty"); }
        return Component.literal(fluid.getDisplayName().getString() + ": " + fluid.getAmount() + "mB");
    }

    public static boolean isFluidRelatedItemStack(ItemStack stack) {
        if (stack.isEmpty()) { return false; }
        return FluidUtil.getFluidHandler(stack).isPresent();
    }

    public static FluidActionResult tryEmptyContainer(@Nonnull ItemStack container, IFluidHandler fluidDestination, int maxAmount, FluidAction doDrain) {
        return container.copyWithCount(1).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(handler -> {
            FluidStack simulatedMoved = FluidUtil.tryFluidTransfer(fluidDestination, handler, maxAmount, false);
            if (simulatedMoved.isEmpty()) { return FluidActionResult.FAILURE; }
            handler.drain(simulatedMoved, FluidAction.EXECUTE);
            fluidDestination.fill(simulatedMoved, doDrain);
            return new FluidActionResult(handler.getContainer());
        }).orElse(FluidActionResult.FAILURE);
    }

    public static boolean fillFluidContainer(IFluidHandler handler, int slotIn, int slotOut, IItemHandlerModifiable inv) {
        ItemStack filledContainer = fillFluidContainer(handler, inv.getStackInSlot(slotIn), inv.getStackInSlot(slotOut), null);
        if (filledContainer.isEmpty()) { return false; }
        if (inv.getStackInSlot(slotIn).getCount() == 1 && !isFluidContainerFull(filledContainer)) { inv.setStackInSlot(slotIn, filledContainer.copy()); }
        else {
            if (!inv.getStackInSlot(slotOut).isEmpty() && ItemHandlerHelper.canItemStacksStack(filledContainer, inv.getStackInSlot(slotOut))) { inv.getStackInSlot(slotOut).grow(filledContainer.getCount()); }
            else { inv.setStackInSlot(slotOut, filledContainer); }
            inv.getStackInSlot(slotIn).shrink(1);
            if (inv.getStackInSlot(slotIn).getCount() <= 0) { inv.setStackInSlot(slotIn, ItemStack.EMPTY); }
        }
        return true;
    }

    public static ItemStack fillFluidContainer(IFluidHandler handler, ItemStack containerIn, ItemStack containerOut, @Nullable Player player) {
        if (containerIn == null || containerIn.isEmpty()) { return ItemStack.EMPTY; }
        FluidActionResult result = FluidUtil.tryFillContainer(containerIn, handler, Integer.MAX_VALUE, player, false);
        if (!result.isSuccess()) { return ItemStack.EMPTY; }
        ItemStack full = result.getResult();
        if (!containerOut.isEmpty() && !ItemHandlerHelper.canItemStacksStack(containerOut, full)) { return ItemStack.EMPTY; }
        if (!containerOut.isEmpty() && containerOut.getCount() + full.getCount() > containerOut.getMaxStackSize()) { return ItemStack.EMPTY; }
        result = FluidUtil.tryFillContainer(containerIn, handler, Integer.MAX_VALUE, player, true);
        return result.isSuccess() ? result.getResult() : ItemStack.EMPTY;
    }

    public static boolean isFluidContainerFull(ItemStack stack) {
        return FluidUtil.getFluidHandler(stack).map(handler -> {
            for (int t = 0; t < handler.getTanks(); ++t) {
                if (handler.getFluidInTank(t).getAmount() < handler.getTankCapacity(t)) { return false; }
            }
            return true;
        }).orElse(true);
    }

    public static boolean interactWithFluidHandler(Player player, InteractionHand hand, IFluidHandler handler) {
        SimulatingFluidHandler simulation = new SimulatingFluidHandler(handler);
        boolean success = FluidUtil.interactWithFluidHandler(player, hand, simulation);
        if (success && simulation.lastMoved != null) {
            if (simulation.insert) { handler.fill(simulation.lastMoved, FluidAction.EXECUTE); }
            else { handler.drain(simulation.lastMoved, FluidAction.EXECUTE); }
        }
        return success;
    }

    private static final class SimulatingFluidHandler implements IFluidHandler {
        private final IFluidHandler handler;
        @Nullable private FluidStack lastMoved;
        private boolean insert;

        private SimulatingFluidHandler(IFluidHandler handler) { this.handler = handler; }

        @Override public int getTanks() { return handler.getTanks(); }

        @Nonnull @Override public FluidStack getFluidInTank(int tank) { return handler.getFluidInTank(tank); }

        @Override public int getTankCapacity(int tank) { return handler.getTankCapacity(tank); }

        @Override public boolean isFluidValid(int tank, @Nonnull FluidStack stack) { return handler.isFluidValid(tank, stack); }

        @Override public int fill(FluidStack resource, FluidAction action) {
            int result = handler.fill(resource, FluidAction.SIMULATE);
            if (action == FluidAction.EXECUTE) {
                lastMoved = new FluidStack(resource, result);
                insert = true;
            }
            return result;
        }

        @Nonnull @Override public FluidStack drain(FluidStack resource, FluidAction action) { return drained(handler.drain(resource, FluidAction.SIMULATE), action); }

        @Nonnull @Override public FluidStack drain(int maxDrain, FluidAction action) { return drained(handler.drain(maxDrain, FluidAction.SIMULATE), action); }

        private FluidStack drained(FluidStack result, FluidAction action) {
            if (action == FluidAction.EXECUTE) {
                insert = false;
                lastMoved = result.copy();
            }
            return result;
        }
    }
}
