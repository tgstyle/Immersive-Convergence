package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.mixin.TurretAccessor;

import blusunrize.immersiveengineering.common.blocks.metal.TurretBlockEntity;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.MBEnergyCallbacks;

@SuppressWarnings("unused") public class TurretCallbacks<T extends TurretBlockEntity<T>> extends Callback<T> {
    public TurretCallbacks() { addAdditional(MBEnergyCallbacks.INSTANCE, turret -> turret.energyStorage); }

    @ComputerCallable public boolean isActive(CallbackEnvironment<T> env) { return ((TurretAccessor) env.object()).ic$getTarget() != null; }
}
