package com.immersiveconvergence.api.energy;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import javax.annotation.Nonnull;

@SuppressWarnings("unused") public class ICEnergyStorageAveraging extends ICEnergyStorage {
    private static final double AVERAGE_DECAY_FACTOR = .5;
    private int averageInsertion = 0;
    private int averageExtraction = 0;
    private int tickInsert = 0;
    private int tickExtract = 0;

    public ICEnergyStorageAveraging(int capacity) { super(capacity); }

    public void updateAverage() {
        averageInsertion = (int)Math.round(averageInsertion * AVERAGE_DECAY_FACTOR + tickInsert * (1 - AVERAGE_DECAY_FACTOR));
        averageExtraction = (int)Math.round(averageExtraction * AVERAGE_DECAY_FACTOR + tickExtract * (1 - AVERAGE_DECAY_FACTOR));
        tickInsert = tickExtract = 0;
    }

    @Override public int receiveEnergy(int energy, boolean simulate) {
        int received = super.receiveEnergy(energy, simulate);
        if (!simulate) { tickInsert += energy; }
        return received;
    }

    @Override public int extractEnergy(int energy, boolean simulate) {
        int extracted = super.extractEnergy(energy, simulate);
        if (!simulate) { tickExtract += energy; }
        return extracted;
    }

    public int getAverageInsertion() { return averageInsertion; }

    public int getAverageExtraction() { return averageExtraction; }

    @Override @Nonnull public Tag serializeNBT(@Nonnull Provider provider) {
        CompoundTag compound = new CompoundTag();
        compound.putInt("energy", energy);
        compound.putInt("averageInsertion", averageInsertion);
        compound.putInt("averageExtraction", averageExtraction);
        return compound;
    }

    @Override public void deserializeNBT(@Nonnull Provider provider, @Nonnull Tag nbt) {
        if (!(nbt instanceof CompoundTag compound)) { return; }
        this.energy = compound.getInt("energy");
        this.averageInsertion = compound.getInt("averageInsertion");
        this.averageExtraction = compound.getInt("averageExtraction");
    }
}
