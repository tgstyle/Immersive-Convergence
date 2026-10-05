package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.compat.opencomputers.ICTableDriver;
import com.immersiveconvergence.api.compat.opencomputers.ICTableEnvironment;
import com.immersiveconvergence.common.util.compat.computers.IEDeviceComputerTables;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityTurretGun;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GunTurretDriver extends ICTableDriver<TileEntityTurretGun> {
    public GunTurretDriver() { super(IEDeviceComputerTables.TURRET_GUN, GunTurretEnvironment::new); }

    @SuppressWarnings("unused") public static class GunTurretEnvironment extends ICTableEnvironment<TileEntityTurretGun> {
        public GunTurretEnvironment(World world, BlockPos pos) { super(world, pos, IEDeviceComputerTables.TURRET_GUN); }

        @Callback(doc = ICCallbackDocs.ENERGY_STORED) public Object[] getEnergyStored(Context context, Arguments args) { return call("getEnergyStored", args); }

        @Callback(doc = ICCallbackDocs.MAX_ENERGY_STORED) public Object[] getMaxEnergyStored(Context context, Arguments args) { return call("getMaxEnergyStored", args); }

        @Callback(doc = "function():boolean -- checks whether the turret has a target") public Object[] isActive(Context context, Arguments args) { return call("isActive", args); }

        @Callback(doc = "function():table -- returns the stack in the ammunition slot") public Object[] getAmmoStack(Context context, Arguments args) { return call("getAmmoStack", args); }

        @Callback(doc = "function():table -- returns the stack in the casing slot") public Object[] getCasingStack(Context context, Arguments args) { return call("getCasingStack", args); }
    }
}
