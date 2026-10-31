package com.immersiveconvergence.api.manual;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.bridge.ICBridges;

import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;

public final class ICManual {
    public static final String CAT_CONSTRUCTION = "construction";

    private ICManual() {}

    public static ICManualCategory category(ResourceLocation id, int priority) { return new ICManualCategory(null, id, priority); }

    public static ICManualCategory ieCategory(String name, int priority) { return category(ICMods.ie(name), priority); }

    public static void addEntry(ICManualCategory category, ResourceLocation file) {
        if (ICMods.immersiveEngineering()) { ICBridges.integration().addManualEntry(category, file); }
    }

    public static void addConfigGetter(Function<String, Object> getter) {
        if (ICMods.immersiveEngineering()) { ICBridges.integration().addManualConfigGetter(getter); }
    }

    public static void reset() {
        if (ICMods.immersiveEngineering()) { ICBridges.integration().resetManual(); }
    }

    public static void reload() {
        if (ICMods.immersiveEngineering()) { ICBridges.integration().reloadManual(); }
    }
}
