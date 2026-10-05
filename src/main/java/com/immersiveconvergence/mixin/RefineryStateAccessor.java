package com.immersiveconvergence.mixin;

import blusunrize.immersiveengineering.api.crafting.RefineryRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.RefineryLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor.InMachineProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RefineryLogic.State.class, remap = false)
public interface RefineryStateAccessor {
    @Accessor(value = "processor", remap = false) InMachineProcessor<RefineryRecipe> ic$getProcessor();
}
