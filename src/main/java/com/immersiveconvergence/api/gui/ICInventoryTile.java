package com.immersiveconvergence.api.gui;

import com.immersiveconvergence.api.util.IICInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import java.util.Collections;

@SuppressWarnings("unused")
public class ICInventoryTile implements IInventory {
    private static final String TILE_ENTITY_PREFIX = "TileEntity";
    private final TileEntity tile;
    private final IICInventory inv;
    private final String name;

    public ICInventoryTile(TileEntity tile) {
        this.tile = tile;
        this.inv = (IICInventory)tile;
        String simpleName = tile.getClass().getSimpleName();
        this.name = simpleName.startsWith(TILE_ENTITY_PREFIX) ? simpleName.substring(TILE_ENTITY_PREFIX.length()) : simpleName;
    }

    @Override @Nonnull public String getName() { return this.name; }

    @Override public boolean hasCustomName() { return false; }

    @Override @Nonnull public ITextComponent getDisplayName() { return new TextComponentString(this.name); }

    @Override public int getSizeInventory() { return inv.getInventory().size(); }

    @Override public boolean isEmpty() {
        for (ItemStack stack : inv.getInventory()) {
            if (!stack.isEmpty()) { return false; }
        }
        return true;
    }

    @Override @Nonnull public ItemStack getStackInSlot(int index) { return inv.getInventory().get(index); }

    @Override @Nonnull public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = inv.getInventory().get(index);
        if (!stack.isEmpty()) {
            if (stack.getCount() <= count) { inv.getInventory().set(index, ItemStack.EMPTY); }
            else {
                stack = stack.splitStack(count);
                if (stack.getCount() == 0) { inv.getInventory().set(index, ItemStack.EMPTY); }
            }
        }
        return stack;
    }

    @Override @Nonnull public ItemStack removeStackFromSlot(int index) {
        ItemStack ret = inv.getInventory().get(index).copy();
        inv.getInventory().set(index, ItemStack.EMPTY);
        return ret;
    }

    @Override public void setInventorySlotContents(int index, @Nonnull ItemStack stack) { inv.getInventory().set(index, stack); }

    @Override public int getInventoryStackLimit() { return 64; }

    @Override public void markDirty() { tile.markDirty(); }

    @Override public boolean isUsableByPlayer(@Nonnull EntityPlayer player) { return !tile.isInvalid() && tile.getDistanceSq(player.posX, player.posY, player.posZ) < 64; }

    @Override public void openInventory(@Nonnull EntityPlayer player) {}

    @Override public void closeInventory(@Nonnull EntityPlayer player) {
        for (int i = 0; i < getSizeInventory(); i++) { inv.doGraphicalUpdates(i); }
    }

    @Override public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) { return inv.isStackValid(index, stack); }

    @Override public int getField(int id) { return 0; }

    @Override public void setField(int id, int value) {}

    @Override public int getFieldCount() { return 0; }

    @Override public void clear() {
        Collections.fill(inv.getInventory(), ItemStack.EMPTY);
    }
}
