package com.immersiveconvergence.api.capability;

import com.immersiveconvergence.api.client.MechanicalEnergyAnimation;

import net.minecraft.util.EnumFacing;

public interface IMechanicalEnergyProvider {
    boolean isValid();
    boolean isMechanicalEnergyTransmitter(EnumFacing facing);
    int getSpeed();
    float getTorqueMultiplier();
    MechanicalEnergyAnimation getAnimation();
}
