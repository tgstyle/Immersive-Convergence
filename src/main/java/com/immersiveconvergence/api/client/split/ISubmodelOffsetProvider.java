package com.immersiveconvergence.api.client.split;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

public interface ISubmodelOffsetProvider {
    BlockPos getModelOffset(BlockState state, Vec3i size);
}
