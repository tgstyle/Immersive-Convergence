package com.immersiveconvergence.api.energy;

import net.minecraftforge.energy.IEnergyStorage;

public interface IICEnergyStorage extends IEnergyStorage {
    void setStoredEnergy(int stored);
}
