package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.compat.opencomputers.ICTableDriver;
import com.immersiveconvergence.api.compat.opencomputers.ICTableEnvironment;
import com.immersiveconvergence.common.util.compat.computers.IEDeviceComputerTables;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBlastFurnacePreheater;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PreheaterDriver extends ICTableDriver<TileEntityBlastFurnacePreheater> {
    public PreheaterDriver() { super(IEDeviceComputerTables.PREHEATER, PreheaterEnvironment::new); }

    @SuppressWarnings("unused") public static class PreheaterEnvironment extends ICTableEnvironment<TileEntityBlastFurnacePreheater> {
        public PreheaterEnvironment(World world, BlockPos pos) { super(world, pos, IEDeviceComputerTables.PREHEATER); }

        @Callback(doc = ICCallbackDocs.ENERGY_STORED) public Object[] getEnergyStored(Context context, Arguments args) { return call("getEnergyStored", args); }

        @Callback(doc = ICCallbackDocs.MAX_ENERGY_STORED) public Object[] getMaxEnergyStored(Context context, Arguments args) { return call("getMaxEnergyStored", args); }

        @Callback(doc = "function():boolean -- checks whether the preheater is heating") public Object[] isActive(Context context, Arguments args) { return call("isActive", args); }
    }
}
