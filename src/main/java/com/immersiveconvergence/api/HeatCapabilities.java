package com.immersiveconvergence.api;

import com.immersiveconvergence.api.capability.IHeatConsumer;
import com.immersiveconvergence.api.capability.IHeatProvider;
import com.immersiveconvergence.core.ICCommonConfig;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;

@SuppressWarnings("unused")
public class HeatCapabilities {
    public static final double MAX_HEAT = ICCommonConfig.maxHeat;

    public static final BlockCapability<IHeatProvider, Direction> HEAT_PROVIDER =
            BlockCapability.createSided(
                    ResourceLocation.fromNamespaceAndPath("immersiveconvergence", "heat_provider"),
                    IHeatProvider.class
            );

    public static final BlockCapability<IHeatConsumer, Direction> HEAT_CONSUMER =
            BlockCapability.createSided(
                    ResourceLocation.fromNamespaceAndPath("immersiveconvergence", "heat_consumer"),
                    IHeatConsumer.class
            );
}
