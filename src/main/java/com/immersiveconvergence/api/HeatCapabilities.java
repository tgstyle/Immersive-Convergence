package com.immersiveconvergence.api;

import com.immersiveconvergence.api.capability.IHeatProvider;
import com.immersiveconvergence.api.capability.IHeatConsumer;
import com.immersiveconvergence.core.ICCommonConfig;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class HeatCapabilities {
    public static double MAX_HEAT = ICCommonConfig.maxHeat;

    public static Capability<IHeatProvider> HEAT_PROVIDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static Capability<IHeatConsumer> HEAT_CONSUMER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
}
