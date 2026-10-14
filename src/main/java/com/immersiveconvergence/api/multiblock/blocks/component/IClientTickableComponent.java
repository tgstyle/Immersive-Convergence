package com.immersiveconvergence.api.multiblock.blocks.component;

import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;

public interface IClientTickableComponent<State> extends IMultiblockComponent<State> {
    void tickClient(IMultiblockContext<State> context);
}
