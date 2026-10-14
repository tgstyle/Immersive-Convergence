package com.immersiveconvergence.api.multiblock.blockimpl;

import com.immersiveconvergence.api.multiblock.MultiblockDataLoader;
import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration.ExtraComponent;
import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.component.IClientTickableComponent;
import com.immersiveconvergence.api.multiblock.blocks.component.IMultiblockComponent;
import com.immersiveconvergence.api.multiblock.blocks.component.IServerTickableComponent;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelperMaster;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.registry.MultiblockBlockEntityMaster;
import com.immersiveconvergence.api.multiblock.blocks.util.MultiblockOrientation;
import com.immersiveconvergence.api.multiblock.util.SafeChunkUtils;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MultiblockBEHelperMaster<State extends IMultiblockState>
        extends MultiblockBEHelperCommon<State>
        implements IMultiblockBEHelperMaster<State> {
    private final State state;
    private final MultiblockContext<State> context;
    private final List<ComponentInstance<?>> componentInstances;
    private final Object2IntMap<BlockPos> currentComparatorOutputs = new Object2IntOpenHashMap<>();
    private final CachedValue<BlockPos, MultiblockOrientation, AABB> renderBox;
    private int dataGeneration = MultiblockDataLoader.generation();

    public MultiblockBEHelperMaster(MultiblockBlockEntityMaster<State> be, MultiblockRegistration<State> multiblock) {
        super(be, multiblock, be.getBlockState());
        this.state = multiblock.logic().createInitialState(new InitialMultiblockContext<>(
                be, orientation, multiblock.masterPosInMB()
        ));
        final BlockPos multiblockOrigin = be.getBlockPos().subtract(
                orientation.getAbsoluteOffset(multiblock.masterPosInMB())
        );
        final MultiblockLevel level = new MultiblockLevel(be::getLevel, this.orientation, multiblockOrigin);
        this.context = new MultiblockContext<>(this, multiblock, level);
        this.componentInstances = new ArrayList<>();
        for (ExtraComponent<State, ?> c : multiblock.extraComponents()) { this.componentInstances.add(ComponentInstance.make(c, this.state, this.context)); }
        this.renderBox = new CachedValue<>((origin, orientation) -> {
            final BlockPos maxBlock = new BlockPos(multiblock.size(level.getRawLevel()));
            final Vec3 max = Vec3.atLowerCornerOf(maxBlock).add(1, 1, 1);
            final Vec3 absoluteOffset = orientation.getAbsoluteOffset(max);
            return new AABB(Vec3.ZERO, absoluteOffset).move(origin);
        });
    }

    @Nonnull @Override public State getState() { return state; }

    @Nonnull @Override public MultiblockContext<State> getContext() { return context; }

    @Nullable @Override protected IMultiblockBEHelperMaster<State> getMasterHelperWithChunkloads() { return this; }

    @Nullable @Override protected MultiblockBEHelperMaster<State> getMasterHelper() { return this; }

    @Override public void load(CompoundTag tag, Provider provider) { load(tag, (iMultiblockState, nbt) -> iMultiblockState.readSaveNBT(nbt, provider)); }

    @Override public void saveAdditional(CompoundTag tag, Provider provider) { save(tag, (iMultiblockState, nbt) -> iMultiblockState.writeSaveNBT(nbt, provider)); }

    private void save(CompoundTag out, BiConsumer<IMultiblockState, CompoundTag> saveSingle) {
        saveSingle.accept(state, out);
        ListTag savedComponents = new ListTag();
        for (final ComponentInstance<?> component : componentInstances) {
            if (component.state() instanceof IMultiblockState saveable) {
                CompoundTag componentNBT = new CompoundTag();
                saveSingle.accept(saveable, componentNBT);
                savedComponents.add(componentNBT);
            }
        }
        if (!savedComponents.isEmpty()) { out.put("componentNBT", savedComponents); }
    }

    private void load(CompoundTag in, BiConsumer<IMultiblockState, CompoundTag> loadSingle) {
        loadSingle.accept(state, in);
        ListTag savedComponents = in.getList("componentNBT", Tag.TAG_COMPOUND);
        int nextIndex = 0;
        for (final ComponentInstance<?> component : componentInstances) {
            if (component.state() instanceof IMultiblockState saveable) {
                loadSingle.accept(saveable, savedComponents.getCompound(nextIndex));
                ++nextIndex;
                if (nextIndex >= savedComponents.size()) { break; }
            }
        }
    }

    @Override public CompoundTag getUpdateTag(Provider provider) {
        CompoundTag result = new CompoundTag();
        save(result, (iMultiblockState, nbt) -> iMultiblockState.writeSyncNBT(nbt, provider));
        return result;
    }

    @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(getMasterBE()); }

    @Override public void handleUpdateTag(CompoundTag tag, Provider provider) { load(tag, (iMultiblockState, nbt) -> iMultiblockState.readSyncNBT(nbt, provider)); }

    @Override public void onDataPacket(CompoundTag tag, Provider provider) { load(tag, (iMultiblockState, nbt) -> iMultiblockState.readSyncNBT(nbt, provider)); }

    @Override public MultiblockRegistration<State> getMultiblock() { return multiblock; }

    @Override public BlockPos getPositionInMB() { return multiblock.masterPosInMB(); }

    @Override public AABB getRenderBoundingBox() { return renderBox.get(context.getLevel().getAbsoluteOrigin(), orientation); }

    @Override public void tickServer() {
        if (SafeChunkUtils.isChunkUnsafe(be.getLevel(), be.getBlockPos())) { return; }
        followDataReload();
        final IMultiblockComponent<State> logic = multiblock.logic();
        if (logic instanceof IServerTickableComponent<State> serverTickable) { serverTickable.tickServer(getContext()); }
        for (final ComponentInstance<?> component : componentInstances) { component.tickServer(); }
    }

    private void followDataReload() {
        final int generation = MultiblockDataLoader.generation();
        if (dataGeneration == generation) { return; }
        final Level level = be.getLevel();
        if (level == null) { return; }
        dataGeneration = generation;
        invalidateAllCaps();
        for (BlockPos pos : List.copyOf(currentComparatorOutputs.keySet())) { context.setComparatorOutputFor(pos, 0); }
        for (StructureBlockInfo info : multiblock.getStructure().apply(level)) {
            final BlockPos pos = context.getLevel().toAbsolute(info.pos());
            if (neighborhoodSafe(level, pos)) {
                final BlockState cell = level.getBlockState(pos);
                if (!cell.isAir()) { level.updateNeighborsAt(pos, cell.getBlock()); }
            }
        }
    }

    private static boolean neighborhoodSafe(Level level, BlockPos pos) {
        if (SafeChunkUtils.isChunkUnsafe(level, pos)) { return false; }
        for (Direction dir : Direction.values()) {
            if (SafeChunkUtils.isChunkUnsafe(level, pos.relative(dir))) { return false; }
        }
        return true;
    }

    @Override public void tickClient() {
        if (SafeChunkUtils.isChunkUnsafe(be.getLevel(), be.getBlockPos())) { return; }
        final IMultiblockComponent<State> logic = multiblock.logic();
        if (logic instanceof IClientTickableComponent<State> clientTickable) { clientTickable.tickClient(getContext()); }
        for (final ComponentInstance<?> component : componentInstances) { component.tickClient(); }
    }

    @Override public void onRemoved() { multiblock.logic().onRemoved(getContext()); }

    @Override public void invalidateAllCaps() {
        Level level = be.getLevel();
        if (level == null) { return; }
        for (StructureBlockInfo blockInMB : multiblock.getStructure().apply(level)) {
            BlockPos realPosition = context.getLevel().toAbsolute(blockInMB.pos());
            level.invalidateCapabilities(realPosition);
        }
    }

    public BlockEntity getMasterBE() { return be; }

    public MultiblockOrientation getOrientation() { return orientation; }

    public Object2IntMap<BlockPos> getCurrentComparatorOutputs() { return currentComparatorOutputs; }

    public List<ComponentInstance<?>> getComponentInstances() { return componentInstances; }
}
