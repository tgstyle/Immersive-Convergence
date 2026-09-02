package com.immersiveconvergence.api.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public interface IStateSideConfig {
    Map<Direction, Enums.IOSideConfig> getStateSideConfig(BlockState state);
}
