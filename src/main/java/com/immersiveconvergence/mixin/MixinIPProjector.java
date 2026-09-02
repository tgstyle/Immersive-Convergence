package com.immersiveconvergence.mixin;

import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import flaxbeard.immersivepetroleum.common.items.ItemProjector;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemProjector.class)
public abstract class MixinIPProjector {
    @Inject(method = "getSubItems", at = @At("RETURN"), remap = false)
    private void immersiveconvergence$dropFeedthrough(CreativeTabs tab, NonNullList<ItemStack> list, CallbackInfo ci) {
        list.removeIf(stack -> stack.getItem() instanceof ItemProjector && stack.hasTagCompound() && "IE:Feedthrough".equals(ItemNBTHelper.getString(stack, "multiblock")));
    }
}
