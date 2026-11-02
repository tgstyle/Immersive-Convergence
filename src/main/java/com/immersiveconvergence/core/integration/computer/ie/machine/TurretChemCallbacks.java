package com.immersiveconvergence.core.integration.computer.ie.machine;

import blusunrize.immersiveengineering.common.blocks.metal.TurretChemBlockEntity;

public class TurretChemCallbacks extends TurretCallbacks<TurretChemBlockEntity> {
    public TurretChemCallbacks() { addAdditional(new TankInfoCallbacks<>(turret -> turret.tank, "")); }
}
