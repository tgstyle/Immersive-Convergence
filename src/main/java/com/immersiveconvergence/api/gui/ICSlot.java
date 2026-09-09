package com.immersiveconvergence.api.gui;

import blusunrize.immersiveengineering.common.gui.IESlot;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

@SuppressWarnings("unused")
public abstract class ICSlot extends IESlot {
    public ICSlot(Container container, IInventory inv, int id, int x, int y) { super(container, inv, id, x, y); }

    public static class Output extends IESlot.Output {
        public Output(Container container, IInventory inv, int id, int x, int y) { super(container, inv, id, x, y); }
    }

    public static class FluidContainer extends IESlot.FluidContainer {
        public FluidContainer(Container container, IInventory inv, int id, int x, int y, int filter) { super(container, inv, id, x, y, filter); }
    }
}
