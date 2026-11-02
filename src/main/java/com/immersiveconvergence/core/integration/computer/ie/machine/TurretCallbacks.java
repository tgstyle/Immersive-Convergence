package com.immersiveconvergence.core.integration.computer.ie.machine;

import com.immersiveconvergence.mixin.accessor.immersiveengineering.TurretBlockEntityAccessor;

import blusunrize.immersiveengineering.common.blocks.metal.TurretBlockEntity;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.MBEnergyCallbacks;

public class TurretCallbacks<T extends TurretBlockEntity<T>> extends Callback<T> {
    public TurretCallbacks() { addAdditional(MBEnergyCallbacks.INSTANCE, turret -> turret.energyStorage); }

    @ComputerCallable public boolean isActive(CallbackEnvironment<T> env) { return ((TurretBlockEntityAccessor) env.object()).ic$getTarget() != null; }
}
