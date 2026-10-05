package com.immersiveconvergence.common.util.compat.opencomputers;

import com.immersiveconvergence.api.compat.opencomputers.ICTableDriver;
import com.immersiveconvergence.api.compat.opencomputers.ICTableEnvironment;
import com.immersiveconvergence.common.util.compat.computers.IEMachineComputerTables;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAutoWorkbench;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AutoWorkbenchDriver extends ICTableDriver<TileEntityAutoWorkbench> {
    public AutoWorkbenchDriver() { super(IEMachineComputerTables.AUTO_WORKBENCH, AutoWorkbenchEnvironment::new); }

    @SuppressWarnings("unused") public static class AutoWorkbenchEnvironment extends ICTableEnvironment<TileEntityAutoWorkbench> {
        public AutoWorkbenchEnvironment(World world, BlockPos pos) { super(world, pos, IEMachineComputerTables.AUTO_WORKBENCH); }

        @Callback(doc = ICCallbackDocs.ENERGY_STORED) public Object[] getEnergyStored(Context context, Arguments args) { return call("getEnergyStored", args); }

        @Callback(doc = ICCallbackDocs.MAX_ENERGY_STORED) public Object[] getMaxEnergyStored(Context context, Arguments args) { return call("getMaxEnergyStored", args); }

        @Callback(doc = "function():boolean -- checks whether the automated workbench is currently running") public Object[] isRunning(Context context, Arguments args) { return call("isRunning", args); }

        @Callback(doc = "function(recipe:int):nil -- selects the recipe in the specified position of the blueprint") public Object[] selectRecipe(Context context, Arguments args) { return call("selectRecipe", args); }

        @Callback(doc = "function():nil -- clears the selected recipe") public Object[] unselectRecipe(Context context, Arguments args) { return call("unselectRecipe", args); }

        @Callback(doc = "function():table -- returns the outputs of the recipes in the blueprint") public Object[] getAvailableRecipes(Context context, Arguments args) { return call("getAvailableRecipes", args); }

        @Callback(doc = "function():int -- returns the position of the selected recipe, or 0 if none is selected") public Object[] getSelectedRecipe(Context context, Arguments args) { return call("getSelectedRecipe", args); }

        @Callback(doc = ICCallbackDocs.ENABLE_COMPUTER_CONTROL) public Object[] enableComputerControl(Context context, Arguments args) { return call("enableComputerControl", args); }

        @Callback(doc = ICCallbackDocs.SET_ENABLED) public Object[] setEnabled(Context context, Arguments args) { return call("setEnabled", args); }
    }
}
