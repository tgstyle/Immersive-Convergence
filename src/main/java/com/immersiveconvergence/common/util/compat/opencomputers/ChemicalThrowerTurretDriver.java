package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.compat.opencomputers.ICTableDriver;
import com.immersiveconvergence.api.compat.opencomputers.ICTableEnvironment;
import com.immersiveconvergence.common.util.compat.computers.IEDeviceComputerTables;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityTurretChem;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChemicalThrowerTurretDriver extends ICTableDriver<TileEntityTurretChem> {
    public ChemicalThrowerTurretDriver() { super(IEDeviceComputerTables.TURRET_CHEM, ChemicalThrowerTurretEnvironment::new); }

    @SuppressWarnings("unused") public static class ChemicalThrowerTurretEnvironment extends ICTableEnvironment<TileEntityTurretChem> {
        public ChemicalThrowerTurretEnvironment(World world, BlockPos pos) { super(world, pos, IEDeviceComputerTables.TURRET_CHEM); }

        @Callback(doc = ICCallbackDocs.ENERGY_STORED) public Object[] getEnergyStored(Context context, Arguments args) { return call("getEnergyStored", args); }

        @Callback(doc = ICCallbackDocs.MAX_ENERGY_STORED) public Object[] getMaxEnergyStored(Context context, Arguments args) { return call("getMaxEnergyStored", args); }

        @Callback(doc = "function():boolean -- checks whether the turret has a target") public Object[] isActive(Context context, Arguments args) { return call("isActive", args); }

        @Callback(doc = ICCallbackDocs.TANK_INFO) public Object[] getTankInfo(Context context, Arguments args) { return call("getTankInfo", args); }
    }
}
