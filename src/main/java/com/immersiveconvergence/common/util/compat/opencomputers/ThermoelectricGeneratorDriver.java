package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.compat.opencomputers.ICTableDriver;
import com.immersiveconvergence.api.compat.opencomputers.ICTableEnvironment;
import com.immersiveconvergence.common.util.compat.computers.IEDeviceComputerTables;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityThermoelectricGen;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThermoelectricGeneratorDriver extends ICTableDriver<TileEntityThermoelectricGen> {
    public ThermoelectricGeneratorDriver() { super(IEDeviceComputerTables.THERMOELECTRIC_GENERATOR, ThermoelectricGeneratorEnvironment::new); }

    @SuppressWarnings("unused") public static class ThermoelectricGeneratorEnvironment extends ICTableEnvironment<TileEntityThermoelectricGen> {
        public ThermoelectricGeneratorEnvironment(World world, BlockPos pos) { super(world, pos, IEDeviceComputerTables.THERMOELECTRIC_GENERATOR); }

        @Callback(doc = "function():int -- returns the amount of energy produced each tick") public Object[] getEnergyOutput(Context context, Arguments args) { return call("getEnergyOutput", args); }
    }
}
