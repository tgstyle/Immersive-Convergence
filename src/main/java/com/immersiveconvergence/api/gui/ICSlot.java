package com.immersiveconvergence.api.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public abstract class ICSlot extends Slot {
    public final Container container;

    public ICSlot(Container container, IInventory inv, int id, int x, int y) {
        super(inv, id, x, y);
        this.container = container;
    }

    @Override public boolean isItemValid(@Nonnull ItemStack itemStack) { return true; }

    public static class Output extends ICSlot {
        public Output(Container container, IInventory inv, int id, int x, int y) { super(container, inv, id, x, y); }

        @Override public boolean isItemValid(@Nonnull ItemStack itemStack) { return false; }
    }

    public static class FluidContainer extends ICSlot {
        public static final int ANY = 0, EMPTY = 1, FULL = 2;
        final int filter;

        public FluidContainer(Container container, IInventory inv, int id, int x, int y, int filter) {
            super(container, inv, id, x, y);
            this.filter = filter;
        }

        @Override public boolean isItemValid(@Nonnull ItemStack itemStack) {
            IFluidHandler handler = FluidUtil.getFluidHandler(itemStack);
            if (handler == null) { return false; }
            IFluidTankProperties[] tank = handler.getTankProperties();
            if (tank == null || tank.length < 1 || tank[0] == null) { return false; }
            if (filter == EMPTY) { return tank[0].getContents() == null; }
            if (filter == FULL) { return tank[0].getContents() != null; }
            return true;
        }
    }

    public static class Ghost extends ICSlot {
        public Ghost(Container container, IInventory inv, int id, int x, int y) { super(container, inv, id, x, y); }

        @Override public boolean canTakeStack(@Nonnull EntityPlayer player) { return false; }

        @Override public int getSlotStackLimit() { return 1; }
    }
}
