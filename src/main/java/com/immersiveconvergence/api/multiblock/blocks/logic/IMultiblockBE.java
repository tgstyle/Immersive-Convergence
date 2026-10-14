package com.immersiveconvergence.api.multiblock.blocks.logic;

import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelper;

public interface IMultiblockBE<State extends IMultiblockState> {
    IMultiblockBEHelper<State> getHelper();
}
