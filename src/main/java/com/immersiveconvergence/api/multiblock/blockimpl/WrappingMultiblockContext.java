package com.immersiveconvergence.api.multiblock.blockimpl;

import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockContext;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockLevel;
import com.immersiveconvergence.api.multiblock.blocks.util.RelativeBlockFace;
import com.immersiveconvergence.api.multiblock.util.CapabilityReference;

import net.minecraft.core.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import java.util.function.BooleanSupplier;

public record WrappingMultiblockContext<State>(IMultiblockContext<?> inner, State ownState) implements IMultiblockContext<State> {
    @Override public <T> CapabilityReference<T> getCapabilityAt(Capability<T> capability, BlockPos posRelativeToMB, RelativeBlockFace face) { return inner.getCapabilityAt(capability, posRelativeToMB, face); }

    @Override public void markMasterDirty() { inner.markMasterDirty(); }

    @Override public State getState() { return ownState; }

    @Override public IMultiblockLevel getLevel() { return inner.getLevel(); }

    @Override public <T> LazyOptional<T> registerCapability(T value) { return inner.registerCapability(value); }

    @Override public BooleanSupplier isValid() { return inner.isValid(); }

    @Override public void requestMasterBESync() { inner.requestMasterBESync(); }

    @Override public void setComparatorOutputFor(BlockPos posInMultiblock, int newValue) { inner.setComparatorOutputFor(posInMultiblock, newValue); }

    @Override public int getRedstoneInputValue(BlockPos posInMultiblock, int fallback) { return inner.getRedstoneInputValue(posInMultiblock, fallback); }
}
