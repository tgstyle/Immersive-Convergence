package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;

@SuppressWarnings("unused")
public class MultiblockRegistry {
    public static <T extends MultiblockHandler.IMultiblock> T register(T multiblock) {
        MultiblockHandler.registerMultiblock(multiblock);
        return multiblock;
    }
}
