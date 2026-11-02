package com.immersiveconvergence.core.compat.computer.alias;

import com.immersiveconvergence.api.util.ComputerValues;

import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import net.neoforged.neoforge.fluids.IFluidTank;
import java.util.Map;
import java.util.function.Function;

public class TankInfoAlias<T> extends Callback<T> {
    private final Function<T, IFluidTank> tank;
    private final String name;

    public TankInfoAlias(Function<T, IFluidTank> tank, String name) {
        this.tank = tank;
        this.name = name;
    }

    @Override public String renameMethod(String javaName) { return name; }

    @ComputerCallable public Map<String, Object> tankInfo(CallbackEnvironment<T> env) { return ComputerValues.tankInfo(tank.apply(env.object())); }
}
