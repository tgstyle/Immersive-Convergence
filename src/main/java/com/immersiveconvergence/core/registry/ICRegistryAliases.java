package com.immersiveconvergence.core.registry;

import com.immersiveconvergence.core.registration.ICBlocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class ICRegistryAliases {
    private ICRegistryAliases() {}

    public static void register() {
        alias("rotor_creative", ICBlocks.ROTOR_CREATIVE.getId());
        alias("heat_creative", ICBlocks.HEAT_CREATIVE.getId());
    }

    private static void alias(String path, ResourceLocation to) {
        ResourceLocation from = ResourceLocation.fromNamespaceAndPath("immersivetechnology", path);
        BuiltInRegistries.BLOCK.addAlias(from, to);
        BuiltInRegistries.ITEM.addAlias(from, to);
    }
}
