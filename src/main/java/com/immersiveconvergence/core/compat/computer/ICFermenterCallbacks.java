package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.ComputerValues;
import com.immersiveconvergence.core.compat.computer.alias.OutputCanisterAliases;

import blusunrize.immersiveengineering.api.crafting.FermenterRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.FermenterLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.FermenterLogic.State;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.IndexArgument;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.FermenterCallbacks;
import net.minecraft.world.item.ItemStack;

public class ICFermenterCallbacks extends FermenterCallbacks {
    public ICFermenterCallbacks() { addAdditional(new OutputCanisterAliases<>(State::getInventory, FermenterLogic.OUTPUT_SLOT, FermenterLogic.EMPTY_FLUID_SLOT, FermenterLogic.FILLED_FLUID_SLOT)); }

    @ComputerCallable public boolean isActive(CallbackEnvironment<State> env) { return isRunning(env); }

    @ComputerCallable public Object[] getRecipe(CallbackEnvironment<State> env, @IndexArgument int slot) {
        ItemStack input = ComputerValues.slot(env.object().getInventory(), slot, 8, "Input slots are 1-8");
        FermenterRecipe recipe = FermenterRecipe.findRecipe(env.level(), input);
        return recipe == null ? new Object[0] : IEComputerValues.recipe(input, recipe.itemOutput.get(), recipe.fluidOutput, recipe.getTotalProcessTime());
    }
}
