package com.immersiveconvergence.api.capability;

import com.immersiveconvergence.api.client.MechanicalEnergyAnimation;

import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public interface IMechanicalEnergyConsumer {
    double getMass();
    double getFriction();
    int getMaxSpeed();
    boolean isValid();
    boolean isMechanicalEnergyReceiver(EnumFacing facing);
    int getSpeed();
    default int getEffectiveMaxSpeed() { return getMaxSpeed(); }
    MechanicalEnergyAnimation getAnimation();
}
