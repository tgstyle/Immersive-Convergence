package com.immersiveconvergence.api.petroleum;

import flaxbeard.immersivepetroleum.api.crafting.PumpjackHandler;

import javax.annotation.Nullable;

public interface ICReservoirHolder {
    ICReservoirData immersiveconvergence$data();

    @Nullable static ICReservoirData of(@Nullable PumpjackHandler.ReservoirType reservoir) {
        return reservoir instanceof ICReservoirHolder ? ((ICReservoirHolder)reservoir).immersiveconvergence$data() : null;
    }
}
