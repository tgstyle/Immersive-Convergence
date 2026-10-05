package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.compat.opencomputers.ICTableDriver;
import com.immersiveconvergence.api.compat.opencomputers.ICTableEnvironment;
import com.immersiveconvergence.common.util.compat.computers.IEMachineComputerTables;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMetalPress;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MetalPressDriver extends ICTableDriver<TileEntityMetalPress> {
    public MetalPressDriver() { super(IEMachineComputerTables.METAL_PRESS, MetalPressEnvironment::new); }

    @SuppressWarnings("unused") public static class MetalPressEnvironment extends ICTableEnvironment<TileEntityMetalPress> {
        public MetalPressEnvironment(World world, BlockPos pos) { super(world, pos, IEMachineComputerTables.METAL_PRESS); }

        @Callback(doc = ICCallbackDocs.ENERGY_STORED) public Object[] getEnergyStored(Context context, Arguments args) { return call("getEnergyStored", args); }

        @Callback(doc = ICCallbackDocs.MAX_ENERGY_STORED) public Object[] getMaxEnergyStored(Context context, Arguments args) { return call("getMaxEnergyStored", args); }

        @Callback(doc = "function():boolean -- checks whether the metal press is currently active") public Object[] isActive(Context context, Arguments args) { return call("isActive", args); }

        @Callback(doc = "function():table -- returns the mold in the metal press") public Object[] getMold(Context context, Arguments args) { return call("getMold", args); }

        @Callback(doc = "function():table -- returns the items being pressed and their outputs") public Object[] getInputQueue(Context context, Arguments args) { return call("getInputQueue", args); }

        @Callback(doc = ICCallbackDocs.ENABLE_COMPUTER_CONTROL) public Object[] enableComputerControl(Context context, Arguments args) { return call("enableComputerControl", args); }

        @Callback(doc = ICCallbackDocs.SET_ENABLED) public Object[] setEnabled(Context context, Arguments args) { return call("setEnabled", args); }
    }
}
