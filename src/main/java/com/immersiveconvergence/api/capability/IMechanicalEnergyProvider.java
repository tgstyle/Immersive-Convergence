package com.immersiveconvergence.api.capability;

@SuppressWarnings("unused")
public interface IMechanicalEnergyProvider {
    int getSpeed();
    float getTorque();
    int getMaxSpeed();
    double getBaseMass();
    double getDriveTorque();
    double getFriction();
}
