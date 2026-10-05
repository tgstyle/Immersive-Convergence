package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.ComputerValues;
import com.immersiveconvergence.api.util.TankInfoCallbacks;

import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.MBEnergyCallbacks;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.distillation_tower.DistillationTowerLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.distillation_tower.DistillationTowerLogic.State;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused") public class DistillationTowerCallbacks extends Callback<State> {
    public DistillationTowerCallbacks() {
        addAdditional(MBEnergyCallbacks.INSTANCE, state -> state.energy);
        addAdditional(new TankInfoCallbacks<>(state -> state.tanks.input(), "input"));
    }

    @ComputerCallable public boolean isActive(CallbackEnvironment<State> env) { return env.object().wasActive; }

    @ComputerCallable public List<Map<String, Object>> getOutputTanks(CallbackEnvironment<State> env) { return ComputerValues.fluids(env.object().tanks.output().fluids); }

    @ComputerCallable public Map<String, Object> getEmptyCannisters(CallbackEnvironment<State> env) { return cannisters(env.object(), DistillationTowerLogic.INV_1, DistillationTowerLogic.INV_2); }

    @ComputerCallable public Map<String, Object> getFilledCannisters(CallbackEnvironment<State> env) { return cannisters(env.object(), DistillationTowerLogic.INV_0, DistillationTowerLogic.INV_3); }

    private static Map<String, Object> cannisters(State state, int inputSlot, int outputSlot) {
        Map<String, ItemStack> stacks = new LinkedHashMap<>();
        stacks.put("input", state.inventory.get(inputSlot));
        stacks.put("output", state.inventory.get(outputSlot));
        return ComputerValues.items(stacks);
    }
}
