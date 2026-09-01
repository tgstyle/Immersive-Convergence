package com.immersiveconvergence.common.blocks.tileentities;

import com.immersiveconvergence.api.capability.IHeatProvider;
import com.immersiveconvergence.core.ICCommonConfig;

import blusunrize.immersiveengineering.common.blocks.TileEntityIEBase;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class TileEntityHeatCreative extends TileEntityIEBase implements IHeatProvider {
    @Override public double getHeatLevel() { return ICCommonConfig.heat.maxHeat; }

    @Override public void readCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) {}

    @Override public void writeCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) {}
}
