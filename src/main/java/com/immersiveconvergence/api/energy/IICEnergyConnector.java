package com.immersiveconvergence.api.energy;

public interface IICEnergyConnector {
    boolean isEnergySource(ICConnectionPoint cp);

    boolean isEnergySink(ICConnectionPoint cp);

    int energyAvailable();

    int energyRequested();

    void energyInserted(int amount);

    void energyExtracted(int amount);

    void energyPassedThrough(double amount);
}
