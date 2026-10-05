package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.ComputerValues;
import com.immersiveconvergence.mixin.RefineryStateAccessor;

import blusunrize.immersiveengineering.api.crafting.RefineryRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.RefineryLogic.State;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.RefineryCallbacks;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused") public class ICRefineryCallbacks extends RefineryCallbacks {
    @ComputerCallable public Map<String, Object> getRecipe(CallbackEnvironment<State> env) {
        RefineryRecipe recipe = recipe(env);
        if (recipe == null) { throw new IllegalArgumentException("The recipe of the refinery is invalid"); }
        Map<String, Object> result = new HashMap<>();
        putInput(result, "input1", recipe.input0);
        putInput(result, "input2", recipe.input1);
        result.put("output", ComputerValues.fluid(recipe.output));
        return result;
    }

    @ComputerCallable public boolean isValidRecipe(CallbackEnvironment<State> env) { return recipe(env) != null; }

    @Nullable private static RefineryRecipe recipe(CallbackEnvironment<State> env) { return ComputerValues.firstRecipe(((RefineryStateAccessor) env.object()).ic$getProcessor().getQueue(), env.level()); }

    private static void putInput(Map<String, Object> result, String key, @Nullable SizedFluidIngredient input) {
        if (input == null) { return; }
        FluidStack[] matching = input.getFluids();
        if (matching.length > 0) { result.put(key, ComputerValues.fluid(matching[0])); }
    }
}
