package com.immersiveconvergence.core;

import com.immersiveconvergence.core.lib.ICLib;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = ICLib.MODID)
public class ICClientConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue DISABLE_FANCY_TESR;

    public static boolean disableFancyTESR = false;

    static {
        BUILDER.comment("Rendering settings shared by every mod built on Immersive Convergence").push("rendering");
        DISABLE_FANCY_TESR = BUILDER
                .comment("Disables most lighting code for models rendered dynamically (TESR). May improve FPS. Affects various multiblocks.")
                .define("disableFancyTESR", false);
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent public static void onConfig(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) { disableFancyTESR = DISABLE_FANCY_TESR.get(); }
    }
}
