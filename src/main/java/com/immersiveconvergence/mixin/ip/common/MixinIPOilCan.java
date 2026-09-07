package com.immersiveconvergence.mixin.ip.common;

import flaxbeard.immersivepetroleum.common.items.ItemOilCan;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemOilCan.class)
public abstract class MixinIPOilCan {
    @Inject(method = "hitEntity", at = @At("HEAD"), cancellable = true)
    private void injectAttackerAsPlayer(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, CallbackInfoReturnable<Boolean> cir) {
        if (attacker instanceof EntityPlayer) { ((ItemOilCan)(Object)this).itemInteractionForEntity(stack, (EntityPlayer)attacker, target, EnumHand.MAIN_HAND); }
        cir.setReturnValue(true);
    }

    @Redirect(method = "itemInteractionForEntity", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F", remap = false))
    private float redirectGolemHeal(float healed, float maximum) { return Math.min(healed, maximum); }
}
