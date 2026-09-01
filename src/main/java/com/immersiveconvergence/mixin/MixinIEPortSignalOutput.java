package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblock;
import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAssembler;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAutoWorkbench;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBottlingMachine;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMetalPress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({TileEntityAssembler.class, TileEntityAutoWorkbench.class, TileEntityBottlingMachine.class, TileEntityMetalPress.class})
public abstract class MixinIEPortSignalOutput {
    @Redirect(method = "sigOutputDirections", at = @At(value = "FIELD", target = "Lblusunrize/immersiveengineering/common/blocks/TileEntityMultiblockPart;pos:I"), require = 0, remap = false)
    private int redirectPosDev(TileEntityMultiblockPart<?> part) { return MixinIEPortSignalOutput.immersiveconvergence$portPos(part); }

    @Redirect(method = "sigOutputDirections", at = @At(value = "FIELD", target = "Lblusunrize/immersiveengineering/common/blocks/TileEntityMultiblockPart;field_174879_c:I"), require = 0, remap = false)
    private int redirectPosProduction(TileEntityMultiblockPart<?> part) { return MixinIEPortSignalOutput.immersiveconvergence$portPos(part); }

    private static int immersiveconvergence$portPos(TileEntityMultiblockPart<?> part) {
        IEMultiblock template = IEMultiblockRegistry.templateFor(part);
        return template == null ? part.pos : template.portPos(part.pos);
    }
}
