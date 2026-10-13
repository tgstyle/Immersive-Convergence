package com.immersiveconvergence.api.energy;

import net.neoforged.neoforge.energy.IEnergyStorage;

@SuppressWarnings("unused") public interface IICEnergyStorage extends IEnergyStorage {
    void setStoredEnergy(int stored);

    default void modifyEnergyStored(int changeBy) { setStoredEnergy(getEnergyStored() + changeBy); }
}
