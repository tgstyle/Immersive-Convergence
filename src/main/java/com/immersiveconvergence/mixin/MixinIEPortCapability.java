package com.immersiveconvergence.mixin;

import com.immersiveconvergence.common.multiblock.IEMultiblockRegistry;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityArcFurnace;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAssembler;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityAutoWorkbench;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBottlingMachine;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFermenter;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMetalPress;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMixer;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySilo;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySqueezer;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityBlastFurnaceAdvanced;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({TileEntityArcFurnace.class, TileEntityAssembler.class, TileEntityAutoWorkbench.class, TileEntityBottlingMachine.class, TileEntityFermenter.class, TileEntityMetalPress.class, TileEntityMixer.class, TileEntitySilo.class, TileEntitySqueezer.class, TileEntityBlastFurnaceAdvanced.class})
public abstract class MixinIEPortCapability {
    @Redirect(method = "getCapability", at = @At(value = "FIELD", target = "Lblusunrize/immersiveengineering/common/blocks/TileEntityMultiblockPart;pos:I"), require = 0, remap = false)
    private int redirectPosDev(TileEntityMultiblockPart<?> part) { return IEMultiblockRegistry.portPos(part); }

    @Redirect(method = "getCapability", at = @At(value = "FIELD", target = "Lblusunrize/immersiveengineering/common/blocks/TileEntityMultiblockPart;field_174879_c:I"), require = 0, remap = false)
    private int redirectPosProduction(TileEntityMultiblockPart<?> part) { return IEMultiblockRegistry.portPos(part); }
}
