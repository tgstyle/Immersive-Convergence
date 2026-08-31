package com.immersiveconvergence.api.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

@SuppressWarnings("unused")
public interface IRadiationProvider {
    float getDoseRate();
    BlockPos getRadiationPos();
    ChunkPos getChunkPos();
}
