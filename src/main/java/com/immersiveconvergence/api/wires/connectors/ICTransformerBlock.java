package com.immersiveconvergence.api.wires.connectors;

import com.immersiveconvergence.api.block.ModProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public abstract class ICTransformerBlock<T extends ICTransformerBlockEntity> extends ICConnectorBlock<T> {
    protected ICTransformerBlock(BiFunction<BlockPos, BlockState, T> makeEntity) { super(makeEntity); }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ModProperties.MULTIBLOCKSLAVE);
    }

    @Override @Nullable public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos middle = context.getClickedPos().above();
        BlockPos top = middle.above();
        return top.getY() < level.getMaxBuildHeight() && level.getBlockState(middle).canBeReplaced(context) && level.getBlockState(top).canBeReplaced(context) ? super.getStateForPlacement(context) : null;
    }

    @Override public void onIEBlockPlacedBy(BlockPlaceContext context, BlockState state) {
        super.onIEBlockPlacedBy(context, state);
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!(level.getBlockEntity(pos) instanceof ICTransformerBlockEntity below)) { return; }
        BlockState upper = state.setValue(ModProperties.MULTIBLOCKSLAVE, true);
        level.setBlockAndUpdate(pos.above(), upper);
        if (level.getBlockEntity(pos.above()) instanceof ICTransformerBlockEntity middle) { middle.fill(below.getFacing()); }
        level.setBlockAndUpdate(pos.above(2), upper);
        if (level.getBlockEntity(pos.above(2)) instanceof ICTransformerBlockEntity top) { top.raise(below.getFacing()); }
    }

    @Override public void onRemove(BlockState state, Level level, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        boolean removeAbove = level.getBlockEntity(pos) instanceof ICTransformerBlockEntity tile && !tile.isTop();
        boolean removeBelow = level.getBlockEntity(pos) instanceof ICTransformerBlockEntity tile && tile.isUpperCell();
        boolean gone = state.getBlock() != newState.getBlock();
        super.onRemove(state, level, pos, newState, isMoving);
        if (!gone) { return; }
        if (removeAbove && level.getBlockState(pos.above()).getBlock() == this) { level.removeBlock(pos.above(), false); }
        if (removeBelow && level.getBlockState(pos.below()).getBlock() == this) { level.removeBlock(pos.below(), false); }
    }
}
