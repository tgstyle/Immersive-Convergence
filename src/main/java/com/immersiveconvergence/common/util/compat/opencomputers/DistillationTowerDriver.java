package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.compat.opencomputers.ICTableDriver;
import com.immersiveconvergence.api.compat.opencomputers.ICTableEnvironment;
import com.immersiveconvergence.common.util.compat.computers.IPComputerTables;

import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityDistillationTower;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DistillationTowerDriver extends ICTableDriver<TileEntityDistillationTower> {
    public DistillationTowerDriver() { super(IPComputerTables.DISTILLATION_TOWER, DistillationTowerEnvironment::new); }

    @SuppressWarnings("unused") public static class DistillationTowerEnvironment extends ICTableEnvironment<TileEntityDistillationTower> {
        public DistillationTowerEnvironment(World world, BlockPos pos) { super(world, pos, IPComputerTables.DISTILLATION_TOWER); }

        @Callback(doc = ICCallbackDocs.ENERGY_STORED) public Object[] getEnergyStored(Context context, Arguments args) { return call("getEnergyStored", args); }

        @Callback(doc = ICCallbackDocs.MAX_ENERGY_STORED) public Object[] getMaxEnergyStored(Context context, Arguments args) { return call("getMaxEnergyStored", args); }

        @Callback(doc = "function():boolean -- checks whether the distillation tower is currently active") public Object[] isActive(Context context, Arguments args) { return call("isActive", args); }

        @Callback(doc = "function():table -- returns information about the input tank") public Object[] getInputTankInfo(Context context, Arguments args) { return call("getInputTankInfo", args); }

        @Callback(doc = "function():table -- returns the fluids in the output tank") public Object[] getOutputTanks(Context context, Arguments args) { return call("getOutputTanks", args); }

        @Callback(doc = "function():table -- returns the input and output stacks of the empty canister slots") public Object[] getEmptyCannisters(Context context, Arguments args) { return call("getEmptyCannisters", args); }

        @Callback(doc = "function():table -- returns the input and output stacks of the filled canister slots") public Object[] getFilledCannisters(Context context, Arguments args) { return call("getFilledCannisters", args); }

        @Callback(doc = ICCallbackDocs.ENABLE_COMPUTER_CONTROL) public Object[] enableComputerControl(Context context, Arguments args) { return call("enableComputerControl", args); }

        @Callback(doc = ICCallbackDocs.SET_ENABLED) public Object[] setEnabled(Context context, Arguments args) { return call("setEnabled", args); }
    }
}
