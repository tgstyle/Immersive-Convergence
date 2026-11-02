package com.immersiveconvergence.core.compat.opencomputers;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.core.compat.opencomputers.ie.IEOpenComputers;

import li.cil.oc.api.Driver;
import net.neoforged.fml.InterModComms;
import java.util.List;
import java.util.Map;

public final class ICOpenComputers {
    private static final String MODERNIZED_REGISTRY = "li.cil.oc.core.impl.server.driver.Registry";
    private static final List<String> CC_BRIDGES = List.of("blusunrize.immersiveengineering.common.util.compat.computers.cctweaked.GenericPeripheral", "com.immersiveconvergence.api.compat.computercraft.ICPeripheral");
    private static final Map<String, String> IE_NAMES = Map.ofEntries(
            Map.entry("arc_furnace", "ie_arc_furnace"),
            Map.entry("assembler", "ie_assembler"),
            Map.entry("bottling_machine", "ie_bottling_machine"),
            Map.entry("capacitor_lv", "ie_lv_capacitor"),
            Map.entry("capacitor_mv", "ie_mv_capacitor"),
            Map.entry("capacitor_hv", "ie_hv_capacitor"),
            Map.entry("capacitor_creative", "ie_creative_capacitor"),
            Map.entry("crusher", "ie_crusher"),
            Map.entry("diesel_generator", "ie_diesel_generator"),
            Map.entry("current_transformer", "ie_current_transformer"),
            Map.entry("exavator", "ie_excavator"),
            Map.entry("fermenter", "ie_fermenter"),
            Map.entry("floodlight", "ie_floodlight"),
            Map.entry("mixer", "ie_mixer"),
            Map.entry("refinery", "ie_refinery"),
            Map.entry("sample_drill", "ie_sample_drill"),
            Map.entry("squeezer", "ie_squeezer"),
            Map.entry("tesla_coil", "ie_tesla_coil"),
            Map.entry("metal_press", "ie_metal_press"),
            Map.entry("auto_workbench", "ie_auto_workbench"),
            Map.entry("coke_oven", "ie_coke_oven"),
            Map.entry("blast_furnace", "ie_blast_furnace"),
            Map.entry("advanced_blast_furnace", "ie_blast_furnace_advanced"),
            Map.entry("alloy_smelter", "ie_alloy_smelter"),
            Map.entry("lightning_rod", "ie_lightning_rod"),
            Map.entry("sheetmetal_tank", "ie_sheetmetal_tank"),
            Map.entry("silo", "ie_silo"),
            Map.entry("fluid_pump", "ie_fluid_pump"),
            Map.entry("cloche", "ie_cloche"),
            Map.entry("thermoelectric_generator", "ie_thermoelectric_generator"),
            Map.entry("turret_chem", "ie_turret_chem"),
            Map.entry("turret_gun", "ie_turret_gun"),
            Map.entry("wooden_barrel", "ie_barrel"),
            Map.entry("sawmill", "ie_sawmill"),
            Map.entry("metal_barrel", "ie_metal_barrel"),
            Map.entry("fluid_placer", "ie_fluid_placer"),
            Map.entry("blast_furnace_preheater", "ie_blast_furnace_preheater"),
            Map.entry("furnace_heater", "ie_furnace_heater"),
            Map.entry("distillation_tower", "ip_distillation_tower"),
            Map.entry("pumpjack", "ip_pumpjack"),
            Map.entry("gas_generator", "ip_gas_generator"),
            Map.entry("auto_lubricator", "ip_auto_lubricator"));

    private ICOpenComputers() {}

    public static void register() {
        Object registry = modernizedRegistry();
        if (registry != null) { setLocked(registry, false); }
        try {
            Driver.add(new ICTableBlockDriver());
            if (ICMods.immersiveEngineering()) { IEOpenComputers.register(IE_NAMES); }
        }
        finally {
            if (registry != null) { setLocked(registry, true); }
        }
        if (ICMods.computerCraft()) { blacklistInSettings(); }
    }

    private static Object modernizedRegistry() {
        try { return Class.forName(MODERNIZED_REGISTRY).getField("INSTANCE").get(null); }
        catch (ClassNotFoundException e) { return null; }
        catch (ReflectiveOperationException e) { throw new IllegalStateException(e); }
    }

    private static void setLocked(Object registry, boolean locked) {
        try { registry.getClass().getMethod("setLocked", boolean.class).invoke(registry, locked); }
        catch (ReflectiveOperationException e) { throw new IllegalStateException(e); }
    }

    public static void enqueueIMC() {
        if (!ICMods.computerCraft()) { return; }
        for (String bridge : CC_BRIDGES) { InterModComms.sendTo(ICMods.OPENCOMPUTERS, "blacklistPeripheral", () -> bridge); }
    }

    @SuppressWarnings("unchecked") private static void blacklistInSettings() {
        try {
            Class<?> settingsClass = Class.forName("li.cil.oc.core.impl.OCSettings");
            Object settings = settingsClass.getMethod("get").invoke(null);
            List<String> blacklist = (List<String>) settingsClass.getField("peripheralBlacklist").get(settings);
            for (String bridge : CC_BRIDGES) {
                if (!blacklist.contains(bridge)) { blacklist.add(bridge); }
            }
        }
        catch (ReflectiveOperationException e) {
            if (!(e instanceof ClassNotFoundException)) { throw new IllegalStateException(e); }
        }
    }
}
