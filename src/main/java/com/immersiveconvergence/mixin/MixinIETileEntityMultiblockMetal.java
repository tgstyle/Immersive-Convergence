package com.immersiveconvergence.mixin;

import blusunrize.immersiveengineering.api.MultiblockHandler.IMultiblock;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMultiblockMetal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TileEntityMultiblockMetal.class)
public interface MixinIETileEntityMultiblockMetal {
    @Accessor(value = "mutliblockInstance", remap = false)
    IMultiblock getMultiblockInstance();
}
