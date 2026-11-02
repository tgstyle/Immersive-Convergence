package com.immersiveconvergence.core.registration;

import com.immersiveconvergence.api.capability.HeatCapabilities;
import com.immersiveconvergence.api.capability.IHeatConsumer;
import com.immersiveconvergence.api.capability.IHeatProvider;
import com.immersiveconvergence.api.capability.IMechanicalEnergyConsumer;
import com.immersiveconvergence.api.capability.IMechanicalEnergyProvider;
import com.immersiveconvergence.api.capability.MechanicalCapabilities;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class ICCapabilities {
    private static final CapabilityToken<IHeatProvider> HEAT_PROVIDER_TOKEN = new CapabilityToken<>() {};
    private static final CapabilityToken<IHeatConsumer> HEAT_CONSUMER_TOKEN = new CapabilityToken<>() {};
    private static final CapabilityToken<IMechanicalEnergyProvider> MECHANICAL_PROVIDER_TOKEN = new CapabilityToken<>() {};
    private static final CapabilityToken<IMechanicalEnergyConsumer> MECHANICAL_CONSUMER_TOKEN = new CapabilityToken<>() {};

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IHeatProvider.class);
        event.register(IHeatConsumer.class);
        event.register(IMechanicalEnergyProvider.class);
        event.register(IMechanicalEnergyConsumer.class);
        HeatCapabilities.HEAT_PROVIDER_CAPABILITY = CapabilityManager.get(HEAT_PROVIDER_TOKEN);
        HeatCapabilities.HEAT_CONSUMER_CAPABILITY = CapabilityManager.get(HEAT_CONSUMER_TOKEN);
        MechanicalCapabilities.MECHANICAL_PROVIDER_CAPABILITY = CapabilityManager.get(MECHANICAL_PROVIDER_TOKEN);
        MechanicalCapabilities.MECHANICAL_CONSUMER_CAPABILITY = CapabilityManager.get(MECHANICAL_CONSUMER_TOKEN);
    }
}
