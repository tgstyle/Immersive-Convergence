package com.immersiveconvergence.core.integration.computer.alias;

import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.IndexArgument;
import net.minecraft.world.item.ItemStack;
import java.util.function.BiFunction;

public class IndexedStackAlias<T> extends Callback<T> {
    private final BiFunction<T, Integer, ItemStack> stack;
    private final int count;
    private final String error;
    private final String name;

    public IndexedStackAlias(BiFunction<T, Integer, ItemStack> stack, int count, String error, String name) {
        this.stack = stack;
        this.count = count;
        this.error = error;
        this.name = name;
    }

    @Override public String renameMethod(String javaName) { return name; }

    @ComputerCallable public ItemStack stack(CallbackEnvironment<T> env, @IndexArgument int index) {
        if (index < 0 || index >= count) { throw new IllegalArgumentException(error); }
        return stack.apply(env.object(), index);
    }
}
