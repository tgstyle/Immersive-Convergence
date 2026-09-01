package com.immersiveconvergence.core.registry;

import com.immersiveconvergence.core.lib.ICLib;
import com.immersiveconvergence.core.registration.ICBlocks;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.MissingMappingsEvent;

import java.util.HashMap;
import java.util.Map;

public final class ICRegistryRemaps {
    private static final Map<ResourceLocation, ResourceLocation> RENAMES = new HashMap<>();

    static {
        rename("immersivetechnology", "rotor_creative", ICBlocks.ROTOR_CREATIVE.getId());
        rename("immersivetechnology", "heat_creative", ICBlocks.HEAT_CREATIVE.getId());
    }

    private ICRegistryRemaps() {}

    public static void rename(String namespace, String path, ResourceLocation to) { RENAMES.put(ResourceLocation.fromNamespaceAndPath(namespace, path), to); }

    public static void handleRemapping(MissingMappingsEvent event) {
        handleRemapping(event, Registries.BLOCK, ForgeRegistries.BLOCKS);
        handleRemapping(event, Registries.ITEM, ForgeRegistries.ITEMS);
    }

    private static <T> void handleRemapping(MissingMappingsEvent event, ResourceKey<Registry<T>> key, IForgeRegistry<T> registry) {
        for (ResourceLocation source : RENAMES.keySet()) {
            for (MissingMappingsEvent.Mapping<T> mapping : event.getMappings(key, source.getNamespace())) {
                ResourceLocation renamed = RENAMES.get(mapping.getKey());
                if (renamed == null) { continue; }
                T value = registry.getValue(renamed);
                if (value == null) {
                    ICLib.IC_LOGGER.error("Registry rename sends {} to {}, but nothing is registered under that name", mapping.getKey(), renamed);
                    continue;
                }
                mapping.remap(value);
                ICLib.IC_LOGGER.info("Remapped {} to {}", mapping.getKey(), renamed);
            }
            break;
        }
    }
}
