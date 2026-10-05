package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.ComputerValues;
import com.immersiveconvergence.mixin.MetalPressStateAccessor;

import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.MetalPressLogic.State;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInWorld;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInWorld;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.MBEnergyCallbacks;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.SingleItemCallback;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused") public class MetalPressCallbacks extends Callback<State> {
    public MetalPressCallbacks() {
        addAdditional(MBEnergyCallbacks.INSTANCE, State::getEnergy);
        addAdditional(new SingleItemCallback<>(state -> state.mold, "mold"));
    }

    @ComputerCallable public boolean isActive(CallbackEnvironment<State> env) { return ((MetalPressStateAccessor) env.object()).ic$isRenderAsActive(); }

    @ComputerCallable public List<Map<String, Object>> getInputQueue(CallbackEnvironment<State> env) {
        Level level = env.level();
        List<Map<String, Object>> queue = new ArrayList<>();
        for (MultiblockProcess<MetalPressRecipe, ProcessContextInWorld<MetalPressRecipe>> process : env.object().processor.getQueue()) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("progress", process.processTick);
            entry.put("maxProgress", process.getMaxTicks(level));
            if (process instanceof MultiblockProcessInWorld<MetalPressRecipe> inWorld) { entry.put("input", inWorld.inputItems.stream().map(ComputerValues::item).toList()); }
            MetalPressRecipe recipe = process.getRecipe(level);
            if (recipe != null) { entry.put("output", ComputerValues.item(recipe.output.get())); }
            queue.add(entry);
        }
        return queue;
    }
}
