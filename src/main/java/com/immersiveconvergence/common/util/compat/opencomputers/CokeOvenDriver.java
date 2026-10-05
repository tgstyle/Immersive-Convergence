package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.compat.opencomputers.ICTableDriver;
import com.immersiveconvergence.api.compat.opencomputers.ICTableEnvironment;
import com.immersiveconvergence.common.util.compat.computers.IEMachineComputerTables;

import blusunrize.immersiveengineering.common.blocks.stone.TileEntityCokeOven;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CokeOvenDriver extends ICTableDriver<TileEntityCokeOven> {
    public CokeOvenDriver() { super(IEMachineComputerTables.COKE_OVEN, CokeOvenEnvironment::new); }

    @SuppressWarnings("unused") public static class CokeOvenEnvironment extends ICTableEnvironment<TileEntityCokeOven> {
        public CokeOvenEnvironment(World world, BlockPos pos) { super(world, pos, IEMachineComputerTables.COKE_OVEN); }

        @Callback(doc = ICCallbackDocs.IS_ACTIVE) public Object[] isActive(Context context, Arguments args) { return call("isActive", args); }

        @Callback(doc = ICCallbackDocs.PROCESS) public Object[] getProcess(Context context, Arguments args) { return call("getProcess", args); }

        @Callback(doc = ICCallbackDocs.MAX_PROCESS) public Object[] getMaxProcess(Context context, Arguments args) { return call("getMaxProcess", args); }

        @Callback(doc = ICCallbackDocs.INPUT_STACK) public Object[] getInputStack(Context context, Arguments args) { return call("getInputStack", args); }

        @Callback(doc = ICCallbackDocs.OUTPUT_STACK) public Object[] getOutputStack(Context context, Arguments args) { return call("getOutputStack", args); }

        @Callback(doc = "function():table -- returns information about the creosote tank") public Object[] getTankInfo(Context context, Arguments args) { return call("getTankInfo", args); }

        @Callback(doc = "function():table -- returns the stack in the empty canister slot") public Object[] getEmptyCannisters(Context context, Arguments args) { return call("getEmptyCannisters", args); }

        @Callback(doc = "function():table -- returns the stack in the filled canister slot") public Object[] getFilledCannisters(Context context, Arguments args) { return call("getFilledCannisters", args); }
    }
}
