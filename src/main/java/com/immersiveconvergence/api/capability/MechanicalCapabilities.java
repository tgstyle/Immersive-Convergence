package com.immersiveconvergence.api.capability;

import com.immersiveconvergence.core.ICCommonConfig;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;

@SuppressWarnings("unused")
public class MechanicalCapabilities {
    public static int maxRpm() { return ICCommonConfig.maxRpm; }

    public static final BlockCapability<IMechanicalEnergyProvider, Direction> MECHANICAL_PROVIDER =
            BlockCapability.createSided(
                    ResourceLocation.fromNamespaceAndPath("immersiveconvergence", "mechanical_provider"),
                    IMechanicalEnergyProvider.class
            );

    public static final BlockCapability<IMechanicalEnergyConsumer, Direction> MECHANICAL_CONSUMER =
            BlockCapability.createSided(
                    ResourceLocation.fromNamespaceAndPath("immersiveconvergence", "mechanical_consumer"),
                    IMechanicalEnergyConsumer.class
            );
}
