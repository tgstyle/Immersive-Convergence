package com.immersiveconvergence.core;

import com.immersiveconvergence.ImmersiveConvergence;

import net.minecraftforge.common.config.Config;

@SuppressWarnings("unused")
@Config(modid = ImmersiveConvergence.MODID, name = "immersiveconvergence_common")
public class ICCommonConfig {
    public static Mechanical mechanical = new Mechanical();
    public static Heat heat = new Heat();

    public static class Mechanical {
        @Config.Comment("The maximum rotational speed any mechanical device can reach, in RPM [Default=7200]")
        public int maxRpm = 7200;
    }

    public static class Heat {
        @Config.Comment("The maximum heat level any heat device can provide or require [Default=2000.0]")
        public double maxHeat = 2000.0;
    }
}
