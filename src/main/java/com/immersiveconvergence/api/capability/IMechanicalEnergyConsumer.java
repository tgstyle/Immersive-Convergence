package com.immersiveconvergence.api.capability;

import com.immersiveconvergence.api.client.MechanicalEnergyAnimation;

import net.minecraft.util.EnumFacing;

public interface IMechanicalEnergyConsumer {
    boolean isValid();
    boolean isMechanicalEnergyReceiver(EnumFacing facing);
    int getSpeed();
    float getTorqueMultiplier();
    MechanicalEnergyAnimation getAnimation();
}
