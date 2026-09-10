package com.immersiveconvergence.api;

import net.minecraftforge.fml.common.Loader;

public final class ICMods {
    public static final String IMMERSIVE_ENGINEERING = "immersiveengineering";
    public static final String IMMERSIVE_PETROLEUM = "immersivepetroleum";

    private ICMods() {}

    private static boolean immersiveEngineering;
    private static boolean immersivePetroleum;

    public static void init(boolean disableImmersiveEngineering) {
        immersiveEngineering = !disableImmersiveEngineering && Loader.isModLoaded(IMMERSIVE_ENGINEERING);
        immersivePetroleum = immersiveEngineering && Loader.isModLoaded(IMMERSIVE_PETROLEUM);
    }

    public static boolean immersiveEngineering() { return immersiveEngineering; }

    public static boolean immersivePetroleum() { return immersivePetroleum; }
}
