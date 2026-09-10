package com.immersiveconvergence.mixin.ie.client;

import com.immersiveconvergence.api.ICMultiblockNames;

import blusunrize.immersiveengineering.common.items.ItemIETool;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ItemIETool.class, remap = false)
public abstract class MixinIEToolMultiblockNames {
    @Redirect(method = "addInformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/I18n;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", remap = true), remap = false)
    private String immersiveconvergence$resolveMultiblockName(String translateKey, Object[] parameters) {
        return I18n.format(ICMultiblockNames.resolve(translateKey), parameters);
    }
}
