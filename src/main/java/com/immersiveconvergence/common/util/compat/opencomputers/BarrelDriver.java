package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.compat.opencomputers.ICTableDriver;
import com.immersiveconvergence.api.compat.opencomputers.ICTableEnvironment;
import com.immersiveconvergence.common.util.compat.computers.IEDeviceComputerTables;

import blusunrize.immersiveengineering.common.blocks.wooden.TileEntityWoodenBarrel;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BarrelDriver extends ICTableDriver<TileEntityWoodenBarrel> {
    public BarrelDriver() { super(IEDeviceComputerTables.BARREL, BarrelEnvironment::new); }

    @SuppressWarnings("unused") public static class BarrelEnvironment extends ICTableEnvironment<TileEntityWoodenBarrel> {
        public BarrelEnvironment(World world, BlockPos pos) { super(world, pos, IEDeviceComputerTables.BARREL); }

        @Callback(doc = ICCallbackDocs.TANK_INFO) public Object[] getTankInfo(Context context, Arguments args) { return call("getTankInfo", args); }
    }
}
