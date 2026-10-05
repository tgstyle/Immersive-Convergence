package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unused") public class CanisterCallbacks<T> extends Callback<T> {
    private final Function<T, IItemHandler> inventory;
    private final Map<String, Integer> fullSlots;
    private final Map<String, Integer> emptySlots;

    public CanisterCallbacks(Function<T, IItemHandler> inventory, Map<String, Integer> fullSlots, Map<String, Integer> emptySlots) {
        this.inventory = inventory;
        this.fullSlots = fullSlots;
        this.emptySlots = emptySlots;
    }

    @ComputerCallable public Map<String, Object> getFullCanisters(CallbackEnvironment<T> env) { return stacks(env, fullSlots); }

    @ComputerCallable public Map<String, Object> getEmptyCanisters(CallbackEnvironment<T> env) { return stacks(env, emptySlots); }

    private Map<String, Object> stacks(CallbackEnvironment<T> env, Map<String, Integer> slots) {
        IItemHandler handler = inventory.apply(env.object());
        Map<String, ItemStack> stacks = new LinkedHashMap<>();
        slots.forEach((key, slot) -> stacks.put(key, handler.getStackInSlot(slot)));
        return ComputerValues.items(stacks);
    }
}
