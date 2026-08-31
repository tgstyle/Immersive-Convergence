package com.immersiveconvergence.api.capability;

import com.immersiveconvergence.core.ICCommonConfig;
import net.minecraftforge.common.capabilities.Capability;

@SuppressWarnings("unused")
public class MechanicalCapabilities {
    public static int MAX_RPM = ICCommonConfig.maxRpm;

    public static Capability<IMechanicalEnergyProvider> MECHANICAL_PROVIDER_CAPABILITY;
    public static Capability<IMechanicalEnergyConsumer> MECHANICAL_CONSUMER_CAPABILITY;
}
