package com.immersiveconvergence.mixin.ip.client;

import com.immersiveconvergence.api.ICMultiblockNames;

import flaxbeard.immersivepetroleum.common.items.ItemProjector;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ItemProjector.class, remap = false)
public abstract class MixinIPProjectorMultiblockNames {
    @Redirect(method = "addInformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/I18n;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", remap = true), remap = false)
    private String immersiveconvergence$resolveMultiblockName(String translateKey, Object[] parameters) {
        return I18n.format(ICMultiblockNames.resolve(translateKey), parameters);
    }
}
