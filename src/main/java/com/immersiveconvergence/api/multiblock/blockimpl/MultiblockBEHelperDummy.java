package com.immersiveconvergence.api.multiblock.blockimpl;

import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelperDummy;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelperMaster;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.registry.MultiblockBlockEntityMaster;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import javax.annotation.Nullable;
import java.util.function.Function;

public class MultiblockBEHelperDummy<State extends IMultiblockState>
        extends MultiblockBEHelperCommon<State>
        implements IMultiblockBEHelperDummy<State> {
    private final MultiblockLevel level;
    private BlockPos positionInMB;

    public MultiblockBEHelperDummy(BlockEntity be, MultiblockRegistration<State> multiblock) {
        super(be, multiblock, be.getBlockState());
        this.level = new MultiblockLevel(be::getLevel, orientation, () -> {
            final BlockPos absoluteOffset = orientation.getAbsoluteOffset(positionInMB);
            return be.getBlockPos().subtract(absoluteOffset);
        });
        this.positionInMB = BlockPos.ZERO;
    }

    @Override @Nullable public State getState() { return getOnMaster(IMultiblockBEHelperMaster::getState); }

    @Override @Nullable public IMultiblockContext<State> getContext() { return getOnMaster(IMultiblockBEHelperMaster::getContext); }

    @Nullable @Override protected IMultiblockBEHelperMaster<State> getMasterHelperWithChunkloads() { return getMasterHelper(level.forciblyGetBlockEntity(multiblock.masterPosInMB())); }

    @Override public void load(CompoundTag tag, Provider provider) {
        if (tag.contains("posInMultiblock", Tag.TAG_COMPOUND)) {
            final CompoundTag legacyPos = tag.getCompound("posInMultiblock");
            this.positionInMB = new BlockPos(legacyPos.getInt("X"), legacyPos.getInt("Y"), legacyPos.getInt("Z"));
        }
        else { this.positionInMB = NbtUtils.readBlockPos(tag, "posInMB").orElseThrow(); }
    }

    @Override public void saveAdditional(CompoundTag tag, Provider provider) { tag.put("posInMB", NbtUtils.writeBlockPos(this.positionInMB)); }

    @Override public CompoundTag getUpdateTag(Provider provider) {
        CompoundTag result = new CompoundTag();
        saveAdditional(result, provider);
        return result;
    }

    @Override public void handleUpdateTag(CompoundTag tag, Provider provider) { load(tag, provider); }

    @Nullable private <T> T getOnMaster(Function<IMultiblockBEHelperMaster<State>, T> get) {
        final IMultiblockBEHelperMaster<State> master = getIMasterHelper();
        if (master != null) { return get.apply(master); }
        else { return null; }
    }

    @Nullable @Override protected MultiblockBEHelperMaster<State> getMasterHelper() { return getIMasterHelper() instanceof MultiblockBEHelperMaster<State> helper ? helper : null; }

    @Nullable protected IMultiblockBEHelperMaster<State> getIMasterHelper() { return getMasterHelper(level.getBlockEntity(multiblock.masterPosInMB())); }

    @SuppressWarnings("unchecked") @Nullable private IMultiblockBEHelperMaster<State> getMasterHelper(BlockEntity beAtMasterPos) {
        if (!(beAtMasterPos instanceof MultiblockBlockEntityMaster<?> masterBE)) { return null; }
        final IMultiblockBEHelperMaster<?> masterHelper = masterBE.getHelper();
        if (masterHelper.getMultiblock() == multiblock) { return (IMultiblockBEHelperMaster<State>)masterHelper; }
        else { return null; }
    }

    @Override public void setPositionInMB(BlockPos pos) {
        Preconditions.checkArgument(!multiblock.masterPosInMB().equals(pos));
        this.positionInMB = pos;
        this.be.setChanged();
    }

    @Override public BlockPos getPositionInMB() { return positionInMB; }
}
