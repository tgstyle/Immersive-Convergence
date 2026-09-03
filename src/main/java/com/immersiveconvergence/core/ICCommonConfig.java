package com.immersiveconvergence.core;

import com.immersiveconvergence.api.multiblock.DisassemblyMode;
import com.immersiveconvergence.core.lib.ICLib;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ICLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ICCommonConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue MAX_RPM;
    public static final ForgeConfigSpec.DoubleValue MAX_HEAT;
    public static final ForgeConfigSpec.DoubleValue MAX_RADIATION;
    public static final ForgeConfigSpec.EnumValue<DisassemblyMode> DISASSEMBLY_MODE;

    public static int maxRpm = 7200;
    public static double maxHeat = 2000.0;
    public static double maxRadiation = 10000;
    public static DisassemblyMode disassemblyMode = DisassemblyMode.PROCESS_QUEUE;

    static {
        BUILDER.comment("Mechanical system global settings").push("mechanical");
        MAX_RPM = BUILDER
                .comment("Global maximum rotational speed in RPM for all mechanical devices (turbines, alternators, etc.). Default 7200 RPM. Changing this affects speed_factor calculations in turbines.")
                .defineInRange("max_rpm", 7200, 1000, 50000);
        BUILDER.pop();

        BUILDER.comment("Heat system global settings").push("heat");
        MAX_HEAT = BUILDER
                .comment("Global maximum heat level for all heat-related devices. Default 2000.0")
                .defineInRange("max_heat", 2000.0, 100.0, 10000.0);
        MAX_RADIATION = BUILDER
                .comment("Global maximum radiation level for all nuclear-related devices & players. Default 0.0")
                .defineInRange("max_radiation", 0.0, 0.0, 10000.0);
        BUILDER.pop();

        BUILDER.comment("Multiblock settings shared by every machine built on Immersive Convergence, Immersive Engineering's and Immersive Petroleum's included").push("multiblocks");
        DISASSEMBLY_MODE = BUILDER
                .comment("How a machine comes apart. PROCESS_QUEUE breaks it down block by block over a few ticks and drops all its materials at the broken block; TEMPLATE_BLOCKS instantly reverts it to its placed blocks. Sneaking while breaking always uses TEMPLATE_BLOCKS.")
                .defineEnum("disassemblyMode", DisassemblyMode.PROCESS_QUEUE);
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    public static void onConfig(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            maxRpm = MAX_RPM.get();
            maxHeat = MAX_HEAT.get();
            disassemblyMode = DISASSEMBLY_MODE.get();
        }
    }
}
