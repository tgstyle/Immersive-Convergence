package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblock;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityRefinery;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySheetmetalTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({TileEntityRefinery.class, TileEntitySheetmetalTank.class})
public abstract class MixinIEPortDrainTank {
    @Redirect(method = "canDrainTankFrom", at = @At(value = "FIELD", target = "Lblusunrize/immersiveengineering/common/blocks/TileEntityMultiblockPart;pos:I"), require = 0, remap = false)
    private int redirectPosDev(TileEntityMultiblockPart<?> part) { return MixinIEPortDrainTank.immersiveconvergence$portPos(part); }

    @Redirect(method = "canDrainTankFrom", at = @At(value = "FIELD", target = "Lblusunrize/immersiveengineering/common/blocks/TileEntityMultiblockPart;field_174879_c:I"), require = 0, remap = false)
    private int redirectPosProduction(TileEntityMultiblockPart<?> part) { return MixinIEPortDrainTank.immersiveconvergence$portPos(part); }

    private static int immersiveconvergence$portPos(TileEntityMultiblockPart<?> part) {
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        return template == null ? part.pos : template.portPos(part.pos);
    }
}
