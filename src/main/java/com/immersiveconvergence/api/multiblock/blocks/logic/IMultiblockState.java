package com.immersiveconvergence.api.multiblock.blocks.logic;

import net.minecraft.nbt.CompoundTag;

@SuppressWarnings({"unused", "RedundantSuppression"}) public interface IMultiblockState {
    void writeSaveNBT(CompoundTag nbt);

    default void writeSyncNBT(CompoundTag nbt) {
    }

    void readSaveNBT(CompoundTag nbt);

    default void readSyncNBT(CompoundTag nbt) {
    }
}
