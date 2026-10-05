package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.mixin.LightningRodStateAccessor;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.LightningRodLogic.State;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.MBEnergyCallbacks;

public class LightningRodCallbacks extends Callback<State> {
    public LightningRodCallbacks() { addAdditional(MBEnergyCallbacks.INSTANCE, state -> ((LightningRodStateAccessor) state).ic$getEnergy()); }
}
