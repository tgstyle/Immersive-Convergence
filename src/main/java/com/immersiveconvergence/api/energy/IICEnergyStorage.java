package com.immersiveconvergence.api.energy;

import net.neoforged.neoforge.energy.IEnergyStorage;

public interface IICEnergyStorage extends IEnergyStorage {
    void setStoredEnergy(int stored);
}
