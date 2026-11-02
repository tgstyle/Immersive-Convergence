package com.immersiveconvergence.core.compat.computer.alias;

import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import java.util.function.Function;

public class OutputCanisterAliases<T> extends Callback<T> {
    private final Function<T, IItemHandler> inventory;
    private final int output;
    private final int empty;
    private final int filled;

    public OutputCanisterAliases(Function<T, IItemHandler> inventory, int output, int empty, int filled) {
        this.inventory = inventory;
        this.output = output;
        this.empty = empty;
        this.filled = filled;
    }

    @ComputerCallable public ItemStack getOutputStack(CallbackEnvironment<T> env) { return inventory.apply(env.object()).getStackInSlot(output); }

    @ComputerCallable public ItemStack getEmptyCannisters(CallbackEnvironment<T> env) { return inventory.apply(env.object()).getStackInSlot(empty); }

    @ComputerCallable public ItemStack getFilledCannisters(CallbackEnvironment<T> env) { return inventory.apply(env.object()).getStackInSlot(filled); }
}
