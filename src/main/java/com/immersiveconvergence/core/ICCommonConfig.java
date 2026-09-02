package com.immersiveconvergence.core;

import com.immersiveconvergence.ImmersiveConvergence;

import net.minecraftforge.common.config.Config;

@SuppressWarnings("unused")
@Config(modid = ImmersiveConvergence.MODID, name = "immersiveconvergence_common")
public class ICCommonConfig {
    public static Mechanical mechanical = new Mechanical();
    public static Heat heat = new Heat();
    public static Multiblocks multiblocks = new Multiblocks();

    public enum DisassemblyMode { PROCESS_QUEUE, TEMPLATE_BLOCKS }

    public static class Mechanical {
        @Config.Comment("The maximum rotational speed any mechanical device can reach, in RPM [Default=7200]")
        public int maxRpm = 7200;
    }

    public static class Heat {
        @Config.Comment("The maximum heat level any heat device can provide or require [Default=2000.0]")
        public double maxHeat = 2000.0;
    }

    public static class Multiblocks {
        @Config.Comment("How a machine comes apart. PROCESS_QUEUE breaks it down block by block over a few ticks and drops all its materials at the broken block; TEMPLATE_BLOCKS instantly reverts it to the blocks it was built from. Sneak-breaking always uses TEMPLATE_BLOCKS. Applies to Immersive Engineering, Immersive Petroleum and Immersive Technology multiblocks alike [Default=PROCESS_QUEUE]")
        public DisassemblyMode disassemblyMode = DisassemblyMode.PROCESS_QUEUE;
    }
}
