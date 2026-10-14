package com.immersiveconvergence.api.multiblock.blocks.registry;

import com.immersiveconvergence.api.block.ModProperties;
import com.immersiveconvergence.api.multiblock.QueueProcessor;
import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration.ExtraComponent;
import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.component.IClientTickableComponent;
import com.immersiveconvergence.api.multiblock.blocks.component.IServerTickableComponent;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockBE;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.util.ShapeType;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MultiblockPartBlock<State extends IMultiblockState> extends Block implements EntityBlock {
    private final MultiblockRegistration<State> multiblock;
    private final boolean needsServerTicker;
    private final boolean needsClientTicker;

    public MultiblockPartBlock(Properties properties, MultiblockRegistration<State> multiblock) {
        super(properties.dynamicShape());
        this.multiblock = multiblock;
        final boolean hasMirrorProperty = getStateDefinition().getProperties().contains(ModProperties.MIRRORED);
        Preconditions.checkState(this.multiblock.mirrorable() == hasMirrorProperty);
        if (multiblock.logic() instanceof IServerTickableComponent<?>) { needsServerTicker = true; }
        else {
            needsServerTicker = multiblock.extraComponents().stream()
                    .map(ExtraComponent::component)
                    .anyMatch(c -> c instanceof IServerTickableComponent<?>);
        }
        if (multiblock.logic() instanceof IClientTickableComponent<?>) { needsClientTicker = true; }
        else {
            needsClientTicker = multiblock.extraComponents().stream()
                    .map(ExtraComponent::component)
                    .anyMatch(c -> c instanceof IClientTickableComponent<?>);
        }
    }

    @Override protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ModProperties.MULTIBLOCKSLAVE);
        builder.add(ModProperties.FACING_HORIZONTAL);
    }

    @Nullable @Override public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        if (state.getValue(ModProperties.MULTIBLOCKSLAVE)) { return multiblock.dummyBE().get().create(pos, state); }
        else { return multiblock.masterBE().get().create(pos, state); }
    }

    @Nullable @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            @Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> actual
    ) {
        if (state.getValue(ModProperties.MULTIBLOCKSLAVE)) { return null; }
        if (level.isClientSide && needsClientTicker) { return createTickerHelper(actual, ($1, $2, $3, blockEntity) -> blockEntity.getHelper().tickClient()); }
        if (!level.isClientSide && needsServerTicker) { return createTickerHelper(actual, ($1, $2, $3, blockEntity) -> blockEntity.getHelper().tickServer()); }
        return null;
    }

    @SuppressWarnings("unchecked") @Nullable protected <A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> actual, BlockEntityTicker<MultiblockBlockEntityMaster<State>> ticker) { return multiblock.masterBE().get() == actual ? (BlockEntityTicker<A>)ticker : null; }

    @Nonnull @Override public VoxelShape getCollisionShape(
            @Nonnull BlockState state,
            @Nonnull BlockGetter level,
            @Nonnull BlockPos pos,
            @Nonnull CollisionContext context
    ) { return getShape(level, pos, context, ShapeType.COLLISION); }

    @Nonnull @Override public VoxelShape getShape(
            @Nonnull BlockState state,
            @Nonnull BlockGetter level,
            @Nonnull BlockPos pos,
            @Nonnull CollisionContext context
    ) { return getShape(level, pos, context, ShapeType.SELECTION); }

    @Override @Nonnull public VoxelShape getBlockSupportShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) { return getShape(level, pos, null, ShapeType.BLOCK_SUPPORT); }

    @Nonnull private VoxelShape getShape(
            @Nonnull BlockGetter level,
            @Nonnull BlockPos pos,
            @Nullable CollisionContext context,
            @Nonnull ShapeType type
    ) {
        final BlockEntity bEntity = level.getBlockEntity(pos);
        if (bEntity instanceof IMultiblockBE<?> multiblockBE) { return multiblockBE.getHelper().getShape(context, type); }
        else { return Shapes.block(); }
    }

    @Override public void onRemove(BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock() && level.getBlockEntity(pos) instanceof IMultiblockBE<?> multiblockBE) {
            super.onRemove(state, level, pos, newState, isMoving);
            multiblockBE.getHelper().disassemble();
        }
        else { super.onRemove(state, level, pos, newState, isMoving); }
        QueueProcessor.currentlyBreakingPos = null;
        QueueProcessor.sneakBreaking = false;
    }

    @Override public int getLightBlock(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) { return 0; }

    @Override public boolean propagatesSkylightDown(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) { return true; }

    @Nonnull @Override public ItemInteractionResult useItemOn(
            @Nonnull ItemStack stack,
            @Nonnull BlockState state,
            @Nonnull Level level,
            @Nonnull BlockPos pos,
            @Nonnull Player player,
            @Nonnull InteractionHand hand,
            @Nonnull BlockHitResult hit
    ) {
        final BlockEntity bEntity = level.getBlockEntity(pos);
        if (bEntity instanceof IMultiblockBE<?> multiblockBE) { return multiblockBE.getHelper().click(player, hand, hit); }
        else { return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION; }
    }

    @Override public void entityInside(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Entity entity) {
        final BlockEntity bEntity = level.getBlockEntity(pos);
        if (bEntity instanceof IMultiblockBE<?> multiblockBE) { multiblockBE.getHelper().onEntityCollided(entity); }
    }

    @Override public boolean hasAnalogOutputSignal(@Nonnull BlockState state) { return multiblock.hasComparatorOutput(); }

    @Override public int getAnalogOutputSignal(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos) {
        if (multiblock.hasComparatorOutput() && level.getBlockEntity(pos) instanceof IMultiblockBE<?> multiblockBE) { return multiblockBE.getHelper().getComparatorValue(); }
        return super.getAnalogOutputSignal(state, level, pos);
    }

    @Override public void neighborChanged(
            @Nonnull BlockState state,
            @Nonnull Level level,
            @Nonnull BlockPos pos,
            @Nonnull Block block,
            @Nonnull BlockPos fromPos,
            boolean isMoving
    ) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (multiblock.redstoneInputAware() && level.getBlockEntity(pos) instanceof IMultiblockBE<?> multiblockBE) { multiblockBE.getHelper().onNeighborChanged(fromPos); }
    }

    @Nonnull @Override public ItemStack getCloneItemStack(@Nonnull BlockState state, @Nonnull HitResult target, @Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull Player player) {
        if (level.getBlockEntity(pos) instanceof IMultiblockBE<?> multiblockBE) { return multiblockBE.getHelper().getPickBlock(); }
        else { return ItemStack.EMPTY; }
    }

    @Override protected boolean isPathfindable(@Nonnull BlockState state, @Nonnull PathComputationType type) { return false; }

    public static class WithMirrorState<State extends IMultiblockState> extends MultiblockPartBlock<State> {
        public WithMirrorState(Properties properties, MultiblockRegistration<State> multiblock) { super(properties, multiblock); }

        @Override protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
            super.createBlockStateDefinition(builder);
            builder.add(ModProperties.MIRRORED);
        }
    }
}
