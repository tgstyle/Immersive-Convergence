package com.immersiveconvergence.core;

import com.immersiveconvergence.core.lib.ICLib;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ICLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ICClientConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue DISABLE_FANCY_TESR;

    public static boolean disableFancyTESR = false;

    static {
        BUILDER.comment("Rendering settings shared by every mod built on Immersive Convergence").push("rendering");
        DISABLE_FANCY_TESR = BUILDER
                .comment("Disables most lighting code for models rendered dynamically (TESR). May improve FPS. Affects various multiblocks.")
                .define("disableFancyTESR", false);
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent public static void onConfig(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) { disableFancyTESR = DISABLE_FANCY_TESR.get(); }
    }
}
