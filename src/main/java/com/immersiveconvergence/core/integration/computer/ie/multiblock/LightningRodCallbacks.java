package com.immersiveconvergence.core.integration.computer.ie.multiblock;

import com.immersiveconvergence.mixin.accessor.immersiveengineering.LightningRodLogicStateAccessor;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.LightningRodLogic.State;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.MBEnergyCallbacks;

public class LightningRodCallbacks extends Callback<State> {
    public LightningRodCallbacks() { addAdditional(MBEnergyCallbacks.INSTANCE, state -> ((LightningRodLogicStateAccessor) state).ic$getEnergy()); }
}
