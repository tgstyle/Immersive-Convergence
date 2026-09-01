package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblock;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityArcFurnace;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAssembler;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAutoWorkbench;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBottlingMachine;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityCrusher;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityDieselGenerator;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityExcavator;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFermenter;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityLightningrod;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMetalPress;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMixer;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityRefinery;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySheetmetalTank;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySilo;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySqueezer;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityBlastFurnaceAdvanced;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({TileEntityArcFurnace.class, TileEntityAssembler.class, TileEntityAutoWorkbench.class, TileEntityBottlingMachine.class, TileEntityCrusher.class, TileEntityDieselGenerator.class, TileEntityExcavator.class, TileEntityFermenter.class, TileEntityLightningrod.class, TileEntityMetalPress.class, TileEntityMixer.class, TileEntityRefinery.class, TileEntitySheetmetalTank.class, TileEntitySilo.class, TileEntitySqueezer.class, TileEntityBlastFurnaceAdvanced.class})
public abstract class MixinIEMultiblockBounds {
    @Inject(method = "getBlockBounds", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectGetBlockBounds(CallbackInfoReturnable<float[]> cir) {
        TileEntityMultiblockPart<?> part = (TileEntityMultiblockPart<?>)(Object)this;
        if (part.pos < 0) { return; }
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        if (template != null) { cir.setReturnValue(template.blockBoundsFor(part.pos, part.facing, part.mirrored)); }
    }
}
