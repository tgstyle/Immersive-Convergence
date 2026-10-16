package com.immersiveconvergence.api.energy;

import net.minecraft.util.Mth;
import net.neoforged.neoforge.energy.EnergyStorage;

public class ICEnergyStorage extends EnergyStorage implements IICEnergyStorage {
    public ICEnergyStorage(int capacity) { super(capacity); }

    public ICEnergyStorage(int capacity, int maxIO) { super(capacity, maxIO); }

    public ICEnergyStorage(int capacity, int maxInsert, int maxExtract) { super(capacity, maxInsert, maxExtract); }

    @Override public void setStoredEnergy(int stored) { this.energy = Mth.clamp(stored, 0, capacity); }
}
