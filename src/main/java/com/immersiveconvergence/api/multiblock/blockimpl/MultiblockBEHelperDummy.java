package com.immersiveconvergence.api.multiblock.blockimpl;

import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelperDummy;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelperMaster;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.registry.MultiblockBlockEntityMaster;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import javax.annotation.Nullable;
import java.util.function.Function;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MultiblockBEHelperDummy<State extends IMultiblockState> extends MultiblockBEHelperCommon<State> implements IMultiblockBEHelperDummy<State> {
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

    @Override public void load(CompoundTag tag) {
        final String key = tag.contains("posInMultiblock", Tag.TAG_COMPOUND) ? "posInMultiblock" : "posInMB";
        this.positionInMB = NbtUtils.readBlockPos(tag.getCompound(key));
    }

    @Override public void saveAdditional(CompoundTag tag) { tag.put("posInMB", NbtUtils.writeBlockPos(this.positionInMB)); }

    @Override public CompoundTag getUpdateTag() {
        CompoundTag result = new CompoundTag();
        saveAdditional(result);
        return result;
    }

    @Override public void handleUpdateTag(CompoundTag tag) { load(tag); }

    @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this.be); }

    @Override public void onDataPacket(CompoundTag tag) { load(tag); }

    @Override public MultiblockRegistration<State> getMultiblock() { return multiblock; }

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
