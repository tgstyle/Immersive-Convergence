package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.TankInfoCallbacks;
import com.immersiveconvergence.mixin.ClocheAccessor;

import blusunrize.immersiveengineering.common.blocks.metal.ClocheBlockEntity;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callback;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackEnvironment;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerCallable;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.InventoryCallbacks;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.MBEnergyCallbacks;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.SingleItemCallback;

@SuppressWarnings("unused") public class ClocheCallbacks extends Callback<ClocheBlockEntity> {
    public ClocheCallbacks() {
        addAdditional(MBEnergyCallbacks.INSTANCE, cloche -> cloche.energyStorage);
        addAdditional(new TankInfoCallbacks<>(cloche -> cloche.tank, ""));
        addAdditional(new SingleItemCallback<>(cloche -> ((ClocheAccessor) cloche).ic$getInventory(), ClocheBlockEntity.SLOT_SOIL, "soil stack"));
        addAdditional(new SingleItemCallback<>(cloche -> ((ClocheAccessor) cloche).ic$getInventory(), ClocheBlockEntity.SLOT_SEED, "seed stack"));
        addAdditional(InventoryCallbacks.fromList(cloche -> ((ClocheAccessor) cloche).ic$getInventory(), ClocheBlockEntity.SLOT_FERTILIZER + 1, ClocheBlockEntity.NUM_SLOTS - ClocheBlockEntity.SLOT_FERTILIZER - 1, "output"));
    }

    @ComputerCallable public int getFertilizer(CallbackEnvironment<ClocheBlockEntity> env) { return env.object().fertilizerAmount; }

    @ComputerCallable public float getGrowth(CallbackEnvironment<ClocheBlockEntity> env) { return env.object().getGuiProgress(); }
}
