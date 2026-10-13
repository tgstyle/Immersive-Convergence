package com.immersiveconvergence.core.compat;

import com.immersiveconvergence.api.energy.ICEnergyStorageAveraging;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.Tag;
import javax.annotation.Nonnull;

public class IEEnergyStorageView extends AveragingEnergyStorage {
    private final ICEnergyStorageAveraging storage;

    public IEEnergyStorageView(ICEnergyStorageAveraging storage) {
        super(0);
        this.storage = storage;
    }

    @Override public void updateAverage() { storage.updateAverage(); }

    @Override public int receiveEnergy(int energy, boolean simulate) { return storage.receiveEnergy(energy, simulate); }

    @Override public int extractEnergy(int energy, boolean simulate) { return storage.extractEnergy(energy, simulate); }

    @Override public int getEnergyStored() { return storage.getEnergyStored(); }

    @Override public int getMaxEnergyStored() { return storage.getMaxEnergyStored(); }

    @Override public boolean canExtract() { return storage.canExtract(); }

    @Override public boolean canReceive() { return storage.canReceive(); }

    @Override public void setStoredEnergy(int stored) { storage.setStoredEnergy(stored); }

    @Override public int getAverageInsertion() { return storage.getAverageInsertion(); }

    @Override public int getAverageExtraction() { return storage.getAverageExtraction(); }

    @Override @Nonnull public Tag serializeNBT(@Nonnull Provider provider) { return storage.serializeNBT(provider); }

    @Override public void deserializeNBT(@Nonnull Provider provider, @Nonnull Tag nbt) { storage.deserializeNBT(provider, nbt); }
}
