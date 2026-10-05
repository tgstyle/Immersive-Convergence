package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.compat.opencomputers.ICTableDriver;
import com.immersiveconvergence.api.compat.opencomputers.ICTableEnvironment;
import com.immersiveconvergence.common.util.compat.computers.IPComputerTables;

import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityAutoLubricator;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AutoLubricatorDriver extends ICTableDriver<TileEntityAutoLubricator> {
    public AutoLubricatorDriver() { super(IPComputerTables.AUTO_LUBRICATOR, AutoLubricatorEnvironment::new); }

    @SuppressWarnings("unused") public static class AutoLubricatorEnvironment extends ICTableEnvironment<TileEntityAutoLubricator> {
        public AutoLubricatorEnvironment(World world, BlockPos pos) { super(world, pos, IPComputerTables.AUTO_LUBRICATOR); }

        @Callback(doc = "function():boolean -- checks whether the lubricator is currently lubricating a machine") public Object[] isActive(Context context, Arguments args) { return call("isActive", args); }

        @Callback(doc = "function():table -- returns information about the lubricant tank") public Object[] getTankInfo(Context context, Arguments args) { return call("getTankInfo", args); }
    }
}
