package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.compat.opencomputers.ICTableDriver;
import com.immersiveconvergence.api.compat.opencomputers.ICTableEnvironment;
import com.immersiveconvergence.common.util.compat.computers.IEMachineComputerTables;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySilo;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SiloDriver extends ICTableDriver<TileEntitySilo> {
    public SiloDriver() { super(IEMachineComputerTables.SILO, SiloEnvironment::new); }

    @SuppressWarnings("unused") public static class SiloEnvironment extends ICTableEnvironment<TileEntitySilo> {
        public SiloEnvironment(World world, BlockPos pos) { super(world, pos, IEMachineComputerTables.SILO); }

        @Callback(doc = "function():table -- returns the stored item and its amount") public Object[] getContents(Context context, Arguments args) { return call("getContents", args); }
    }
}
