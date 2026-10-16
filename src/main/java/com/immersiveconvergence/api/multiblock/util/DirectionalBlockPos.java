package com.immersiveconvergence.api.multiblock.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import javax.annotation.Nullable;

public record DirectionalBlockPos(BlockPos position, @Nullable Direction side) {}
