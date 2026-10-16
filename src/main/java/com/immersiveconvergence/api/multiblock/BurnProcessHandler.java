package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockLevel;

public final class BurnProcessHandler {
    public interface IFurnaceEnvironment {
        double getProcessSpeed(IMultiblockLevel level);
    }
}
