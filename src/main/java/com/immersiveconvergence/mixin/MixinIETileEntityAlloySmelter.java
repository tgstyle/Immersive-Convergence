package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityAlloySmelter;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityAlloySmelter.class)
public abstract class MixinIETileEntityAlloySmelter {
    @Inject(method = "getOriginalBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectGetOriginalBlock(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = IEMultiblockRegistry.getOriginalBlock("IE:AlloySmelter", ((TileEntityMultiblockPart<?>)(Object)this).pos);
        if (stack != null) { cir.setReturnValue(stack); }
    }
}
