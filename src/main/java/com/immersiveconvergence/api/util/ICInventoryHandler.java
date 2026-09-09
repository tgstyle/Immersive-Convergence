package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;

@SuppressWarnings("unused")
public class ICInventoryHandler extends IEInventoryHandler {
    public ICInventoryHandler(int slots, IICInventory inventory) { super(slots, inventory); }

    public ICInventoryHandler(int slots, IICInventory inventory, int slotOffset, boolean canInsert, boolean canExtract) { super(slots, inventory, slotOffset, canInsert, canExtract); }

    public ICInventoryHandler(int slots, IICInventory inventory, int slotOffset, boolean[] canInsert, boolean[] canExtract) { super(slots, inventory, slotOffset, canInsert, canExtract); }
}
