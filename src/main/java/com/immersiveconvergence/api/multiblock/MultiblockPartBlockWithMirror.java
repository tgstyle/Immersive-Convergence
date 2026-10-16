package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.block.ModProperties;

import com.google.common.base.Preconditions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockBehaviour;
import javax.annotation.Nonnull;

public class MultiblockPartBlockWithMirror<S extends IMultiblockState> extends MachineMultiblockPartBlock<S> {
    public MultiblockPartBlockWithMirror(BlockBehaviour.Properties properties, MultiblockRegistration<S> multiblock) {
        super(properties, multiblock);
        Preconditions.checkState(multiblock.mirrorable());
        registerDefaultState(defaultBlockState().setValue(ModProperties.MIRRORED, false));
    }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ModProperties.MIRRORED);
    }
}
