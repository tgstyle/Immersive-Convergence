package com.immersiveconvergence.core.integration.computer.ip;

import com.immersiveconvergence.core.integration.computer.ie.machine.TankInfoCallbacks;
import com.immersiveconvergence.mixin.accessor.immersivepetroleum.GasGeneratorTileEntityAccessor;

import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.MBEnergyCallbacks;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.GasGeneratorTileEntity;

public class GasGeneratorCallbacks extends Callback<GasGeneratorTileEntity> {
    public GasGeneratorCallbacks() {
        addAdditional(MBEnergyCallbacks.INSTANCE, generator -> ((GasGeneratorTileEntityAccessor) generator).ic$getEnergyStorage());
        addAdditional(new TankInfoCallbacks<>(generator -> ((GasGeneratorTileEntityAccessor) generator).ic$getTank(), ""));
    }

    @ComputerCallable public boolean isActive(CallbackEnvironment<GasGeneratorTileEntity> env) { return ((GasGeneratorTileEntityAccessor) env.object()).ic$isActive(); }
}
