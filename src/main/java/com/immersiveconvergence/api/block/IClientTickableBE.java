package com.immersiveconvergence.api.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

@SuppressWarnings({"unused", "RedundantSuppression"}) public interface IClientTickableBE extends ITickableBase {
    void tickClient();

    static <T extends BlockEntity> BlockEntityTicker<T> makeTicker() { return (level, pos, state, blockEntity) -> { IClientTickableBE tickable = (IClientTickableBE)blockEntity; if (tickable.canTickAny()) { tickable.tickClient(); } }; }
}
