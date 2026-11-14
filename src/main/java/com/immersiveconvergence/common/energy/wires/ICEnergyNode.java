package com.immersiveconvergence.common.energy.wires;

public interface ICEnergyNode {
    default boolean valid() { return true; }

    boolean isEnergyOutput();

    int outputEnergy(int amount, boolean simulate);

    void onEnergyPassthrough(int amount);
}
