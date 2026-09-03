package com.immersiveconvergence.api.client.split;

import net.minecraft.util.math.BlockPos;
import javax.annotation.Nullable;

public interface ISubmodelOffsetProvider {
    @Nullable BlockPos getModelOffset();
}
