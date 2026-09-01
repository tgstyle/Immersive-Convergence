package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblock;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAssembler;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBottlingMachine;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityDieselGenerator;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFermenter;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMixer;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityRefinery;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySheetmetalTank;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySqueezer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({TileEntityAssembler.class, TileEntityBottlingMachine.class, TileEntityDieselGenerator.class, TileEntityFermenter.class, TileEntityMixer.class, TileEntityRefinery.class, TileEntitySheetmetalTank.class, TileEntitySqueezer.class})
public abstract class MixinIEPortFluidTanks {
    @Redirect(method = "getAccessibleFluidTanks", at = @At(value = "FIELD", target = "Lblusunrize/immersiveengineering/common/blocks/TileEntityMultiblockPart;pos:I"), require = 0, remap = false)
    private int redirectPosDev(TileEntityMultiblockPart<?> part) { return MixinIEPortFluidTanks.immersiveconvergence$portPos(part); }

    @Redirect(method = "getAccessibleFluidTanks", at = @At(value = "FIELD", target = "Lblusunrize/immersiveengineering/common/blocks/TileEntityMultiblockPart;field_174879_c:I"), require = 0, remap = false)
    private int redirectPosProduction(TileEntityMultiblockPart<?> part) { return MixinIEPortFluidTanks.immersiveconvergence$portPos(part); }

    private static int immersiveconvergence$portPos(TileEntityMultiblockPart<?> part) {
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        return template == null ? part.pos : template.portPos(part.pos);
    }
}
