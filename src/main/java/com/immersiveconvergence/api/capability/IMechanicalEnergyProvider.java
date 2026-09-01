package com.immersiveconvergence.api.capability;

import com.immersiveconvergence.api.client.MechanicalEnergyAnimation;

import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public interface IMechanicalEnergyProvider {
    boolean isValid();
    boolean isMechanicalEnergyTransmitter(EnumFacing facing);
    int getSpeed();
    int getMaxSpeed();
    float getTorqueMultiplier();
    double getBaseMass();
    double getDriveTorque();
    double getFriction();
    MechanicalEnergyAnimation getAnimation();
}
