package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;

public interface IDataReloadAware {
    void onDataReload(IInitialMultiblockContext<?> context);
}
