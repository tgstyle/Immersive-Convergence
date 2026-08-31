package com.immersiveconvergence.api.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

@SuppressWarnings("unused")
public interface IRadiationConsumer {
    float getDoseRate();
    BlockPos getRadiationPos();
    BlockPos getPlayerPos();
    ChunkPos getChunkPos();
}