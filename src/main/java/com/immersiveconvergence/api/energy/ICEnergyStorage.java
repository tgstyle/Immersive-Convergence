package com.immersiveconvergence.api.energy;

import net.neoforged.neoforge.energy.EnergyStorage;

@SuppressWarnings("unused") public class ICEnergyStorage extends EnergyStorage implements IICEnergyStorage {
    public ICEnergyStorage(int capacity) { super(capacity); }

    public ICEnergyStorage(int capacity, int maxIO) { super(capacity, maxIO); }

    public ICEnergyStorage(int capacity, int maxInsert, int maxExtract) { super(capacity, maxInsert, maxExtract); }

    @Override public void setStoredEnergy(int stored) { this.energy = stored; }
}
