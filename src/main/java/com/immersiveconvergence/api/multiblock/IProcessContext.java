package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import net.minecraft.world.level.Level;

@SuppressWarnings({"unused", "RedundantSuppression"}) public interface IProcessContext<R extends MultiblockRecipe> extends ProcessContext<R> {
    AveragingEnergyStorage getEnergy();

    default boolean additionalCanProcessCheck(MultiblockProcess<R, ?> process, Level level) { return true; }

    default void onProcessFinish(MultiblockProcess<R, ?> process, Level level) { }

    interface IProcessContextInMachine<R extends MultiblockRecipe> extends IProcessContext<R>, ProcessContext.ProcessContextInMachine<R> { }
}
