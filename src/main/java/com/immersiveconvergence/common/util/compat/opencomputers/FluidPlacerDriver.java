package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.compat.opencomputers.ICTableDriver;
import com.immersiveconvergence.api.compat.opencomputers.ICTableEnvironment;
import com.immersiveconvergence.common.util.compat.computers.IEDeviceComputerTables;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFluidPlacer;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidPlacerDriver extends ICTableDriver<TileEntityFluidPlacer> {
    public FluidPlacerDriver() { super(IEDeviceComputerTables.FLUID_PLACER, FluidPlacerEnvironment::new); }

    @SuppressWarnings("unused") public static class FluidPlacerEnvironment extends ICTableEnvironment<TileEntityFluidPlacer> {
        public FluidPlacerEnvironment(World world, BlockPos pos) { super(world, pos, IEDeviceComputerTables.FLUID_PLACER); }

        @Callback(doc = ICCallbackDocs.TANK_INFO) public Object[] getTankInfo(Context context, Arguments args) { return call("getTankInfo", args); }
    }
}
