package com.immersiveconvergence.api.wires.fittings;

import net.minecraft.util.ITickable;

public abstract class ICTileEntityEnergyFitting extends ICTileEntityFitting implements ITickable {
    protected ICTileEntityEnergyFitting(ICFittingTier tier) { super(tier, false); }

    @Override public void update() { offerBuffer(); }
}
