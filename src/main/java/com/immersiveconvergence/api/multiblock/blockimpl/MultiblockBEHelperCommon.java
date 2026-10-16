package com.immersiveconvergence.api.multiblock.blockimpl;

import com.immersiveconvergence.api.multiblock.ClearTankRegistry;
import com.immersiveconvergence.api.multiblock.IDisassemblingAware;
import com.immersiveconvergence.api.multiblock.MultiblockDataLoader;
import com.immersiveconvergence.api.multiblock.MultiblockOverride;
import com.immersiveconvergence.api.multiblock.QueueProcessor;
import com.immersiveconvergence.api.multiblock.ShapeData;
import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelper;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelperMaster;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockLevel;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.util.CapabilityPosition;
import com.immersiveconvergence.api.multiblock.blocks.util.MultiblockOrientation;
import com.immersiveconvergence.api.multiblock.blocks.util.RelativeBlockFace;
import com.immersiveconvergence.api.multiblock.blocks.util.ShapeType;
import com.immersiveconvergence.api.multiblock.util.DirectionUtils;
import com.immersiveconvergence.api.multiblock.util.MultiblockItemUtils;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public abstract class MultiblockBEHelperCommon<State extends IMultiblockState> implements IMultiblockBEHelper<State>, IDisassemblingAware {
    protected final BlockEntity be;
    private boolean beingDisassembled = false;
    private final Map<ShapeType, VoxelShape> overrideShapes = new EnumMap<>(ShapeType.class);
    @Nullable private BlockPos overrideShapesPos;
    private int shapeGeneration = MultiblockDataLoader.generation();
    private final Map<RelativeBlockFace, Integer> cachedRedstoneValues = new EnumMap<>(RelativeBlockFace.class);
    protected final MultiblockRegistration<State> multiblock;
    protected final MultiblockOrientation orientation;
    private final EnumMap<ShapeType, CachedValue<BlockPos, MultiblockOrientation, VoxelShape>> cachedShape;

    protected MultiblockBEHelperCommon(BlockEntity be, MultiblockRegistration<State> multiblock, BlockState state) {
        this.be = be;
        this.multiblock = multiblock;
        this.orientation = new MultiblockOrientation(state, multiblock.mirrorable());
        this.cachedShape = new EnumMap<>(ShapeType.class);
        for (final ShapeType type : ShapeType.values()) {
            this.cachedShape.put(type, new CachedValue<>((pos, orientation) -> {
                final VoxelShape relative = multiblock.logic().shapeGetter(type).apply(pos);
                return orientation.transformRelativeShape(relative);
            }));
        }
    }

    @Override public boolean ic$isDisassembling() { return beingDisassembled; }

    @Override public VoxelShape getShape(@Nullable CollisionContext ctx, ShapeType type) {
        final BlockPos posInMB = getPositionInMB();
        final int generation = MultiblockDataLoader.generation();
        if (shapeGeneration != generation) {
            shapeGeneration = generation;
            for (CachedValue<?, ?, ?> cached : cachedShape.values()) { cached.reset(); }
            overrideShapesPos = null;
        }
        final VoxelShape overridden = getOverrideShape(posInMB, type);
        return overridden != null ? overridden : cachedShape.get(type).get(posInMB, orientation);
    }

    @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(be); }

    @Override public MultiblockRegistration<State> getMultiblock() { return multiblock; }

    @Nullable private VoxelShape getOverrideShape(BlockPos posInMB, ShapeType type) {
        final MultiblockOverride override = MultiblockOverride.get(multiblock.id());
        final Level level = be.getLevel();
        if (override == null || level == null) { return null; }
        final ShapeData shape = override.shape(multiblock.id(), multiblock.size(level));
        if (shape == null) { return null; }
        if (!posInMB.equals(overrideShapesPos)) {
            overrideShapes.clear();
            overrideShapesPos = posInMB.immutable();
        }
        return overrideShapes.computeIfAbsent(type, t -> orientation.transformRelativeShape(shape.getter.apply(posInMB)));
    }

    @Override public CapabilityPosition getCapabilityPosition(@Nullable Direction side) {
        final RelativeBlockFace relativeSide = RelativeBlockFace.from(orientation, side);
        return new CapabilityPosition(getPositionInMB(), relativeSide);
    }

    @Override public ItemInteractionResult click(Player player, InteractionHand hand, BlockHitResult hit) {
        final MultiblockBEHelperMaster<State> helper = getMasterHelper();
        if (helper == null) { return ItemInteractionResult.FAIL; }
        final MultiblockContext<State> ctx = helper.getContext();
        final ItemInteractionResult cleared = ClearTankRegistry.handle(multiblock.id(), getPositionInMB(), ctx.getState(), ctx::markDirtyAndSync, player, hand);
        if (cleared != null) { return cleared; }
        for (final ComponentInstance<?> component : helper.getComponentInstances()) {
            final ItemInteractionResult componentResult = component.click(
                    getPositionInMB(), player, hand, hit, player.level().isClientSide
            );
            if (componentResult != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION) { return componentResult; }
        }
        return multiblock.logic().click(ctx, getPositionInMB(), player, hand, hit, player.level().isClientSide);
    }

    @Override public void disassemble() {
        if (beingDisassembled || queueDisassembly()) { return; }
        final IMultiblockBEHelperMaster<State> masterHelper = getMasterHelperWithChunkloads();
        if (masterHelper == null) { return; }
        final IMultiblockContext<State> ctx = masterHelper.getContext();
        final IMultiblockLevel levelWrapper = ctx.getLevel();
        final BlockPos absolutePos = levelWrapper.toAbsolute(getPositionInMB());
        final Level levelRaw = levelWrapper.getRawLevel();
        getMultiblock().disassemble().disassemble(
                levelRaw, levelWrapper.getAbsoluteOrigin(), levelWrapper.getOrientation()
        );
        levelRaw.removeBlock(absolutePos, false);
    }

    private boolean queueDisassembly() {
        if (QueueProcessor.MANAGED.contains(multiblock.id())) { return false; }
        final IMultiblockBEHelperMaster<State> master = getMasterHelperWithChunkloads();
        if (master == null) { return false; }
        final IMultiblockLevel mbLevel = master.getContext().getLevel();
        if (!(mbLevel.getRawLevel() instanceof ServerLevel serverLevel)) { return false; }
        final MultiblockOrientation mbOrientation = mbLevel.getOrientation();
        final Rotation rot = DirectionUtils.getRotationBetweenFacings(Direction.NORTH, mbOrientation.front());
        if (rot == null) { return false; }
        final Mirror mirror = mbOrientation.mirrored() ? Mirror.FRONT_BACK : Mirror.NONE;
        final BlockPos masterPos = mbLevel.toAbsolute(multiblock.masterPosInMB());
        if (!QueueProcessor.disassemble(serverLevel, multiblock.getStructure().apply(serverLevel), mbLevel.getAbsoluteOrigin(), mirror, rot, masterPos, multiblock.size(serverLevel), multiblock.block().get(), false, null)) { return false; }
        beingDisassembled = true;
        final BlockPos brokenPos = mbLevel.toAbsolute(getPositionInMB());
        serverLevel.removeBlock(brokenPos, false);
        QueueProcessor.refreshLight(serverLevel, brokenPos);
        return true;
    }

    @Nullable protected abstract IMultiblockBEHelperMaster<State> getMasterHelperWithChunkloads();

    @Nullable protected abstract MultiblockBEHelperMaster<State> getMasterHelper();

    @Override public void markDisassembling() { beingDisassembled = true; }

    @Override public int getComparatorValue() {
        if (!multiblock.hasComparatorOutput()) { return 0; }
        final MultiblockBEHelperMaster<State> masterHelper = getMasterHelper();
        if (masterHelper == null) { return 0; }
        return masterHelper.getCurrentComparatorOutputs().getInt(getPositionInMB());
    }

    @Override public void onNeighborChanged(BlockPos fromPos) {
        BlockPos delta = fromPos.subtract(be.getBlockPos());
        Direction sideAbsolute = Direction.getNearest(delta.getX(), delta.getY(), delta.getZ());
        Preconditions.checkNotNull(sideAbsolute);
        updateRedstoneValue(sideAbsolute, fromPos);
    }

    @Override public int getRedstoneInput(RelativeBlockFace side) {
        if (cachedRedstoneValues.containsKey(side)) { return cachedRedstoneValues.get(side); }
        else {
            final Direction absoluteFace = side.forFront(orientation);
            final BlockPos neighbor = be.getBlockPos().relative(absoluteFace);
            return updateRedstoneValue(absoluteFace, neighbor);
        }
    }

    @Override public BlockState getOriginalBlock(Level level) {
        for (StructureBlockInfo block : multiblock.getStructure().apply(level)) {
            if (block.pos().equals(getPositionInMB())) { return block.state(); }
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override public ItemStack getPickBlock() {
        final Level level = be.getLevel();
        if (level == null) { return ItemStack.EMPTY; }
        return MultiblockItemUtils.getPickBlock(getOriginalBlock(level));
    }

    private int updateRedstoneValue(Direction sideAbsolute, BlockPos neighborPos) {
        final Level level = Objects.requireNonNull(be.getLevel());
        final int rsStrength = level.getSignal(neighborPos, sideAbsolute);
        final RelativeBlockFace sideRelative = RelativeBlockFace.from(orientation, sideAbsolute);
        cachedRedstoneValues.put(sideRelative, rsStrength);
        return rsStrength;
    }

    protected static class CachedValue<K1, K2, T> {
        private final BiFunction<K1, K2, T> computeValue;
        @Nullable private T value;
        private K1 key1;
        private K2 key2;

        protected CachedValue(BiFunction<K1, K2, T> computeValue) { this.computeValue = computeValue; }

        void reset() { value = null; }

        public T get(K1 first, K2 second) {
            if (value == null || !Objects.equals(first, key1) || !Objects.equals(second, key2)) {
                value = computeValue.apply(first, second);
                this.key1 = first;
                this.key2 = second;
            }
            return value;
        }
    }
}
