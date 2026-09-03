package com.immersiveconvergence.core;

import com.immersiveconvergence.api.multiblock.DisassemblyMode;
import com.immersiveconvergence.core.lib.ICLib;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = ICLib.MODID)
public class ICCommonConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MAX_RPM;
    public static final ModConfigSpec.DoubleValue MAX_HEAT;
    public static final ModConfigSpec.EnumValue<DisassemblyMode> DISASSEMBLY_MODE;

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

        BUILDER.comment("Multiblock settings shared by every machine built on Immersive Convergence, Immersive Engineering's and Immersive Petroleum's included").push("multiblocks");
        DISASSEMBLY_MODE = BUILDER
                .comment("How a machine comes apart. PROCESS_QUEUE breaks it down block by block over a few ticks and drops all its materials at the broken block; TEMPLATE_BLOCKS instantly reverts it to its placed blocks. Sneaking while breaking always uses TEMPLATE_BLOCKS.")
                .defineEnum("disassemblyMode", DisassemblyMode.PROCESS_QUEUE);
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    public static void onConfig(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            maxRpm = MAX_RPM.get();
            maxHeat = MAX_HEAT.get();
            disassemblyMode = DISASSEMBLY_MODE.get();
        }
    }
}
