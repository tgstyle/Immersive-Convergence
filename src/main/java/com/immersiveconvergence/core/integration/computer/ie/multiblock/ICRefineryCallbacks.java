package com.immersiveconvergence.core.integration.computer.ie.multiblock;

import com.immersiveconvergence.api.util.ComputerValues;
import com.immersiveconvergence.core.integration.computer.IEComputerValues;
import com.immersiveconvergence.core.integration.computer.alias.TankInfoAlias;
import com.immersiveconvergence.mixin.accessor.immersiveengineering.RefineryLogicStateAccessor;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.RefineryRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.RefineryLogic.State;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.owners.RefineryCallbacks;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraftforge.fluids.FluidStack;

public class ICRefineryCallbacks extends RefineryCallbacks {
    private static final int EMPTY_CONTAINER_SLOT = 1;
    private static final int FILLED_CONTAINER_SLOT = 2;

    public ICRefineryCallbacks() { addAdditional(new TankInfoAlias<>(state -> state.tanks.output(), "getOutputTank")); }

    @ComputerCallable public boolean isActive(CallbackEnvironment<State> env) { return isRunning(env); }

    @ComputerCallable public Map<String, Object> getInputFluidTanks(CallbackEnvironment<State> env) {
        Map<String, Object> result = new HashMap<>();
        result.put("input1", ComputerValues.tankInfo(env.object().tanks.leftInput()));
        result.put("input2", ComputerValues.tankInfo(env.object().tanks.rightInput()));
        return result;
    }

    @ComputerCallable public Map<String, Object> getEmptyCannisters(CallbackEnvironment<State> env) { return Map.of("output", IEComputerValues.item(env.object().getInventory().getStackInSlot(EMPTY_CONTAINER_SLOT))); }

    @ComputerCallable public Map<String, Object> getFullCannisters(CallbackEnvironment<State> env) { return Map.of("output", IEComputerValues.item(env.object().getInventory().getStackInSlot(FILLED_CONTAINER_SLOT))); }

    @ComputerCallable public Map<String, Object> getRecipe(CallbackEnvironment<State> env) {
        RefineryRecipe recipe = recipe(env);
        if (recipe == null) { throw new IllegalArgumentException("The recipe of the refinery is invalid"); }
        Map<String, Object> result = new HashMap<>();
        putInput(result, "input1", recipe.input0);
        putInput(result, "input2", recipe.input1);
        result.put("output", IEComputerValues.fluid(recipe.output));
        return result;
    }

    @ComputerCallable public boolean isValidRecipe(CallbackEnvironment<State> env) { return recipe(env) != null; }

    @Nullable private static RefineryRecipe recipe(CallbackEnvironment<State> env) { return IEComputerValues.firstRecipe(((RefineryLogicStateAccessor) env.object()).ic$getProcessor().getQueue(), env.level()); }

    private static void putInput(Map<String, Object> result, String key, @Nullable FluidTagInput input) {
        if (input == null) { return; }
        List<FluidStack> matching = input.getMatchingFluidStacks();
        if (!matching.isEmpty()) { result.put(key, IEComputerValues.fluid(matching.get(0))); }
    }
}
