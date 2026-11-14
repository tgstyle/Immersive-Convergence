package com.immersiveconvergence.api.wires.fittings;

import com.immersiveconvergence.api.block.ModProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public abstract class ICTransformerBlock<T extends ICTransformerBlockEntity> extends ICFittingBlock<T> {
    private final ICTransformerLayout layout;

    protected ICTransformerBlock(BiFunction<BlockPos, BlockState, T> makeEntity, ICTransformerLayout layout) {
        super(makeEntity);
        this.layout = layout;
    }

    @Override protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ModProperties.MULTIBLOCKSLAVE);
    }

    @Override @Nullable public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        for (int index = 1; index < layout.cells(); index++) {
            BlockPos above = context.getClickedPos().above(index);
            if (above.getY() >= level.getMaxBuildHeight() || !level.getBlockState(above).canBeReplaced(context)) { return null; }
        }
        return super.getStateForPlacement(context);
    }

    @Override public void onIEBlockPlacedBy(BlockPlaceContext context, BlockState state) {
        super.onIEBlockPlacedBy(context, state);
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!(level.getBlockEntity(pos) instanceof ICTransformerBlockEntity base)) { return; }
        Direction facing = base.getFacing();
        base.assemble(facing, 0);
        BlockState upper = state.setValue(ModProperties.MULTIBLOCKSLAVE, true);
        for (int index = 1; index < layout.cells(); index++) {
            BlockPos above = pos.above(index);
            level.setBlockAndUpdate(above, upper);
            if (level.getBlockEntity(above) instanceof ICTransformerBlockEntity tile) { tile.assemble(facing, index); }
        }
    }

    @Override public void onRemove(BlockState state, Level level, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        int cell = level.getBlockEntity(pos) instanceof ICTransformerBlockEntity tile ? tile.cell() : -1;
        boolean gone = state.getBlock() != newState.getBlock();
        super.onRemove(state, level, pos, newState, isMoving);
        if (!gone || cell < 0) { return; }
        BlockPos base = pos.below(cell);
        for (int index = 0; index < layout.cells(); index++) {
            BlockPos other = base.above(index);
            if (index != cell && level.getBlockState(other).getBlock() == this) { level.removeBlock(other, false); }
        }
    }
}
