package com.immersiveconvergence.api.block;

import com.immersiveconvergence.api.gui.ArgContainer;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;

@SuppressWarnings({"unused", "RedundantSuppression"}) public interface IArgMenuProvider<T extends BlockEntity & IArgMenuProvider<T>> extends IMasterMenuProvider<T> {
    ArgContainer<? super T, ?> getContainerType();

    @Nonnull default AbstractContainerMenu createMenu(int id, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        T master = getGuiMaster();
        Preconditions.checkNotNull(master);
        ArgContainer<? super T, ?> type = getContainerType();
        return type.create(id, playerInventory, master);
    }
}
