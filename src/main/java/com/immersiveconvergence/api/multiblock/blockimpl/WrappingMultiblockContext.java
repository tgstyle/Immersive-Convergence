package com.immersiveconvergence.api.multiblock.blockimpl;

import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockLevel;
import com.immersiveconvergence.api.multiblock.blocks.util.RelativeBlockFace;

import net.minecraft.core.BlockPos;
import java.util.function.BooleanSupplier;

public record WrappingMultiblockContext<State>(
        IMultiblockContext<?> inner, State ownState
) implements IMultiblockContext<State> {
    @Override public void markMasterDirty() { inner.markMasterDirty(); }

    @Override public State getState() { return ownState; }

    @Override public IMultiblockLevel getLevel() { return inner.getLevel(); }

    @Override public BooleanSupplier isValid() { return inner.isValid(); }

    @Override public void requestMasterBESync() { inner.requestMasterBESync(); }

    @Override public void setComparatorOutputFor(BlockPos posInMultiblock, int newValue) { inner.setComparatorOutputFor(posInMultiblock, newValue); }

    @Override public int getRedstoneInputValue(BlockPos posInMultiblock, RelativeBlockFace side, int fallback) { return inner.getRedstoneInputValue(posInMultiblock, side, fallback); }

    @Override public int getRedstoneInputValue(BlockPos posInMultiblock, int fallback) { return inner.getRedstoneInputValue(posInMultiblock, fallback); }
}
