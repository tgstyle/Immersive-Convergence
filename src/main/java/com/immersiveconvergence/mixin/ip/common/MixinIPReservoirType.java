package com.immersiveconvergence.mixin.ip.common;

import com.immersiveconvergence.api.petroleum.ICReservoirData;
import com.immersiveconvergence.api.petroleum.ICReservoirHolder;
import com.immersiveconvergence.api.petroleum.ICReservoirNBT;

import flaxbeard.immersivepetroleum.api.crafting.PumpjackHandler;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PumpjackHandler.ReservoirType.class, remap = false)
public abstract class MixinIPReservoirType implements ICReservoirHolder {
    @Unique private final ICReservoirData immersiveconvergence$data = new ICReservoirData();

    @Override public ICReservoirData immersiveconvergence$data() { return immersiveconvergence$data; }

    @Inject(method = "writeToNBT", at = @At("RETURN"), remap = false)
    private void immersiveconvergence$writeExtras(CallbackInfoReturnable<NBTTagCompound> cir) { ICReservoirNBT.write(cir.getReturnValue(), immersiveconvergence$data); }

    @Inject(method = "readFromNBT", at = @At("RETURN"), remap = false)
    private static void immersiveconvergence$readExtras(NBTTagCompound tag, CallbackInfoReturnable<PumpjackHandler.ReservoirType> cir) {
        ICReservoirData data = ICReservoirHolder.of(cir.getReturnValue());
        if (data != null) { ICReservoirNBT.read(tag, data); }
    }
}
