package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBottlingMachine;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityRefinery;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySheetmetalTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({TileEntityBottlingMachine.class, TileEntityRefinery.class, TileEntitySheetmetalTank.class})
public abstract class MixinIEPortFillTank {
    @Redirect(method = "canFillTankFrom", at = @At(value = "FIELD", target = "Lblusunrize/immersiveengineering/common/blocks/TileEntityMultiblockPart;pos:I"), require = 0, remap = false)
    private int redirectPosDev(TileEntityMultiblockPart<?> part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "canFillTankFrom", at = @At(value = "FIELD", target = "Lblusunrize/immersiveengineering/common/blocks/TileEntityMultiblockPart;field_174879_c:I"), require = 0, remap = false)
    private int redirectPosProduction(TileEntityMultiblockPart<?> part) { return IEMultiblockRegistry.portPos(part); }
}
