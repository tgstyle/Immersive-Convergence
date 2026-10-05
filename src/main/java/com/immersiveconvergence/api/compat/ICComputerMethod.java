package com.immersiveconvergence.api.compat;

import net.minecraft.tileentity.TileEntity;

@FunctionalInterface
public interface ICComputerMethod<T extends TileEntity> {
    Object[] call(T tile, Object[] args);
}
