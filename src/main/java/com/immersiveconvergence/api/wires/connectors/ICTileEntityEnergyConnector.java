package com.immersiveconvergence.api.wires.connectors;

import net.minecraft.util.ITickable;

public abstract class ICTileEntityEnergyConnector extends ICTileEntityConnector implements ITickable {
    protected ICTileEntityEnergyConnector(ICConnectorTier tier) { super(tier, false); }

    @Override public void update() { offerBuffer(); }
}
