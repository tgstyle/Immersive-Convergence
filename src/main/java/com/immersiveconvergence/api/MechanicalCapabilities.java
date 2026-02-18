package com.immersiveconvergence.api;

import com.immersiveconvergence.core.ICCommonConfig;
import com.immersiveconvergence.api.capability.IMechanicalEnergyConsumer;
import com.immersiveconvergence.api.capability.IMechanicalEnergyProvider;
import net.minecraftforge.common.capabilities.Capability;

public class MechanicalCapabilities {
    public static int MAX_RPM = ICCommonConfig.maxRpm;

    public static Capability<IMechanicalEnergyProvider> MECHANICAL_PROVIDER_CAPABILITY;
    public static Capability<IMechanicalEnergyConsumer> MECHANICAL_CONSUMER_CAPABILITY;
}
