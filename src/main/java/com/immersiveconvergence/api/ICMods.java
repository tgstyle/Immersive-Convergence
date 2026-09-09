package com.immersiveconvergence.api;

import net.minecraftforge.fml.common.Loader;

public final class ICMods {
    public static final String IMMERSIVE_ENGINEERING = "immersiveengineering";
    public static final String IMMERSIVE_PETROLEUM = "immersivepetroleum";

    private ICMods() {}

    public static boolean immersiveEngineering() { return Loader.isModLoaded(IMMERSIVE_ENGINEERING); }

    public static boolean immersivePetroleum() { return Loader.isModLoaded(IMMERSIVE_PETROLEUM); }
}
