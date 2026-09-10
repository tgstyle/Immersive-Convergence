package com.immersiveconvergence.mixin.ie.common;

import com.immersiveconvergence.api.ICJEICatalysts;
import com.immersiveconvergence.common.util.ICLogger;

import blusunrize.immersiveengineering.common.util.compat.jei.IERecipeCategory;
import mezz.jei.api.IModRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = IERecipeCategory.class, remap = false)
public abstract class MixinIERecipeCategory {

    @Shadow public String uniqueName;

    @Inject(method = "addCatalysts(Lmezz/jei/api/IModRegistry;)V", at = @At("HEAD"), cancellable = true)
    private void immersiveconvergence$suppressCatalyst(IModRegistry registry, CallbackInfo ci) {
        if (ICJEICatalysts.suppressed(uniqueName)) {
            ICLogger.info("Suppressed Immersive Engineering's " + uniqueName + " JEI catalyst at another mod's request");
            ci.cancel();
        }
    }
}
