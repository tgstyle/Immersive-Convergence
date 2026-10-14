package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.multiblock.blocks.env.IInitialMultiblockContext;

public interface IDataReloadAware {
    void onDataReload(IInitialMultiblockContext<?> context);
}
