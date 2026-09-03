package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.block.ModProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nonnull;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MultiblockPartBlockWithMirrorActive<S extends IMultiblockState> extends MultiblockPartBlockWithMirror<S> {
    public static final Property<Boolean> ACTIVE = ModProperties.ACTIVE;

    public MultiblockPartBlockWithMirrorActive(BlockBehaviour.Properties properties, MultiblockRegistration<S> multiblock) {
        super(properties, multiblock);
        registerDefaultState(defaultBlockState().setValue(ACTIVE, false));
    }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }
}
