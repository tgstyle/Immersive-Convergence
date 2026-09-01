package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.google.common.base.Preconditions;
import com.immersiveconvergence.api.block.ModProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MultiblockPartBlockWithMirror<S extends IMultiblockState> extends MachineMultiblockPartBlock<S> {
    public MultiblockPartBlockWithMirror(BlockBehaviour.Properties properties, MultiblockRegistration<S> multiblock) {
        super(properties, multiblock);
        Preconditions.checkState(multiblock.mirrorable());
    }

    @Override protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ModProperties.MIRRORED);
    }
}
