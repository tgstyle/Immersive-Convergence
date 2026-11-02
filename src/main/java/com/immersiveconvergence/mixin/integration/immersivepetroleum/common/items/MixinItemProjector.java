package com.immersiveconvergence.mixin.integration.immersivepetroleum.common.items;

import com.immersiveconvergence.api.ICMultiblockNames;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import flaxbeard.immersivepetroleum.common.items.ItemProjector;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemProjector.class) public abstract class MixinItemProjector {
    @Inject(method = "getSubItems", at = @At("RETURN")) private void immersiveconvergence$dropFeedthrough(CreativeTabs tab, NonNullList<ItemStack> list, CallbackInfo ci) { list.removeIf(stack -> stack.getItem() instanceof ItemProjector && stack.hasTagCompound() && "IE:Feedthrough".equals(ItemNBTHelper.getString(stack, "multiblock"))); }

    @Redirect(method = "addInformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/I18n;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"), require = 1) private String immersiveconvergence$resolveMultiblockName(String translateKey, Object[] parameters) { return I18n.format(ICMultiblockNames.resolve(translateKey), parameters); }
}
