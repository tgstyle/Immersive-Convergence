package com.immersiveconvergence.api.gui;

import blusunrize.immersiveengineering.common.gui.ContainerIEBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

@SuppressWarnings("unused")
public class ICContainerBase<T extends TileEntity> extends ContainerIEBase<T> {
    public ICContainerBase(InventoryPlayer inventoryPlayer, T tile) { super(inventoryPlayer, tile); }
}
