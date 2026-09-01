package com.immersiveconvergence.core;

import com.immersiveconvergence.ImmersiveConvergence;

import net.minecraftforge.common.config.Config;

@SuppressWarnings("unused")
@Config(modid = ImmersiveConvergence.MODID, name = "immersiveconvergence_mixin")
public class ICMixinConfig {
    public static MixinSettings mixinSettings = new MixinSettings();

    public static class MixinSettings {
        @Config.Comment("Enable debug logging for tile entity additions in the World mixin (Only works if enableWorldMixin is true) [Default=false]")
        public boolean enableAdditionsLogging = false;

        @Config.Comment("Enable debug logging for tile entity potentials in the World mixin (Only works if enableWorldMixin is true) [Default=true]")
        public boolean enablePotentialsLogging = true;

        @Config.Comment("Enable the World mixin for tile entity additions (CME Fix) [Default=true]")
        public boolean enableWorldMixin = true;

        @Config.Comment("Enable the MinecraftServer mixin to redirect error logging for crash debugging [Default=true]")
        public boolean enableErrorLoggingRedirect = true;
    }
}
