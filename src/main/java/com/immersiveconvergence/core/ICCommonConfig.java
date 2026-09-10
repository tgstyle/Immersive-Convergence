package com.immersiveconvergence.core;

import com.immersiveconvergence.ImmersiveConvergence;

import net.minecraftforge.common.config.Config;

@SuppressWarnings("unused")
@Config(modid = ImmersiveConvergence.MODID, name = "immersiveconvergence_common")
public class ICCommonConfig {
    public static Multiblocks multiblocks = new Multiblocks();
    public static Mechanical mechanical = new Mechanical();
    public static Heat heat = new Heat();
    public static Pipes pipes = new Pipes();
    public static Petroleum petroleum = new Petroleum();
    public static Experimental experimental = new Experimental();

    public enum DisassemblyMode { PROCESS_QUEUE, TEMPLATE_BLOCKS }

    public static class Experimental {
        @Config.Comment({
                "EXPERIMENTAL. Run as though Immersive Engineering were not installed, even when it is.",
                "Every Immersive Engineering path in Immersive Convergence and Immersive Technology gates off: no wires, no conveyors, no connectors, no IE multiblock templates, and none of the content built out of IE blocks or items. Immersive Petroleum gates off with it, because it cannot work without Immersive Engineering.",
                "Recipes that name IE items will have no valid ingredients, multiblocks whose templates are built from IE blocks cannot be assembled, and anything already placed from them will not come back.",
                "Turning this on means: I know what I am doing, and I can replace the needed recipes, and NBT data myself.",
                "[Default=false]"})
        @Config.RequiresMcRestart
        public boolean disableImmersiveEngineering = false;
    }

    public static class Multiblocks {
        @Config.Comment("How a machine comes apart. PROCESS_QUEUE breaks it down block by block over a few ticks and drops all its materials at the broken block; TEMPLATE_BLOCKS instantly reverts it to the blocks it was built from. Sneak-breaking always uses TEMPLATE_BLOCKS. Applies to Immersive Engineering, Immersive Petroleum and Immersive Technology multiblocks alike [Default=PROCESS_QUEUE]")
        public DisassemblyMode disassemblyMode = DisassemblyMode.PROCESS_QUEUE;
    }

    public static class Mechanical {
        @Config.Comment("The maximum rotational speed any mechanical device can reach, in RPM [Default=7200]")
        public int maxRpm = 7200;
    }

    public static class Heat {
        @Config.Comment("The maximum heat level any heat device can provide or require [Default=2000.0]")
        public double maxHeat = 2000.0;
    }

    public static class Pipes {
        @Config.Comment("How much a fluid pipe can move per operation, in mB [Default=100]")
        @Config.RangeInt(min = 1)
        public int transferRate = 100;

        @Config.Comment("How much a fluid pipe can move per operation while pressurized, in mB [Default=2500]")
        @Config.RangeInt(min = 1)
        public int pressurizedTransferRate = 2500;

        @Config.Comment("Pipes remember the last path they served (cheaper) instead of round-robining every destination [Default=false]")
        public boolean lastServed = false;
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

}
