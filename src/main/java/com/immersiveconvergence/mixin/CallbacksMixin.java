package com.immersiveconvergence.mixin;

import com.immersiveconvergence.api.util.ComputerCallbacks;
import com.immersiveconvergence.core.compat.computer.IEComputerIntegration;
import com.immersiveconvergence.core.compat.computer.IPComputerIntegration;

import blusunrize.immersiveengineering.common.util.compat.computers.generic.CallbackOwner;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.Callbacks;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = Callbacks.class, remap = false)
public class CallbacksMixin {
    @Shadow @Final private static Map<ResourceLocation, CallbackOwner<?>> CALLBACKS;

    @Inject(method = "ensureInitialized", at = @At("TAIL")) private static void ic$registerComputerCallbacks(CallbackInfo ci) {
        IEComputerIntegration.register();
        if (ModList.get().isLoaded(IPComputerIntegration.MODID)) { IPComputerIntegration.register(); }
        CALLBACKS.putAll(ComputerCallbacks.all());
    }
}
