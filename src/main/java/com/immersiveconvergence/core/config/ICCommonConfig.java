package com.immersiveconvergence.core.config;

import com.immersiveconvergence.api.multiblock.disassembly.DisassemblyMode;
import com.immersiveconvergence.core.lib.ICLib;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = ICLib.MODID) public class ICCommonConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.IntValue MAX_RPM;
    public static final ModConfigSpec.DoubleValue MAX_HEAT;
    public static final ModConfigSpec.EnumValue<DisassemblyMode> DISASSEMBLY_MODE;
    public static final ModConfigSpec.IntValue LV_RATE;
    public static final ModConfigSpec.IntValue MV_RATE;
    public static final ModConfigSpec.IntValue HV_RATE;
    public static final ModConfigSpec.IntValue PIPE_TRANSFER_RATE;
    public static final ModConfigSpec.IntValue PIPE_PRESSURIZED_TRANSFER_RATE;
    public static final ModConfigSpec.BooleanValue PIPE_LAST_SERVED;
    public static int pipeTransferRate = 100;
    public static int pipePressurizedTransferRate = 2500;
    public static boolean pipeLastServed = false;
    public static int lvRate = 2048;
    public static int mvRate = 8192;
    public static int hvRate = 32768;
    public static int maxRpm = 7200;
    public static double maxHeat = 2000.0;
    public static DisassemblyMode disassemblyMode = DisassemblyMode.PROCESS_QUEUE;

    static {
        BUILDER.comment("Mechanical system global settings").push("mechanical");
        MAX_RPM = BUILDER
                .comment("Global maximum rotational speed in RPM for all mechanical devices (turbines, alternators, etc.). Default 7200 RPM.")
                .defineInRange("max_rpm", 7200, 1000, 50000);
        BUILDER.pop();

        BUILDER.comment("Heat system global settings").push("heat");
        MAX_HEAT = BUILDER
                .comment("Global maximum heat level for all heat-related devices. Default 2000.0")
                .defineInRange("max_heat", 2000.0, 100.0, 10000.0);
        BUILDER.pop();

        BUILDER.comment("Wire settings").push("wires");
        LV_RATE = BUILDER
                .comment("The most energy per tick an Immersive Convergence low voltage terminal holds and passes on to the block it faces, in FE. Default 2048. A change needs a game restart.")
                .gameRestart()
                .defineInRange("lvRate", 2048, 1, Integer.MAX_VALUE);
        MV_RATE = BUILDER
                .comment("The most energy per tick an Immersive Convergence medium voltage terminal holds and passes on to the block it faces, in FE. Default 8192. A change needs a game restart.")
                .gameRestart()
                .defineInRange("mvRate", 8192, 1, Integer.MAX_VALUE);
        HV_RATE = BUILDER
                .comment("The most energy per tick an Immersive Convergence high voltage terminal holds and passes on to the block it faces, in FE. Default 32768. A change needs a game restart.")
                .gameRestart()
                .defineInRange("hvRate", 32768, 1, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.comment("Fluid pipe settings").push("pipes");
        PIPE_TRANSFER_RATE = BUILDER
                .comment("How much a fluid pipe can move per operation, in mB. Default 100.")
                .defineInRange("transferRate", 100, 1, Integer.MAX_VALUE);
        PIPE_PRESSURIZED_TRANSFER_RATE = BUILDER
                .comment("How much a fluid pipe can move per operation while pressurized, in mB. Default 2500.")
                .defineInRange("pressurizedTransferRate", 2500, 1, Integer.MAX_VALUE);
        PIPE_LAST_SERVED = BUILDER
                .comment("Pipes remember the last path they served (cheaper) instead of round-robining every destination. Default false.")
                .define("lastServed", false);
        BUILDER.pop();

        BUILDER.comment("Multiblock settings shared by every machine built on Immersive Convergence, Immersive Engineering's and Immersive Petroleum's included").push("multiblocks");
        DISASSEMBLY_MODE = BUILDER
                .comment("How a machine comes apart. PROCESS_QUEUE breaks it down block by block over a few ticks and drops all its materials at the broken block; TEMPLATE_BLOCKS instantly reverts it to its placed blocks. Sneaking while breaking always uses TEMPLATE_BLOCKS.")
                .defineEnum("disassembly_mode", DisassemblyMode.PROCESS_QUEUE);
        BUILDER.pop();

        BUILDER.comment("Experimental settings").push("experimental");
        BUILDER
                .comment(
                        "EXPERIMENTAL. Run as though Immersive Engineering were not installed, even when it is.",
                        "Every Immersive Engineering path in Immersive Convergence and Immersive Technology gates off: no wires, no fittings, no IE multiblock templates, and none of the content built out of IE blocks or items. Immersive Technology keeps two template sets, and its recipes switch between them at load. Immersive Petroleum gates off with it, because it cannot work without Immersive Engineering.",
                        "Recipes that name IE items will have no valid ingredients, multiblocks whose templates are built from IE blocks cannot be assembled, and anything already placed from them will not come back.",
                        "Turning this on means: I know what I am doing.",
                        "Read once at startup; a change needs a game restart.")
                .gameRestart()
                .define("disable_immersive_engineering", false);
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent public static void onConfig(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            lvRate = LV_RATE.get();
            mvRate = MV_RATE.get();
            hvRate = HV_RATE.get();
            maxRpm = MAX_RPM.get();
            maxHeat = MAX_HEAT.get();
            disassemblyMode = DISASSEMBLY_MODE.get();
            pipeTransferRate = PIPE_TRANSFER_RATE.get();
            pipePressurizedTransferRate = PIPE_PRESSURIZED_TRANSFER_RATE.get();
            pipeLastServed = PIPE_LAST_SERVED.get();
        }
    }
}
