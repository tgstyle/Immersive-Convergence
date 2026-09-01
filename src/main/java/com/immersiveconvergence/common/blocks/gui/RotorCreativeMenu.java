package com.immersiveconvergence.common.blocks.gui;

import com.immersiveconvergence.common.blocks.logic.RotorCreativeBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class RotorCreativeMenu extends AbstractContainerMenu {
    public RotorCreativeBlockEntity tile;
    private int rpm;

    public RotorCreativeMenu(MenuType<?> type, int id, Inventory inv, RotorCreativeBlockEntity tile) {
        super(type, id);
        this.tile = tile;
        addDataSlot(new DataSlot() {
            @Override public int get() { return tile.rpm; }
            @Override public void set(int value) { rpm = value; }
        });
    }

    public RotorCreativeMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf buffer) {
        super(type, id);
        BlockPos pos = buffer.readBlockPos();
        this.tile = (RotorCreativeBlockEntity)inv.player.level().getBlockEntity(pos);
        addDataSlot(new DataSlot() {
            @Override public int get() { return 0; }
            @Override public void set(int value) { rpm = value; }
        });
    }

    public static RotorCreativeMenu makeServer(MenuType<RotorCreativeMenu> type, int id, Inventory inv, RotorCreativeBlockEntity tile) { return new RotorCreativeMenu(type, id, inv, tile); }

    public static RotorCreativeMenu makeClient(MenuType<RotorCreativeMenu> type, int id, Inventory inv, FriendlyByteBuf buffer) { return new RotorCreativeMenu(type, id, inv, buffer); }

    @Override public boolean stillValid(@Nonnull Player player) { return tile != null && tile.stillValid(player); }

    @Override @Nonnull public ItemStack quickMoveStack(@Nonnull Player player, int index) { return ItemStack.EMPTY; }

    public int getRpm() { return rpm; }
}
