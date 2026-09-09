package com.immersiveconvergence.core;

import com.immersiveconvergence.ImmersiveConvergence;

import net.minecraftforge.common.config.Config;

@SuppressWarnings("unused")
@Config(modid = ImmersiveConvergence.MODID, name = "immersiveconvergence_common")
public class ICCommonConfig {
    public static Mechanical mechanical = new Mechanical();
    public static Heat heat = new Heat();
    public static Multiblocks multiblocks = new Multiblocks();
    public static Petroleum petroleum = new Petroleum();

    public enum DisassemblyMode { PROCESS_QUEUE, TEMPLATE_BLOCKS }

    public static class Mechanical {
        @Config.Comment("The maximum rotational speed any mechanical device can reach, in RPM [Default=7200]")
        public int maxRpm = 7200;
    }

    public static class Heat {
        @Config.Comment("The maximum heat level any heat device can provide or require [Default=2000.0]")
        public double maxHeat = 2000.0;
    }

    public static class Petroleum {
        @Config.Comment("Energy buffer of a pumpjack whose reservoir declares no power tier, in IF [Default=16000]")
        @Config.RangeInt(min = 1)
        @Config.RequiresMcRestart
        public int defaultCapacity = 16000;

        @Config.Comment("Energy a pumpjack whose reservoir declares no power tier consumes per tick, in IF [Default=1024]")
        @Config.RangeInt(min = 1)
        @Config.RequiresMcRestart
        public int defaultUsage = 1024;

        @Config.Comment("How fast a reservoir that declares no pump speed is pumped, in mB/tick [Default=25]")
        @Config.RangeInt(min = 1)
        @Config.RequiresMcRestart
        public int defaultPumpSpeed = 25;

        @Config.Comment("Draw the reservoir's power tier in the pumpjack JEI page [Default=true]")
        public boolean jeiDrawPowerTier = true;

        @Config.Comment("Draw the reservoir's spawn weight in the pumpjack JEI page [Default=true]")
        public boolean jeiDrawSpawnWeight = true;
    }

    public static class Multiblocks {
        @Config.Comment("How a machine comes apart. PROCESS_QUEUE breaks it down block by block over a few ticks and drops all its materials at the broken block; TEMPLATE_BLOCKS instantly reverts it to the blocks it was built from. Sneak-breaking always uses TEMPLATE_BLOCKS. Applies to Immersive Engineering, Immersive Petroleum and Immersive Technology multiblocks alike [Default=PROCESS_QUEUE]")
        public DisassemblyMode disassemblyMode = DisassemblyMode.PROCESS_QUEUE;
    }
}
