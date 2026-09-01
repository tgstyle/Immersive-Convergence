package com.immersiveconvergence.api.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

@SuppressWarnings({"unused", "RedundantSuppression"}) public interface IServerTickableBE extends ITickableBase
{
    void tickServer();

    static <T extends BlockEntity>BlockEntityTicker<T> makeTicker() {
        return (level, pos, state, blockEntity) -> {
            IServerTickableBE tickable = (IServerTickableBE) blockEntity;
            if (tickable.canTickAny())
                tickable.tickServer();
        };
    }
}
