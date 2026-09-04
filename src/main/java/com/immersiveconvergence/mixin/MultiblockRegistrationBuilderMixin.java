package com.immersiveconvergence.mixin;

import com.immersiveconvergence.api.multiblock.MultiblockOverride;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistrationBuilder;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent.CapabilityRegistrar;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = MultiblockRegistrationBuilder.class, remap = false)
public abstract class MultiblockRegistrationBuilderMixin {
    @Shadow(remap = false) @Final private ResourceLocation name;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @ModifyArg(method = "registerCapabilities", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/multiblocks/blocks/logic/IMultiblockLogic;registerCapabilities(Lblusunrize/immersiveengineering/api/multiblocks/blocks/component/IMultiblockComponent$CapabilityRegistrar;)V"), remap = false)
    private CapabilityRegistrar ic$overridePorts(CapabilityRegistrar registrar) {
        MultiblockOverride override = MultiblockOverride.get(name);
        if (override == null || !override.hasPorts()) { return registrar; }
        return override.wrap(registrar);
    }
}
