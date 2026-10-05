package com.immersiveconvergence.api.util;

import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import net.minecraftforge.fluids.IFluidTank;

import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unused") public class TankInfoCallbacks<T> extends Callback<T> {
    private final Function<T, IFluidTank> tank;
    private final String desc;

    public TankInfoCallbacks(Function<T, IFluidTank> tank, String desc) {
        this.tank = tank;
        this.desc = desc;
    }

    @Override public String renameMethod(String javaName) { return javaName.replace("Desc", capitalize(desc)); }

    @ComputerCallable public Map<String, Object> getDescTankInfo(CallbackEnvironment<T> env) { return ComputerValues.tankInfo(tank.apply(env.object())); }
}
