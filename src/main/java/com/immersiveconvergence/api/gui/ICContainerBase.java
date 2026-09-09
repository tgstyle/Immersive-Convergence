package com.immersiveconvergence.api.gui;

import com.immersiveconvergence.api.util.IICInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class ICContainerBase<T extends TileEntity> extends Container {
    public T tile;
    @Nullable public IInventory inv;
    public int slotCount;

    public ICContainerBase(InventoryPlayer inventoryPlayer, T tile) {
        this.tile = tile;
        if (tile instanceof IICInventory) { this.inv = new ICInventoryTile(tile); }
    }

    @Override public boolean canInteractWith(@Nonnull EntityPlayer player) { return inv != null && inv.isUsableByPlayer(player); }

    private static ItemStack singleStackOf(ItemStack stack) {
        if (stack.isEmpty()) { return ItemStack.EMPTY; }
        ItemStack copy = stack.copy();
        copy.setCount(1);
        return copy;
    }

    @Override @Nonnull public ItemStack slotClick(int id, int button, @Nonnull ClickType clickType, @Nonnull EntityPlayer player) {
        Slot slot = id < 0 ? null : this.inventorySlots.get(id);
        if (!(slot instanceof ICSlot.Ghost)) { return super.slotClick(id, button, clickType, player); }
        ItemStack stack = ItemStack.EMPTY;
        ItemStack stackSlot = slot.getStack();
        if (!stackSlot.isEmpty()) { stack = stackSlot.copy(); }
        if (button == 2) { slot.putStack(ItemStack.EMPTY); }
        else if (button == 0 || button == 1) {
            ItemStack stackHeld = player.inventory.getItemStack();
            if (stackSlot.isEmpty()) {
                if (!stackHeld.isEmpty() && slot.isItemValid(stackHeld)) { slot.putStack(singleStackOf(stackHeld)); }
            }
            else if (stackHeld.isEmpty()) { slot.putStack(ItemStack.EMPTY); }
            else if (slot.isItemValid(stackHeld)) { slot.putStack(singleStackOf(stackHeld)); }
        }
        else if (button == 5) {
            ItemStack stackHeld = player.inventory.getItemStack();
            if (!slot.getHasStack()) { slot.putStack(singleStackOf(stackHeld)); }
        }
        return stack;
    }

    @Override @Nonnull public ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int slot) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slotObject = this.inventorySlots.get(slot);
        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack itemstack1 = slotObject.getStack();
            itemstack = itemstack1.copy();
            if (slot < slotCount) {
                if (!this.mergeItemStack(itemstack1, slotCount, this.inventorySlots.size(), true)) { return ItemStack.EMPTY; }
            }
            else if (!this.mergeItemStack(itemstack1, 0, slotCount, false)) { return ItemStack.EMPTY; }
            if (itemstack1.isEmpty()) { slotObject.putStack(ItemStack.EMPTY); }
            else { slotObject.onSlotChanged(); }
        }
        return itemstack;
    }

    @Override public void onContainerClosed(@Nonnull EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (inv != null) { this.inv.closeInventory(playerIn); }
    }
}
