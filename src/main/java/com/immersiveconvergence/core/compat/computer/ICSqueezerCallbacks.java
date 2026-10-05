package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.ComputerValues;

import blusunrize.immersiveengineering.api.crafting.SqueezerRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.SqueezerLogic.State;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.IndexArgument;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.SqueezerCallbacks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

@SuppressWarnings("unused") public class ICSqueezerCallbacks extends SqueezerCallbacks {
    @ComputerCallable public Object[] getRecipe(CallbackEnvironment<State> env, @IndexArgument int slot) {
        ItemStack input = ComputerValues.slot(env.object().getInventory(), slot, 8, "Input slots are 1-8");
        RecipeHolder<SqueezerRecipe> holder = SqueezerRecipe.findRecipe(env.level(), input);
        if (holder == null) { return new Object[0]; }
        SqueezerRecipe recipe = holder.value();
        return ComputerValues.recipe(input, recipe.itemOutput.get(), recipe.fluidOutput, recipe.getTotalProcessTime());
    }
}
