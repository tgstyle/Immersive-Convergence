package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityLightningrod;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityLightningrod.class)
public abstract class MixinIETileEntityLightningrod {
    @Inject(method = "getOriginalBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectGetOriginalBlock(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = IEMultiblockRegistry.getOriginalBlock("IE:Lightningrod", ((TileEntityMultiblockPart<?>)(Object)this).pos);
        if (stack != null) { cir.setReturnValue(stack); }
    }
}
