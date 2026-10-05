package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.compat.opencomputers.ICTableDriver;
import com.immersiveconvergence.api.compat.opencomputers.ICTableEnvironment;
import com.immersiveconvergence.common.util.compat.computers.IPComputerTables;

import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityGasGenerator;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GasGeneratorDriver extends ICTableDriver<TileEntityGasGenerator> {
    public GasGeneratorDriver() { super(IPComputerTables.GAS_GENERATOR, GasGeneratorEnvironment::new); }

    @SuppressWarnings("unused") public static class GasGeneratorEnvironment extends ICTableEnvironment<TileEntityGasGenerator> {
        public GasGeneratorEnvironment(World world, BlockPos pos) { super(world, pos, IPComputerTables.GAS_GENERATOR); }

        @Callback(doc = ICCallbackDocs.ENERGY_STORED) public Object[] getEnergyStored(Context context, Arguments args) { return call("getEnergyStored", args); }

        @Callback(doc = ICCallbackDocs.MAX_ENERGY_STORED) public Object[] getMaxEnergyStored(Context context, Arguments args) { return call("getMaxEnergyStored", args); }

        @Callback(doc = "function():boolean -- checks whether the generator is currently producing energy") public Object[] isActive(Context context, Arguments args) { return call("isActive", args); }

        @Callback(doc = "function():table -- returns information about the fuel tank") public Object[] getTankInfo(Context context, Arguments args) { return call("getTankInfo", args); }
    }
}
