package com.immersiveconvergence.common.energy.wires;

public interface ICEnergyNode {
    boolean isEnergyOutput();

    int outputEnergy(int amount, boolean simulate);

    void onEnergyPassthrough(int amount);
}
