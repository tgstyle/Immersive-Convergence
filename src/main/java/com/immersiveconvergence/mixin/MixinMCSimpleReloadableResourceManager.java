package com.immersiveconvergence.mixin;

import com.immersiveconvergence.client.OverridesResourcePack;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import java.util.List;

@Mixin(SimpleReloadableResourceManager.class)
public abstract class MixinMCSimpleReloadableResourceManager {
    @ModifyVariable(method = "reloadResources(Ljava/util/List;)V", at = @At("HEAD"), argsOnly = true)
    private List<IResourcePack> immersiveconvergence$insertOverrides(List<IResourcePack> packs) { return OverridesResourcePack.insert(packs); }
}
