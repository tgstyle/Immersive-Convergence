package com.immersiveconvergence.core;

import com.immersiveconvergence.ImmersiveConvergence;

import net.minecraftforge.common.config.Config;

@SuppressWarnings("unused")
@Config(modid = ImmersiveConvergence.MODID, name = "immersiveconvergence_mixin")
public class ICMixinConfig {
    public static MixinSettings mixinSettings = new MixinSettings();

    public static class MixinSettings {
        @Config.Comment("Replace Immersive Engineering's fluid pipes and fluid pump with Immersive Convergence's own implementations. Has no effect without Immersive Engineering [Default=true]")
        public boolean replaceIEPipes = true;

        @Config.Comment("Replace Immersive Engineering's conveyors with Immersive Convergence's own implementations. Has no effect without Immersive Engineering [Default=true]")
        public boolean replaceIEConveyors = true;

        @Config.Comment("Enable the World mixin that fixes the concurrent modification crash when tile entities are added while the world is ticking [Default=true]")
        public boolean enableWorldMixin = true;

        @Config.Comment("Enable the MinecraftServer mixin that redirects error logging, for crash debugging [Default=true]")
        public boolean enableErrorLoggingRedirect = true;

        @Config.Comment("Log every tile entity addition the World mixin sees. Needs enableWorldMixin [Default=false]")
        public boolean enableAdditionsLogging = false;

        @Config.Comment("Log the tile entity additions the World mixin judges risky. Needs enableWorldMixin [Default=true]")
        public boolean enablePotentialsLogging = true;
    }
}
