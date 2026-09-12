package com.immersiveconvergence.api;

import com.immersiveconvergence.core.ICCommonConfig;

import net.minecraftforge.fml.common.Loader;

public final class ICMods {
    public static final String IMMERSIVE_ENGINEERING = "immersiveengineering";
    public static final String IMMERSIVE_PETROLEUM = "immersivepetroleum";

    private ICMods() {}

    private static boolean immersiveEngineering;
    private static boolean immersivePetroleum;
    private static boolean initialized;

    public static void init(boolean disableImmersiveEngineering) {
        immersiveEngineering = !disableImmersiveEngineering && Loader.isModLoaded(IMMERSIVE_ENGINEERING);
        immersivePetroleum = immersiveEngineering && Loader.isModLoaded(IMMERSIVE_PETROLEUM);
        initialized = true;
    }

    private static void ensureInitialized() { if (!initialized) { init(ICCommonConfig.experimental.disableImmersiveEngineering); } }

    public static boolean immersiveEngineering() { ensureInitialized(); return immersiveEngineering; }

    public static boolean immersivePetroleum() { ensureInitialized(); return immersivePetroleum; }
}
