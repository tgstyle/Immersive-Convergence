package com.immersiveconvergence.core.compat.computer;

import blusunrize.immersiveengineering.common.blocks.metal.BlastFurnacePreheaterBlockEntity;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.MBEnergyCallbacks;

@SuppressWarnings("unused") public class PreheaterCallbacks extends Callback<BlastFurnacePreheaterBlockEntity> {
    public PreheaterCallbacks() { addAdditional(MBEnergyCallbacks.INSTANCE, preheater -> preheater.energyStorage); }

    @ComputerCallable public boolean isActive(CallbackEnvironment<BlastFurnacePreheaterBlockEntity> env) { return env.object().active; }
}
