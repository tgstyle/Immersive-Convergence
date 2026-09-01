package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblock;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.api.MultiblockHandler.IMultiblock;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityArcFurnace;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAssembler;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAutoWorkbench;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBottlingMachine;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityCrusher;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityDieselGenerator;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityExcavator;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFermenter;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMetalPress;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMixer;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityRefinery;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySqueezer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({TileEntityArcFurnace.class, TileEntityAssembler.class, TileEntityAutoWorkbench.class, TileEntityBottlingMachine.class, TileEntityCrusher.class, TileEntityDieselGenerator.class, TileEntityExcavator.class, TileEntityFermenter.class, TileEntityMetalPress.class, TileEntityMixer.class, TileEntityRefinery.class, TileEntitySqueezer.class})
public abstract class MixinIEMultiblockConnections {
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

    private int[] immersiveconvergence$positions(String prefix) {
        IMultiblock instance = ((MixinIETileEntityMultiblockMetal)(Object)this).getMultiblockInstance();
        if (instance == null) { return null; }
        IEMultiblock template = IEMultiblockRegistry.get(instance.getUniqueName());
        return template == null ? null : template.positionsNamed(prefix);
    }
}
