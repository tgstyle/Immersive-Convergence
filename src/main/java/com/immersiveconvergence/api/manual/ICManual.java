package com.immersiveconvergence.api.manual;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.common.ie.IEManualBridge;

import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;

public final class ICManual {
    public static final String CAT_CONSTRUCTION = "construction";

    private ICManual() {}

    public static ICManualCategory category(ResourceLocation id, int priority) { return new ICManualCategory(null, id, priority); }

    public static ICManualCategory ieCategory(String name, int priority) { return category(ICMods.ie(name), priority); }

    public static void addEntry(ICManualCategory category, ResourceLocation file) {
        if (ICMods.immersiveEngineering()) { IEManualBridge.addEntry(category, file); }
    }

    public static void addConfigGetter(Function<String, Object> getter) {
        if (ICMods.immersiveEngineering()) { IEManualBridge.addConfigGetter(getter); }
    }

    public static void reset() {
        if (ICMods.immersiveEngineering()) { IEManualBridge.reset(); }
    }

    public static void reload() {
        if (ICMods.immersiveEngineering()) { IEManualBridge.reload(); }
    }
}
