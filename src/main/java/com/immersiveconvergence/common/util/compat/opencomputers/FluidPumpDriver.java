package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.compat.opencomputers.ICTableDriver;
import com.immersiveconvergence.api.compat.opencomputers.ICTableEnvironment;
import com.immersiveconvergence.common.util.compat.computers.IEDeviceComputerTables;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFluidPump;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidPumpDriver extends ICTableDriver<TileEntityFluidPump> {
    public FluidPumpDriver() { super(IEDeviceComputerTables.FLUID_PUMP, FluidPumpEnvironment::new); }

    @SuppressWarnings("unused") public static class FluidPumpEnvironment extends ICTableEnvironment<TileEntityFluidPump> {
        public FluidPumpEnvironment(World world, BlockPos pos) { super(world, pos, IEDeviceComputerTables.FLUID_PUMP); }

        @Callback(doc = ICCallbackDocs.ENERGY_STORED) public Object[] getEnergyStored(Context context, Arguments args) { return call("getEnergyStored", args); }

        @Callback(doc = ICCallbackDocs.MAX_ENERGY_STORED) public Object[] getMaxEnergyStored(Context context, Arguments args) { return call("getMaxEnergyStored", args); }

        @Callback(doc = ICCallbackDocs.TANK_INFO) public Object[] getTankInfo(Context context, Arguments args) { return call("getTankInfo", args); }

        @Callback(doc = "function():boolean -- checks whether the pump places cobblestone in drained source blocks") public Object[] isPlacingCobble(Context context, Arguments args) { return call("isPlacingCobble", args); }
    }
}
