package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.ComputerValues;
import com.immersiveconvergence.api.util.TankInfoCallbacks;

import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.MBEnergyCallbacks;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.distillation_tower.DistillationTowerLogic.Inventory;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.distillation_tower.DistillationTowerLogic.State;
import flaxbeard.immersivepetroleum.common.util.inventory.MultiFluidTankFiltered;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@SuppressWarnings("unused") public class DistillationTowerCallbacks extends Callback<State> {
    public DistillationTowerCallbacks() {
        addAdditional(MBEnergyCallbacks.INSTANCE, state -> state.energy);
        addAdditional(new TankInfoCallbacks<>(state -> state.tanks.input(), "input"));
    }

    @ComputerCallable public boolean isActive(CallbackEnvironment<State> env) { return env.object().wasActive; }

    @ComputerCallable public List<Map<String, Object>> getOutputTanks(CallbackEnvironment<State> env) {
        MultiFluidTankFiltered output = env.object().tanks.output();
        return ComputerValues.fluids(IntStream.range(0, output.getTanks()).mapToObj(output::getFluidInTank).toList());
    }

    @ComputerCallable public Map<String, Object> getEmptyCannisters(CallbackEnvironment<State> env) { return cannisters(env.object(), Inventory.INPUT_EMPTY, Inventory.OUTPUT_EMPTY); }

    @ComputerCallable public Map<String, Object> getFilledCannisters(CallbackEnvironment<State> env) { return cannisters(env.object(), Inventory.INPUT_FILLED, Inventory.OUTPUT_FILLED); }

    private static Map<String, Object> cannisters(State state, Inventory inputSlot, Inventory outputSlot) {
        Map<String, ItemStack> stacks = new LinkedHashMap<>();
        stacks.put("input", state.inventory.get(inputSlot));
        stacks.put("output", state.inventory.get(outputSlot));
        return ComputerValues.items(stacks);
    }
}
