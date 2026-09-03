package com.immersiveconvergence.core;

import com.immersiveconvergence.ImmersiveConvergence;

import net.minecraftforge.common.config.Config;

@SuppressWarnings("unused")
@Config(modid = ImmersiveConvergence.MODID, name = "immersiveconvergence_client")
public class ICClientConfig {
    public static Rendering rendering = new Rendering();

    public static class Rendering {
        @Config.Comment("Disables most lighting code for models rendered dynamically (TESR). May improve FPS. Affects various multiblocks [Default=false]")
        public boolean disableFancyTESR = false;
    }
}
