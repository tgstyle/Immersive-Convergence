package com.immersiveconvergence.core.compat.computer;

import blusunrize.immersiveengineering.common.blocks.metal.TurretGunBlockEntity;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.impl.SingleItemCallback;

public class TurretGunCallbacks extends TurretCallbacks<TurretGunBlockEntity> {
    public TurretGunCallbacks() {
        addAdditional(new SingleItemCallback<>(TurretGunBlockEntity::getInventory, 0, "ammo stack"));
        addAdditional(new SingleItemCallback<>(TurretGunBlockEntity::getInventory, 1, "casing stack"));
    }
}
