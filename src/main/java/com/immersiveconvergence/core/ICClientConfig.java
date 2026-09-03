package com.immersiveconvergence.core;

import com.immersiveconvergence.ImmersiveConvergence;

import net.minecraftforge.common.config.Config;

@SuppressWarnings("unused")
@Config(modid = ImmersiveConvergence.MODID, name = "immersiveconvergence_client")
public class ICClientConfig {
    public static Rendering rendering = new Rendering();
    public static Jei jei = new Jei();

    public static class Rendering {
        @Config.Comment("Disables most lighting code for models rendered dynamically (TESR). May improve FPS. Affects various multiblocks [Default=false]")
        public boolean disableFancyTESR = false;
    }

    public static class Jei {
        @Config.Comment("Show the multiblocks of Immersive Engineering, Immersive Petroleum and Immersive Technology in JEI's ingredient list, drawn as their assembled models. Their recipe pages and the machine icons on those pages stay either way [Default=true]")
        public boolean showMultiblockItems = true;
    }
}
