package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.compat.opencomputers.ICTableDriver;
import com.immersiveconvergence.api.compat.opencomputers.ICTableEnvironment;
import com.immersiveconvergence.common.util.compat.computers.IPComputerTables;

import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityPumpjack;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PumpjackDriver extends ICTableDriver<TileEntityPumpjack> {
    public PumpjackDriver() { super(IPComputerTables.PUMPJACK, PumpjackEnvironment::new); }

    @SuppressWarnings("unused") public static class PumpjackEnvironment extends ICTableEnvironment<TileEntityPumpjack> {
        public PumpjackEnvironment(World world, BlockPos pos) { super(world, pos, IPComputerTables.PUMPJACK); }

        @Callback(doc = ICCallbackDocs.ENERGY_STORED) public Object[] getEnergyStored(Context context, Arguments args) { return call("getEnergyStored", args); }

        @Callback(doc = ICCallbackDocs.MAX_ENERGY_STORED) public Object[] getMaxEnergyStored(Context context, Arguments args) { return call("getMaxEnergyStored", args); }

        @Callback(doc = "function():boolean -- checks whether the pumpjack is currently pumping") public Object[] isActive(Context context, Arguments args) { return call("isActive", args); }

        @Callback(doc = "function():string -- returns the name of the fluid in the reservoir below, or nil") public Object[] getReservoirFluid(Context context, Arguments args) { return call("getReservoirFluid", args); }

        @Callback(doc = "function():int -- returns the amount of fluid left in the reservoir below") public Object[] getReservoirAmount(Context context, Arguments args) { return call("getReservoirAmount", args); }

        @Callback(doc = "function():int -- returns the replenish rate of the reservoir below") public Object[] getReservoirReplenish(Context context, Arguments args) { return call("getReservoirReplenish", args); }

        @Callback(doc = ICCallbackDocs.ENABLE_COMPUTER_CONTROL) public Object[] enableComputerControl(Context context, Arguments args) { return call("enableComputerControl", args); }

        @Callback(doc = ICCallbackDocs.SET_ENABLED) public Object[] setEnabled(Context context, Arguments args) { return call("setEnabled", args); }
    }
}
