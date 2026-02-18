package com.immersiveconvergence.api;

import com.immersiveconvergence.api.capability.IHeatProvider;
import com.immersiveconvergence.api.capability.IHeatConsumer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class HeatCapabilities {
    public static final double MAX_HEAT = 2000.0;
    public static Capability<IHeatProvider> HEAT_PROVIDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static Capability<IHeatConsumer> HEAT_CONSUMER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
}
