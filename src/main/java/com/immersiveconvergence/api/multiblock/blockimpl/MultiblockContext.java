package com.immersiveconvergence.api.multiblock.blockimpl;

import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockLevel;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockBE;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.util.RelativeBlockFace;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.function.BooleanSupplier;

public record MultiblockContext<State extends IMultiblockState>(
        MultiblockBEHelperMaster<State> masterHelper,
        MultiblockRegistration<State> multiblock,
        MultiblockLevel level
) implements IMultiblockContext<State> {
    @Override public State getState() { return masterHelper.getState(); }

    @Override public void markMasterDirty() { masterHelper.getMasterBE().setChanged(); }

    @Override public IMultiblockLevel getLevel() { return level; }

    @Override public BooleanSupplier isValid() {
        final BlockEntity masterBE = masterHelper.getMasterBE();
        return () -> !masterBE.isRemoved();
    }

    @Override public void requestMasterBESync() { requestBESync(this.masterHelper.getMasterBE()); }

    static void requestBESync(BlockEntity be) {
        final Level level = be.getLevel();
        if (level != null && level.getChunkSource() instanceof ServerChunkCache chunkCache) { chunkCache.blockChanged(be.getBlockPos()); }
    }

    @Override public void setComparatorOutputFor(BlockPos posInMultiblock, int newValue) {
        Preconditions.checkState(masterHelper.multiblock.hasComparatorOutput());
        final int oldValue = masterHelper.getCurrentComparatorOutputs().put(posInMultiblock, newValue);
        if (oldValue != newValue) { level.updateNeighbourForOutputSignal(posInMultiblock); }
    }

    @Override public int getRedstoneInputValue(BlockPos posInMultiblock, RelativeBlockFace side, int fallback) {
        Preconditions.checkState(masterHelper.multiblock.redstoneInputAware());
        if (level.getBlockEntity(posInMultiblock) instanceof IMultiblockBE<?> beAtPos) { return beAtPos.getHelper().getRedstoneInput(side); }
        else { return fallback; }
    }

    @Override public int getRedstoneInputValue(BlockPos posInMultiblock, int fallback) {
        Preconditions.checkState(masterHelper.multiblock.redstoneInputAware());
        if (!(level.getBlockEntity(posInMultiblock) instanceof IMultiblockBE<?> beAtPos)) { return fallback; }
        int result = 0;
        for (final RelativeBlockFace face : RelativeBlockFace.values()) { result = Math.max(result, beAtPos.getHelper().getRedstoneInput(face)); }
        return result;
    }
}
