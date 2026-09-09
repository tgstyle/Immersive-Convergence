package com.immersiveconvergence.api.util;

@SuppressWarnings("unused")
public class ICFluxStorageAdvanced extends ICFluxStorage {
    private int averageInsertion = 0;
    private int averageExtraction = 0;
    private double averageDecayFactor = .5;

    public ICFluxStorageAdvanced(int capacity, int limitReceive, int limitExtract) { super(capacity, limitReceive, limitExtract); }
    public ICFluxStorageAdvanced(int capacity, int limitTransfer) { super(capacity, limitTransfer); }
    public ICFluxStorageAdvanced(int capacity) { super(capacity); }

    @Override public int receiveEnergy(int energy, boolean simulate) {
        int received = super.receiveEnergy(energy, simulate);
        if (!simulate) { averageInsertion = (int)Math.round(averageInsertion * averageDecayFactor + received * (1 - averageDecayFactor)); }
        return received;
    }

    @Override public int extractEnergy(int energy, boolean simulate) {
        int extracted = super.extractEnergy(energy, simulate);
        if (!simulate) { averageExtraction = (int)Math.round(averageExtraction * averageDecayFactor + extracted * (1 - averageDecayFactor)); }
        return extracted;
    }

    public int getAverageInsertion() { return averageInsertion; }

    public int getAverageExtraction() { return averageExtraction; }

    public ICFluxStorageAdvanced setDecayFactor(double factor) {
        this.averageDecayFactor = factor;
        return this;
    }
}
