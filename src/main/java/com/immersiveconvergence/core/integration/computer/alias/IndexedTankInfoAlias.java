package com.immersiveconvergence.core.integration.computer.alias;

import com.immersiveconvergence.api.util.ComputerValues;

import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.IndexArgument;
import net.minecraftforge.fluids.IFluidTank;
import java.util.Map;
import java.util.function.BiFunction;

public class IndexedTankInfoAlias<T> extends Callback<T> {
    private final BiFunction<T, Integer, IFluidTank> tank;
    private final int count;
    private final String name;

    public IndexedTankInfoAlias(BiFunction<T, Integer, IFluidTank> tank, int count, String name) {
        this.tank = tank;
        this.count = count;
        this.name = name;
    }

    @Override public String renameMethod(String javaName) { return name; }

    @ComputerCallable public Map<String, Object> tankInfo(CallbackEnvironment<T> env, @IndexArgument int index) {
        if (index < 0 || index >= count) { throw new IllegalArgumentException("Only tanks 1-" + count + " are available"); }
        return ComputerValues.tankInfo(tank.apply(env.object(), index));
    }
}
