package com.immersiveconvergence.api.capability;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;

public class ExhaustCapabilities {
    public static final BlockCapability<IExhaustReceiver, Direction> EXHAUST_RECEIVER =
            BlockCapability.createSided(
                    ResourceLocation.fromNamespaceAndPath("immersiveconvergence", "exhaust_receiver"),
                    IExhaustReceiver.class
            );
}
