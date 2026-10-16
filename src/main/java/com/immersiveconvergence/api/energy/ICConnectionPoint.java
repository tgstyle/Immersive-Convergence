package com.immersiveconvergence.api.energy;

import net.minecraft.core.BlockPos;
import javax.annotation.Nonnull;

public record ICConnectionPoint(@Nonnull BlockPos position, int index) {}
