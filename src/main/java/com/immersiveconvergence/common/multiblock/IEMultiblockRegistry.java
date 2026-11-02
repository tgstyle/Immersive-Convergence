package com.immersiveconvergence.common.multiblock;

import com.immersiveconvergence.core.integration.immersiveengineering.IEMultiblock;

public final class IEMultiblockRegistry {
    private IEMultiblockRegistry() { }

    public static IEMultiblock get(String uniqueName) { return com.immersiveconvergence.core.integration.immersiveengineering.IEMultiblockRegistry.get(uniqueName); }
}
