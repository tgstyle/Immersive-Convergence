package com.immersiveconvergence.mixin.ip.common;

import com.immersiveconvergence.api.petroleum.ICReservoirData;
import com.immersiveconvergence.api.petroleum.ICReservoirHolder;

import flaxbeard.immersivepetroleum.api.crafting.PumpjackHandler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PumpjackHandler.class, remap = false)
public abstract class MixinIPPumpjackDrain {
    @Inject(method = "depleteFluid", at = @At("HEAD"), cancellable = true)
    private static void immersiveconvergence$rollDrainChance(World world, int chunkX, int chunkZ, int amount, CallbackInfo ci) {
        PumpjackHandler.OilWorldInfo info = PumpjackHandler.getOilWorldInfo(world, chunkX, chunkZ);
        if (info == null || info.getType() == null || info.current == 0) {
            ci.cancel();
            return;
        }
        ICReservoirData data = ICReservoirHolder.of(info.getType());
        if (data != null && data.drainChance < 1F && world.rand.nextFloat() >= data.drainChance) { ci.cancel(); }
    }
}
