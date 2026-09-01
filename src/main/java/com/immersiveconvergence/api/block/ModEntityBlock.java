package com.immersiveconvergence.api.block;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings({"RedundantSuppression", "deprecation", "unused"}) public class ModEntityBlock<T extends BlockEntity> extends BaseBlock implements EntityBlock {
    private final BiFunction<BlockPos, BlockState, T> makeEntity;
    private BEClassInspectedData classData;

    public ModEntityBlock(BiFunction<BlockPos, BlockState, T> makeEntity, Properties blockProps) { this(makeEntity, blockProps, true); }

    public ModEntityBlock(BiFunction<BlockPos, BlockState, T> makeEntity, Properties blockProps, boolean fitsIntoContainer) {
        super(blockProps, fitsIntoContainer);
        this.makeEntity = makeEntity;
    }

    @Override @Nullable public BlockEntity newBlockEntity(@Nonnull BlockPos pPos, @Nonnull BlockState pState) { return makeEntity.apply(pPos, pState); }

    @Override @Nullable public <U extends BlockEntity> BlockEntityTicker<U> getTicker(Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<U> type) { return getClassData().makeBaseTicker(world.isClientSide); }

    private static final List<BooleanProperty> DEFAULT_OFF = ImmutableList.of(ModProperties.MULTIBLOCKSLAVE, ModProperties.ACTIVE, ModProperties.MIRRORED);

    @Override protected BlockState getInitDefaultState() {
        BlockState ret = super.getInitDefaultState();
        if (ret.hasProperty(ModProperties.FACING_ALL)) { ret = ret.setValue(ModProperties.FACING_ALL, getDefaultFacing()); }
        else if (ret.hasProperty(ModProperties.FACING_HORIZONTAL)) { ret = ret.setValue(ModProperties.FACING_HORIZONTAL, getDefaultFacing()); }
        for (BooleanProperty defaultOff : DEFAULT_OFF) { if (ret.hasProperty(defaultOff)) { ret = ret.setValue(defaultOff, false); } }
        return ret;
    }

    @Override public void onRemove(BlockState state, Level world, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (state.getBlock() != newState.getBlock()) {
            if (state.getBlock() != newState.getBlock()) {
                if (tile instanceof BaseBlockEntity) { ((BaseBlockEntity) tile).setOverrideState(state); }
                if (tile instanceof BlockInterfaces.IHasDummyBlocks) { ((BlockInterfaces.IHasDummyBlocks) tile).breakDummies(pos, state); }
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override public void playerDestroy(@Nonnull Level world, @Nonnull Player player, @Nonnull BlockPos pos, @Nonnull BlockState state, BlockEntity tile, @Nonnull ItemStack stack) {
        if (tile instanceof BlockInterfaces.IAdditionalDrops) {
            Collection<ItemStack> stacks = ((BlockInterfaces.IAdditionalDrops) tile).getExtraDrops(player, state);
            if (!stacks.isEmpty()) { for (ItemStack s : stacks) { if (!s.isEmpty()) { popResource(world, pos, s); } } }
        }
        super.playerDestroy(world, player, pos, state, tile, stack);
    }

    @Override public boolean canEntityDestroy(@Nonnull BlockState state, BlockGetter world, @Nonnull BlockPos pos, @Nonnull Entity entity) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof BlockInterfaces.IEntityProof) { return ((BlockInterfaces.IEntityProof) tile).canEntityDestroy(entity); }
        return super.canEntityDestroy(state, world, pos, entity);
    }

    @Override public boolean triggerEvent(@Nonnull BlockState state, Level worldIn, @Nonnull BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, worldIn, pos, eventID, eventParam);
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
    }

    protected Direction getDefaultFacing() { return Direction.NORTH; }

    @Override public void onIEBlockPlacedBy(BlockPlaceContext context, BlockState state) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity tile = world.getBlockEntity(pos);
        Player placer = context.getPlayer();
        Direction side = context.getClickedFace();
        float hitX = (float) context.getClickLocation().x - pos.getX();
        float hitY = (float) context.getClickLocation().y - pos.getY();
        float hitZ = (float) context.getClickLocation().z - pos.getZ();
        if (tile instanceof BlockInterfaces.IDirectionalBE directionalBE) {
            Direction f = directionalBE.getFacingForPlacement(context);
            directionalBE.setFacing(f);
            if (tile instanceof BlockInterfaces.IAdvancedDirectionalBE advDirectional) { advDirectional.onDirectionalPlacement(side, hitX, hitY, hitZ, placer); }
        }
        if (tile instanceof BlockInterfaces.IHasDummyBlocks hasDummyBlocks) { hasDummyBlocks.placeDummies(context, state); }
        if (tile instanceof BlockInterfaces.IPlacementInteraction placementInteractionBE) { placementInteractionBE.onBEPlaced(context); }
    }

    @Override public InteractionResult hammerUseSide(Direction side, Player player, InteractionHand hand, Level w, BlockPos pos, BlockHitResult hit) {
        BlockEntity tile = w.getBlockEntity(pos);
        if (tile instanceof BlockInterfaces.IHammerInteraction) {
            boolean b = ((BlockInterfaces.IHammerInteraction) tile).hammerUseSide(side, player, hand, hit.getLocation());
            if (b) { return InteractionResult.SUCCESS; }
            else { return InteractionResult.FAIL; }
        }
        return super.hammerUseSide(side, player, hand, w, pos, hit);
    }

    @Override public InteractionResult screwdriverUseSide(Direction side, Player player, InteractionHand hand, Level w, BlockPos pos, BlockHitResult hit) {
        BlockEntity tile = w.getBlockEntity(pos);
        if (tile instanceof BlockInterfaces.IScrewdriverInteraction) {
            InteractionResult teResult = ((BlockInterfaces.IScrewdriverInteraction) tile).screwdriverUseSide(side, player, hand, hit.getLocation());
            if (teResult != InteractionResult.PASS) { return teResult; }
        }
        return super.screwdriverUseSide(side, player, hand, w, pos, hit);
    }

    @Override @Nonnull public InteractionResult useWithoutItem(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull BlockHitResult hit) {
        InteractionResult superResult = super.useWithoutItem(state, world, pos, player, hit);
        if (superResult.consumesAction()) { return superResult; }
        Direction side = hit.getDirection();
        float hitX = (float) hit.getLocation().x - pos.getX();
        float hitY = (float) hit.getLocation().y - pos.getY();
        float hitZ = (float) hit.getLocation().z - pos.getZ();
        ItemStack heldItem = player.getMainHandItem();
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof BlockInterfaces.IDirectionalBE && BlockToolGates.isFormationTool.test(heldItem) && ((BlockInterfaces.IDirectionalBE) tile).canHammerRotate(side, hit.getLocation().subtract(Vec3.atLowerCornerOf(pos)), player) && !world.isClientSide) {
            Direction f = ((BlockInterfaces.IDirectionalBE) tile).getFacing();
            FacingLimitation limit = ((BlockInterfaces.IDirectionalBE) tile).getFacingLimitation();
            f = switch (limit) {
                case SIDE_CLICKED -> Direction.values()[Math.floorMod(f.ordinal() + (player.isShiftKeyDown() ? -1 : 1), 6)];
                case PISTON_LIKE -> {
                    Direction.Axis axis = side.getAxis();
                    Direction rotated = rotateAround(f, axis);
                    yield player.isShiftKeyDown() != (side.getAxisDirection() == Direction.AxisDirection.NEGATIVE) ? rotated.getOpposite() : rotated;
                }
                case HORIZONTAL, HORIZONTAL_PREFER_SIDE, HORIZONTAL_QUADRANT, HORIZONTAL_AXIS -> player.isShiftKeyDown() != side.equals(Direction.DOWN) ? f.getCounterClockWise() : f.getClockWise();
                default -> f;
            };
            ((BlockInterfaces.IDirectionalBE) tile).setFacing(f);
            ((BlockInterfaces.IDirectionalBE) tile).afterRotation();
            tile.setChanged();
            world.sendBlockUpdated(pos, state, state, 3);
            world.blockEvent(tile.getBlockPos(), tile.getBlockState().getBlock(), 255, 0);
            return InteractionResult.SUCCESS;
        }
        if (tile instanceof BlockInterfaces.IConfigurableSides && BlockToolGates.isFormationTool.test(heldItem) && !world.isClientSide) {
            Direction configSide = player.isShiftKeyDown() ? side.getOpposite() : side;
            if (((BlockInterfaces.IConfigurableSides) tile).toggleSide(configSide, player)) { return InteractionResult.SUCCESS; }
        }
        if (tile instanceof BlockInterfaces.IPlayerInteraction) {
            boolean b = ((BlockInterfaces.IPlayerInteraction) tile).interact(side, player, InteractionHand.MAIN_HAND, heldItem, hitX, hitY, hitZ);
            if (b) { return InteractionResult.SUCCESS; }
        }
        if (tile instanceof MenuProvider menuProvider && !player.isShiftKeyDown()) {
            if (player instanceof ServerPlayer serverPlayer) {
                if (menuProvider instanceof IMasterMenuProvider<?> interaction) {
                    interaction = interaction.getGuiMaster();
                    if (interaction != null && interaction.canUseGui(player)) { serverPlayer.openMenu(interaction); }
                }
                else { serverPlayer.openMenu(menuProvider); }
            }
            return InteractionResult.SUCCESS;
        }
        return superResult;
    }

    private static Direction rotateAround(Direction dir, Direction.Axis axis) {
        if (dir.getAxis() == axis) { return dir; }
        return dir.getClockWise(axis);
    }

    @Override public void neighborChanged(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos fromPos, boolean isMoving) {
        if (!world.isClientSide) {
            BlockEntity tile = world.getBlockEntity(pos);
            if (tile instanceof BaseBlockEntity) { ((BaseBlockEntity) tile).onNeighborBlockChange(fromPos); }
        }
    }

    @Override @Nonnull public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof BlockInterfaces.ISelectionBounds) { return ((BlockInterfaces.ISelectionBounds) te).getSelectionShape(context); }
        return super.getShape(state, world, pos, context);
    }

    @Override @Nonnull public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof BlockInterfaces.ICollisionBounds collisionBounds) { return collisionBounds.getCollisionShape(context); }
        return super.getCollisionShape(state, world, pos, context);
    }

    @Override @Nonnull public VoxelShape getInteractionShape(@Nonnull BlockState state, BlockGetter world, @Nonnull BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof BlockInterfaces.ISelectionBounds) { return ((BlockInterfaces.ISelectionBounds) te).getSelectionShape(null); }
        return super.getInteractionShape(state, world, pos);
    }

    @Override public boolean hasAnalogOutputSignal(@Nonnull BlockState state) { return getClassData().hasComparatorOutput; }

    @Override public int getAnalogOutputSignal(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof BlockInterfaces.IComparatorOverride compOverride) { return compOverride.getComparatorInputOverride(); }
        return 0;
    }

    @Override public int getSignal(@Nonnull BlockState blockState, BlockGetter world, @Nonnull BlockPos pos, @Nonnull Direction side) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof BlockInterfaces.IRedstoneOutput rsOutput) { return rsOutput.getWeakRSOutput(side); }
        return 0;
    }

    @Override public int getDirectSignal(@Nonnull BlockState blockState, BlockGetter world, @Nonnull BlockPos pos, @Nonnull Direction side) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof BlockInterfaces.IRedstoneOutput rsOutput) { return rsOutput.getStrongRSOutput(side); }
        return 0;
    }

    @Override public boolean isSignalSource(@Nonnull BlockState state) { return getClassData().emitsRedstone(); }

    @Override public boolean canConnectRedstone(@Nonnull BlockState state, BlockGetter world, @Nonnull BlockPos pos, Direction side) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof BlockInterfaces.IRedstoneOutput rsOutput) { return rsOutput.canConnectRedstone(side); }
        return false;
    }

    @Override public void entityInside(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull Entity entity) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof BaseBlockEntity) { ((BaseBlockEntity) te).onEntityCollision(world, entity); }
    }

    @Override
    @SuppressWarnings("unused")
    public Component[] getOverlayText(BlockState state, Level level, BlockPos pos, Player player, HitResult rayTrace, boolean hammer) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof BlockInterfaces.IBlockOverlayText overlay) { return overlay.getOverlayText(player, rayTrace, hammer); }
        return null;
    }

    private BEClassInspectedData getClassData() {
        if (this.classData == null) {
            T tempBE = makeEntity.apply(BlockPos.ZERO, getInitDefaultState());
            this.classData = new BEClassInspectedData(tempBE instanceof IServerTickableBE, tempBE instanceof IClientTickableBE, tempBE instanceof BlockInterfaces.IComparatorOverride, tempBE instanceof BlockInterfaces.IRedstoneOutput);
        }
        return this.classData;
    }

    private record BEClassInspectedData(boolean serverTicking, boolean clientTicking, boolean hasComparatorOutput, boolean emitsRedstone) {
        @Nullable public <U extends BlockEntity> BlockEntityTicker<U> makeBaseTicker(boolean isClient) {
            if (serverTicking && !isClient) { return IServerTickableBE.makeTicker(); }
            else if (clientTicking && isClient) { return IClientTickableBE.makeTicker(); }
            else { return null; }
        }
    }

    @Override @Nonnull public ItemStack getCloneItemStack(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof BlockInterfaces.IBlockEntityDrop drop) {
            ItemStack stack = drop.getPickBlock(state);
            if (!stack.isEmpty()) { return stack; }
        }
        return super.getCloneItemStack(level, pos, state);
    }
}
