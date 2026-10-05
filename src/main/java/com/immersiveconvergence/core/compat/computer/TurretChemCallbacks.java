package com.immersiveconvergence.core.compat.computer;

import com.immersiveconvergence.api.util.TankInfoCallbacks;

import blusunrize.immersiveengineering.common.blocks.metal.TurretChemBlockEntity;

public class TurretChemCallbacks extends TurretCallbacks<TurretChemBlockEntity> {
    public TurretChemCallbacks() { addAdditional(new TankInfoCallbacks<>(turret -> turret.tank, "")); }
}
