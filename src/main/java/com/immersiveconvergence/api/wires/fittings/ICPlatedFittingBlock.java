package com.immersiveconvergence.api.wires.fittings;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public abstract class ICPlatedFittingBlock<T extends ICFittingBlockEntity> extends ICFittingBlock<T> {
    public static final BooleanProperty PLATE = BooleanProperty.create("plate");

    protected ICPlatedFittingBlock(BiFunction<BlockPos, BlockState, T> makeEntity) { super(makeEntity); }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PLATE);
    }

    @Override protected BlockState getInitDefaultState() { return super.getInitDefaultState().setValue(PLATE, true); }
}
