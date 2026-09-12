package com.immersiveconvergence.api.capability;

import com.immersiveconvergence.api.client.MechanicalEnergyAnimation;

import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public interface IMechanicalEnergyProvider {
    int getSpeed();
    float getTorque();
    int getMaxSpeed();
    double getBaseMass();
    double getDriveTorque();
    double getFriction();
    boolean isValid();
    boolean isMechanicalEnergyTransmitter(EnumFacing facing);
    MechanicalEnergyAnimation getAnimation();
}
