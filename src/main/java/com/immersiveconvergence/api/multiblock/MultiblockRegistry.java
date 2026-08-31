package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.MultiblockHandler;

@SuppressWarnings("unused")
public class MultiblockRegistry {
    public static void register(TemplateMultiblock multiblock) { MultiblockHandler.registerMultiblock(multiblock); }
}
