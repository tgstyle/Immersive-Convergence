package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.ComputerValues;

import blusunrize.immersiveengineering.api.crafting.SqueezerRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.SqueezerLogic.State;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.IndexArgument;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.SqueezerCallbacks;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused") public class ICSqueezerCallbacks extends SqueezerCallbacks {
    @ComputerCallable public Object[] getRecipe(CallbackEnvironment<State> env, @IndexArgument int slot) {
        ItemStack input = ComputerValues.slot(env.object().getInventory(), slot, 8, "Input slots are 1-8");
        SqueezerRecipe recipe = SqueezerRecipe.findRecipe(env.level(), input);
        return recipe == null ? new Object[0] : ComputerValues.recipe(input, recipe.itemOutput.get(), recipe.fluidOutput, recipe.getTotalProcessTime());
    }
}
