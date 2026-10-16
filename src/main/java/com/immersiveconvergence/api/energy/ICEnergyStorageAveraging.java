package com.immersiveconvergence.api.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import javax.annotation.Nullable;

public class ICEnergyStorageAveraging extends ICEnergyStorage {
    private static final double AVERAGE_DECAY_FACTOR = .5;
    private int averageInsertion = 0;
    private int averageExtraction = 0;
    private int tickInsert = 0;
    private int tickExtract = 0;

    public ICEnergyStorageAveraging(int capacity) { super(capacity); }

    public ICEnergyStorageAveraging(int capacity, int maxIO) { super(capacity, maxIO); }

    public void updateAverage() {
        averageInsertion = (int)Math.round(averageInsertion * AVERAGE_DECAY_FACTOR + tickInsert * (1 - AVERAGE_DECAY_FACTOR));
        averageExtraction = (int)Math.round(averageExtraction * AVERAGE_DECAY_FACTOR + tickExtract * (1 - AVERAGE_DECAY_FACTOR));
        tickInsert = tickExtract = 0;
    }

    @Override public int receiveEnergy(int energy, boolean simulate) {
        int received = super.receiveEnergy(energy, simulate);
        if (!simulate) { tickInsert += received; }
        return received;
    }

    @Override public int extractEnergy(int energy, boolean simulate) {
        int extracted = super.extractEnergy(energy, simulate);
        if (!simulate) { tickExtract += extracted; }
        return extracted;
    }

    public int getAverageInsertion() { return averageInsertion; }

    public int getAverageExtraction() { return averageExtraction; }

    public void save(CompoundTag tag, String key) { tag.put(key, toTag()); }

    public void saveStored(CompoundTag tag, String key) { tag.putInt(key, energy); }

    public void load(CompoundTag tag, String key) { fromTag(tag.get(key)); }

    @Override public Tag serializeNBT() { return toTag(); }

    @Override public void deserializeNBT(Tag nbt) { fromTag(nbt); }

    private CompoundTag toTag() {
        CompoundTag compound = new CompoundTag();
        compound.putInt("energy", energy);
        compound.putInt("averageInsertion", averageInsertion);
        compound.putInt("averageExtraction", averageExtraction);
        return compound;
    }

    private void fromTag(@Nullable Tag nbt) {
        if (nbt instanceof CompoundTag compound) {
            super.setStoredEnergy(compound.getInt("energy"));
            this.averageInsertion = compound.getInt("averageInsertion");
            this.averageExtraction = compound.getInt("averageExtraction");
        }
        else if (nbt instanceof NumericTag stored) { super.setStoredEnergy(stored.getAsInt()); }
    }
}
