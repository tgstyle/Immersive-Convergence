package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySilo;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntitySilo.class)
public abstract class MixinIETileEntitySilo {
    @Inject(method = "getOriginalBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectGetOriginalBlock(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = IEMultiblockRegistry.getOriginalBlock("IE:Silo", ((TileEntityMultiblockPart<?>)(Object)this).pos);
        if (stack != null) { cir.setReturnValue(stack); }
    }
}
