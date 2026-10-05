package com.immersiveconvergence.mixin.ie.common;

import com.immersiveconvergence.api.compat.ICComputerControl;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMultiblockMetal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityMultiblockMetal.class)
public abstract class MixinIEComputerOn {
    @Redirect(method = "readCustomNBT", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/Loader;isModLoaded(Ljava/lang/String;)Z", remap = false), remap = false)
    private boolean redirectComputerModLoaded(String modname) { return ICComputerControl.computerModLoaded(); }
}
