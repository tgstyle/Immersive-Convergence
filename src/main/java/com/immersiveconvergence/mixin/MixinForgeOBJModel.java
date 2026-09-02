package com.immersiveconvergence.mixin;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(value = OBJModel.class, remap = false)
public abstract class MixinForgeOBJModel {

    @Inject(method = "getTextures", at = @At("RETURN"), cancellable = true)
    private void injectGetTextures(CallbackInfoReturnable<Collection<ResourceLocation>> cir) {
        Collection<ResourceLocation> textures = cir.getReturnValue();
        Collection<ResourceLocation> filtered = new ArrayList<>(textures.size());
        for (ResourceLocation location : textures) {
            if (!location.getPath().startsWith("#")) { filtered.add(location); }
        }
        if (filtered.size() != textures.size()) { cir.setReturnValue(filtered); }
    }
}
