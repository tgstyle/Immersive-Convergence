package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblock;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityDistillationTower;
import flaxbeard.immersivepetroleum.common.blocks.metal.TileEntityPumpjack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({TileEntityDistillationTower.class, TileEntityPumpjack.class})
public abstract class MixinIPMultiblockConnections {
    @Inject(method = "getEnergyPos", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectGetEnergyPos(CallbackInfoReturnable<int[]> cir) {
        int[] positions = immersiveconvergence$positions("energy");
        if (positions != null) { cir.setReturnValue(positions); }
    }

    @Inject(method = "getRedstonePos", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectGetRedstonePos(CallbackInfoReturnable<int[]> cir) {
        int[] positions = immersiveconvergence$positions("redstone");
        if (positions != null) { cir.setReturnValue(positions); }
    }

    @Unique private int[] immersiveconvergence$positions(String prefix) {
        IEMultiblock template = IEMultiblockRegistry.templateFor((TileEntityMultiblockPart<?>)(Object)this);
        return template == null ? null : template.positionsNamed(prefix);
    }
}
