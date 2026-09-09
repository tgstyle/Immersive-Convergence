package com.immersiveconvergence.api.energy;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

@SuppressWarnings("unused")
public class ICFluxWrapper implements IEnergyStorage {
    private final IICFluxConnector fluxHandler;
    public final EnumFacing side;

    public ICFluxWrapper(IICFluxConnector fluxHandler, EnumFacing side) {
        this.fluxHandler = fluxHandler;
        this.side = side;
    }

    private IICFluxHandler handler() { return fluxHandler instanceof IICFluxHandler ? (IICFluxHandler)fluxHandler : null; }

    @Override public int receiveEnergy(int maxReceive, boolean simulate) {
        IICFluxHandler handler = handler();
        return handler == null ? 0 : handler.receiveEnergy(side, maxReceive, simulate);
    }

    @Override public int extractEnergy(int maxExtract, boolean simulate) {
        IICFluxHandler handler = handler();
        return handler == null ? 0 : handler.extractEnergy(side, maxExtract, simulate);
    }

    @Override public int getEnergyStored() {
        IICFluxHandler handler = handler();
        return handler == null ? 0 : handler.getEnergyStored(side);
    }

    @Override public int getMaxEnergyStored() {
        IICFluxHandler handler = handler();
        return handler == null ? 0 : handler.getMaxEnergyStored(side);
    }

    @Override public boolean canExtract() {
        IICFluxHandler handler = handler();
        return handler != null && handler.getFluxStorage().getLimitExtract() > 0;
    }

    @Override public boolean canReceive() {
        IICFluxHandler handler = handler();
        return handler != null && handler.getFluxStorage().getLimitReceive() > 0;
    }

    public static ICFluxWrapper[] getDefaultWrapperArray(IICFluxConnector handler) {
        return new ICFluxWrapper[]{
                new ICFluxWrapper(handler, EnumFacing.DOWN),
                new ICFluxWrapper(handler, EnumFacing.UP),
                new ICFluxWrapper(handler, EnumFacing.NORTH),
                new ICFluxWrapper(handler, EnumFacing.SOUTH),
                new ICFluxWrapper(handler, EnumFacing.WEST),
                new ICFluxWrapper(handler, EnumFacing.EAST)
        };
    }
}
