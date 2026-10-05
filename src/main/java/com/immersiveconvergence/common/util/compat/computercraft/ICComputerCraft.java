package com.immersiveconvergence.common.util.compat.computercraft;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.compat.ICComputerTable;
import com.immersiveconvergence.api.compat.computercraft.ICPeripheralProvider;
import com.immersiveconvergence.common.util.compat.ICCompatModule;
import com.immersiveconvergence.common.util.compat.computers.IEComputerTables;
import com.immersiveconvergence.common.util.compat.computers.IEDeviceComputerTables;
import com.immersiveconvergence.common.util.compat.computers.IEMachineComputerTables;
import com.immersiveconvergence.common.util.compat.computers.IPComputerTables;

import dan200.computercraft.api.ComputerCraftAPI;

public class ICComputerCraft extends ICCompatModule {
    @Override public void preInit() {}

    @Override public void init() {
        if (!ICMods.immersiveEngineering()) { return; }
        ICPeripheralProvider provider = new ICPeripheralProvider();
        add(provider, IEComputerTables.ARC_FURNACE, IEComputerTables.ASSEMBLER, IEComputerTables.BOTTLING_MACHINE, IEComputerTables.CAPACITOR_LV, IEComputerTables.CAPACITOR_MV, IEComputerTables.CAPACITOR_HV, IEComputerTables.CAPACITOR_CREATIVE, IEComputerTables.CRUSHER, IEComputerTables.DIESEL_GENERATOR, IEComputerTables.ENERGY_METER, IEComputerTables.EXCAVATOR, IEComputerTables.FERMENTER, IEComputerTables.FLOODLIGHT, IEComputerTables.MIXER, IEComputerTables.REFINERY, IEComputerTables.SAMPLE_DRILL, IEComputerTables.SQUEEZER, IEComputerTables.TESLA_COIL);
        add(provider, IEMachineComputerTables.METAL_PRESS, IEMachineComputerTables.AUTO_WORKBENCH, IEMachineComputerTables.COKE_OVEN, IEMachineComputerTables.BLAST_FURNACE, IEMachineComputerTables.BLAST_FURNACE_ADVANCED, IEMachineComputerTables.ALLOY_SMELTER, IEMachineComputerTables.LIGHTNING_ROD, IEMachineComputerTables.SHEETMETAL_TANK, IEMachineComputerTables.SILO);
        add(provider, IEDeviceComputerTables.FLUID_PUMP, IEDeviceComputerTables.CLOCHE, IEDeviceComputerTables.THERMOELECTRIC_GENERATOR, IEDeviceComputerTables.TURRET_CHEM, IEDeviceComputerTables.TURRET_GUN, IEDeviceComputerTables.BARREL, IEDeviceComputerTables.FLUID_PLACER, IEDeviceComputerTables.PREHEATER, IEDeviceComputerTables.FURNACE_HEATER);
        if (ICMods.immersivePetroleum()) { add(provider, IPComputerTables.DISTILLATION_TOWER, IPComputerTables.PUMPJACK, IPComputerTables.GAS_GENERATOR, IPComputerTables.AUTO_LUBRICATOR); }
        ComputerCraftAPI.registerPeripheralProvider(provider);
    }

    private static void add(ICPeripheralProvider provider, ICComputerTable<?>... tables) { for (ICComputerTable<?> table : tables) { provider.add(table); } }
}
