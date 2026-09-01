package com.immersiveconvergence.api.capability;

import com.immersiveconvergence.api.client.MechanicalEnergyAnimation;

import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public interface IMechanicalEnergyConsumer {
    boolean isValid();
    boolean isMechanicalEnergyReceiver(EnumFacing facing);
    int getSpeed();
    int getMaxSpeed();
    float getTorqueMultiplier();
    double getMass();
    double getFriction();
    MechanicalEnergyAnimation getAnimation();
}
