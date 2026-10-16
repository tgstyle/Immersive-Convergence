package com.immersiveconvergence.api.multiblock.blocks.env;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import java.util.function.BooleanSupplier;

@NonExtendable public interface IMultiblockContext<State> {
    default void markDirtyAndSync() {
        markMasterDirty();
        requestMasterBESync();
    }

    void markMasterDirty();

    State getState();

    IMultiblockLevel getLevel();

    BooleanSupplier isValid();

    void requestMasterBESync();

    void setComparatorOutputFor(BlockPos posInMultiblock, int newValue);

    default boolean updateComparatorOutputs(int oldValue, int newValue, Iterable<BlockPos> positions) {
        if (newValue == oldValue) { return false; }
        for (BlockPos pos : positions) { setComparatorOutputFor(pos, newValue); }
        return true;
    }

    int getRedstoneInputValue(BlockPos posInMultiblock, int fallback);
}
