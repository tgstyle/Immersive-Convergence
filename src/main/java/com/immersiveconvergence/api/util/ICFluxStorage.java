package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorage;
import net.minecraftforge.energy.IEnergyStorage;

@SuppressWarnings("unused")
public class ICFluxStorage extends FluxStorage implements IEnergyStorage {
    public boolean canExtract = true, canReceive = true;

    public ICFluxStorage(int capacity, int limitReceive, int limitExtract) { super(capacity, limitReceive, limitExtract); }
    public ICFluxStorage(int capacity, int limitTransfer) { super(capacity, limitTransfer); }
    public ICFluxStorage(int capacity) { super(capacity); }
    public ICFluxStorage(int capacity, boolean canExtract, boolean canReceive) {
        super(capacity);
        this.canExtract = canExtract;
        this.canReceive = canReceive;
        if (!canExtract) { limitExtract = 0; }
        if (!canReceive) { limitReceive = 0; }
    }

    @Override public boolean canExtract() { return canExtract; }

    @Override public boolean canReceive() { return canReceive; }
}
