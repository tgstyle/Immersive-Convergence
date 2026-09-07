package com.immersiveconvergence.mixin.ip.common;

import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler;
import flaxbeard.immersivepetroleum.api.crafting.PumpjackHandler;
import flaxbeard.immersivepetroleum.common.IPSaveData;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IPSaveData.class)
public abstract class MixinIPSaveData {
    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void injectClearStaleCaches(String s, CallbackInfo ci) {
        PumpjackHandler.oilCache.clear();
        PumpjackHandler.timeCache.clear();
        LubricatedHandler.lubricatedTiles.clear();
    }

    @Inject(method = "readFromNBT", at = @At("RETURN"))
    private void injectDropExpiredLubrication(NBTTagCompound nbt, CallbackInfo ci) {
        LubricatedHandler.lubricatedTiles.removeIf(info -> info.ticks <= 0);
    }
}
