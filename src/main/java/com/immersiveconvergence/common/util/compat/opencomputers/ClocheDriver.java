package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.compat.opencomputers.ICTableDriver;
import com.immersiveconvergence.api.compat.opencomputers.ICTableEnvironment;
import com.immersiveconvergence.common.util.compat.computers.IEDeviceComputerTables;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBelljar;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClocheDriver extends ICTableDriver<TileEntityBelljar> {
    public ClocheDriver() { super(IEDeviceComputerTables.CLOCHE, ClocheEnvironment::new); }

    @SuppressWarnings("unused") public static class ClocheEnvironment extends ICTableEnvironment<TileEntityBelljar> {
        public ClocheEnvironment(World world, BlockPos pos) { super(world, pos, IEDeviceComputerTables.CLOCHE); }

        @Callback(doc = ICCallbackDocs.ENERGY_STORED) public Object[] getEnergyStored(Context context, Arguments args) { return call("getEnergyStored", args); }

        @Callback(doc = ICCallbackDocs.MAX_ENERGY_STORED) public Object[] getMaxEnergyStored(Context context, Arguments args) { return call("getMaxEnergyStored", args); }

        @Callback(doc = "function():table -- returns information about the water tank") public Object[] getTankInfo(Context context, Arguments args) { return call("getTankInfo", args); }

        @Callback(doc = "function():int -- returns the amount of fertilizer left") public Object[] getFertilizer(Context context, Arguments args) { return call("getFertilizer", args); }

        @Callback(doc = "function():number -- returns the growth of the plant, from 0 to 1") public Object[] getGrowth(Context context, Arguments args) { return call("getGrowth", args); }

        @Callback(doc = "function():table -- returns the stack in the soil slot") public Object[] getSoilStack(Context context, Arguments args) { return call("getSoilStack", args); }

        @Callback(doc = "function():table -- returns the stack in the seed slot") public Object[] getSeedStack(Context context, Arguments args) { return call("getSeedStack", args); }

        @Callback(doc = "function(slot:int):table -- returns the stack in the specified output slot (1-4)") public Object[] getOutputStack(Context context, Arguments args) { return call("getOutputStack", args); }
    }
}
