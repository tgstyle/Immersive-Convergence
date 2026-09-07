package com.immersiveconvergence.mixin.ip.common;

import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LubricatedHandler.class, remap = false)
public abstract class MixinIPLubrication {
    @Inject(method = "lubricateTile(Lnet/minecraft/tileentity/TileEntity;IZI)Z", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectRejectEmptyDuration(TileEntity tile, int ticks, boolean additive, int cap, CallbackInfoReturnable<Boolean> cir) {
        if (ticks <= 0) { cir.setReturnValue(false); }
    }
}
