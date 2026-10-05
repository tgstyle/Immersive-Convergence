package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.compat.opencomputers.ICTableDriver;
import com.immersiveconvergence.api.compat.opencomputers.ICTableEnvironment;
import com.immersiveconvergence.common.util.compat.computers.IEMachineComputerTables;

import blusunrize.immersiveengineering.common.blocks.stone.TileEntityBlastFurnaceAdvanced;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlastFurnaceAdvancedDriver extends ICTableDriver<TileEntityBlastFurnaceAdvanced> {
    public BlastFurnaceAdvancedDriver() { super(IEMachineComputerTables.BLAST_FURNACE_ADVANCED, BlastFurnaceAdvancedEnvironment::new); }

    @SuppressWarnings("unused") public static class BlastFurnaceAdvancedEnvironment extends ICTableEnvironment<TileEntityBlastFurnaceAdvanced> {
        public BlastFurnaceAdvancedEnvironment(World world, BlockPos pos) { super(world, pos, IEMachineComputerTables.BLAST_FURNACE_ADVANCED); }

        @Callback(doc = ICCallbackDocs.IS_ACTIVE) public Object[] isActive(Context context, Arguments args) { return call("isActive", args); }

        @Callback(doc = ICCallbackDocs.PROCESS) public Object[] getProcess(Context context, Arguments args) { return call("getProcess", args); }

        @Callback(doc = ICCallbackDocs.MAX_PROCESS) public Object[] getMaxProcess(Context context, Arguments args) { return call("getMaxProcess", args); }

        @Callback(doc = ICCallbackDocs.BURN_TIME) public Object[] getBurnTime(Context context, Arguments args) { return call("getBurnTime", args); }

        @Callback(doc = ICCallbackDocs.INPUT_STACK) public Object[] getInputStack(Context context, Arguments args) { return call("getInputStack", args); }

        @Callback(doc = ICCallbackDocs.FUEL_STACK) public Object[] getFuelStack(Context context, Arguments args) { return call("getFuelStack", args); }

        @Callback(doc = ICCallbackDocs.OUTPUT_STACK) public Object[] getOutputStack(Context context, Arguments args) { return call("getOutputStack", args); }

        @Callback(doc = "function():table -- returns the stack in the slag slot") public Object[] getSlagStack(Context context, Arguments args) { return call("getSlagStack", args); }
    }
}
