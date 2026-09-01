package com.immersiveconvergence.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.google.common.base.Preconditions;
import com.immersiveconvergence.api.block.ModProperties;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nonnull;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MultiblockPartBlockNonMirrorActiveBlock<S extends IMultiblockState> extends MachineMultiblockPartBlock<S> {
    public static final Property<Boolean> ACTIVE;

    public MultiblockPartBlockNonMirrorActiveBlock(BlockBehaviour.Properties properties, MultiblockRegistration<S> multiblock) {
        super(properties, multiblock);
        Preconditions.checkState(!multiblock.mirrorable());
    }

    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    static { ACTIVE = ModProperties.ACTIVE; }
}
