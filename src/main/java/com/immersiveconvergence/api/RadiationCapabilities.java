package com.immersiveconvergence.api;

import com.immersiveconvergence.api.capability.IRadiationConsumer;
import com.immersiveconvergence.api.capability.IRadiationProvider;
import com.immersiveconvergence.core.ICCommonConfig;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

@SuppressWarnings("unused")
public class RadiationCapabilities {
    public static double MAX_RADIATION = ICCommonConfig.maxRadiation;

    public static Capability<IRadiationProvider> RADIATION_PROVIDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static Capability<IRadiationConsumer> RADIATION_CONSUMER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
}
