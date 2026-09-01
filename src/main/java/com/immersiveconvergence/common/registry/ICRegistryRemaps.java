package com.immersiveconvergence.common.registry;

import com.immersiveconvergence.common.util.ICLogger;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public final class ICRegistryRemaps {
    public static final ResourceLocation BLOCKS = new ResourceLocation("minecraft:blocks");
    public static final ResourceLocation ITEMS = new ResourceLocation("minecraft:items");

    private static final Map<ResourceLocation, Map<ResourceLocation, ResourceLocation>> REMAPS = new HashMap<>();

    private ICRegistryRemaps() {}

    public static void rename(ResourceLocation registry, String from, String to) { REMAPS.computeIfAbsent(registry, key -> new HashMap<>()).put(new ResourceLocation(from), new ResourceLocation(to)); }

    public static void renameBlockAndItem(String from, String to) {
        rename(BLOCKS, from, to);
        rename(ITEMS, from, to);
    }

    private static ResourceLocation follow(Map<ResourceLocation, ResourceLocation> target, ResourceLocation from) {
        ResourceLocation current = target.get(from);
        if (current == null) { return null; }
        Set<ResourceLocation> seen = new HashSet<>();
        seen.add(from);
        while (seen.add(current)) {
            ResourceLocation next = target.get(current);
            if (next == null) { return current; }
            current = next;
        }
        ICLogger.error("Registry rename chain from " + from + " loops back on itself, so it is ignored");
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @SubscribeEvent public static void onMissingMappings(RegistryEvent.MissingMappings event) {
        Map<ResourceLocation, ResourceLocation> target = REMAPS.get(event.getName());
        if (target == null) { return; }
        for (Object raw : event.getAllMappings()) {
            RegistryEvent.MissingMappings.Mapping mapping = (RegistryEvent.MissingMappings.Mapping)raw;
            ResourceLocation renamed = follow(target, mapping.key);
            if (renamed == null) { continue; }
            IForgeRegistryEntry value = mapping.registry.getValue(renamed);
            if (value == null) {
                ICLogger.error("Registry rename sends " + mapping.key + " to " + renamed + " in " + event.getName() + ", but nothing is registered under that name");
                continue;
            }
            mapping.remap(value);
            ICLogger.info("Remapped " + mapping.key + " to " + renamed + " in " + event.getName());
        }
    }
}
