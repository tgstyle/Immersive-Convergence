package com.immersiveconvergence.api.energy;

import com.immersiveconvergence.api.block.ICSideConfig;
import com.immersiveconvergence.api.util.ICFluxStorage;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public interface IICFluxHandler extends IICFluxConnector, IICFluxAcceptor, IICFluxProvider {
    @Nonnull ICFluxStorage getFluxStorage();

    default void postEnergyTransferUpdate(int energy, boolean simulate) {}

    @Override default int extractEnergy(@Nullable EnumFacing fd, int amount, boolean simulate) {
        if (((TileEntity)this).getWorld().isRemote || getEnergySideConfig(fd) != ICSideConfig.OUTPUT) { return 0; }
        int moved = getFluxStorage().extractEnergy(amount, simulate);
        postEnergyTransferUpdate(-moved, simulate);
        return moved;
    }

    @Override default int receiveEnergy(@Nullable EnumFacing fd, int amount, boolean simulate) {
        if (((TileEntity)this).getWorld().isRemote || getEnergySideConfig(fd) != ICSideConfig.INPUT) { return 0; }
        int moved = getFluxStorage().receiveEnergy(amount, simulate);
        postEnergyTransferUpdate(moved, simulate);
        return moved;
    }

    @Override default int getEnergyStored(@Nullable EnumFacing fd) { return getFluxStorage().getEnergyStored(); }

    @Override default int getMaxEnergyStored(@Nullable EnumFacing fd) { return getFluxStorage().getMaxEnergyStored(); }
}
