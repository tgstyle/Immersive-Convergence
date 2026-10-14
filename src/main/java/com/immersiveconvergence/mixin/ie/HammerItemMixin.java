package com.immersiveconvergence.mixin.ie;

import com.immersiveconvergence.api.block.BlockInterfaces;
import com.immersiveconvergence.api.block.HammerUse;

import blusunrize.immersiveengineering.common.items.HammerItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HammerItem.class, remap = false)
public abstract class HammerItemMixin {
    @Inject(method = "onItemUseFirst(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;", at = @At("HEAD"), cancellable = true, remap = false)
    private void ic$hammerICBlock(ItemStack stack, UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        BlockEntity tile = context.getLevel().getBlockEntity(context.getClickedPos());
        if (tile instanceof BlockInterfaces.IConfigurableSides || tile instanceof BlockInterfaces.IDirectionalBE || tile instanceof BlockInterfaces.IHammerInteraction) { cir.setReturnValue(HammerUse.onBlock(context.getLevel(), context.getClickedPos(), context.getClickedFace(), context.getPlayer(), context.getHand(), context.getClickLocation())); }
    }
}
