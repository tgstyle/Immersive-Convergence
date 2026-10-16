package com.immersiveconvergence.api.multiblock.blocks.logic;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;

public interface IMultiblockState {
    void writeSaveNBT(CompoundTag nbt, Provider provider);

    default void writeSyncNBT(CompoundTag nbt, Provider provider) {}

    void readSaveNBT(CompoundTag nbt, Provider provider);

    default void readSyncNBT(CompoundTag nbt, Provider provider) {}
}
