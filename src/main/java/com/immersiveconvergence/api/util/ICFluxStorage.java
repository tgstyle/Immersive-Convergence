package com.immersiveconvergence.api.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.IEnergyStorage;

@SuppressWarnings("unused")
public class ICFluxStorage implements IEnergyStorage {
    protected int energy;
    protected int capacity;
    protected int limitReceive;
    protected int limitExtract;
    public boolean canExtract = true, canReceive = true;

    public ICFluxStorage(int capacity, int limitReceive, int limitExtract) {
        this.capacity = capacity;
        this.limitReceive = limitReceive;
        this.limitExtract = limitExtract;
    }

    public ICFluxStorage(int capacity, int limitTransfer) { this(capacity, limitTransfer, limitTransfer); }

    public ICFluxStorage(int capacity) { this(capacity, capacity, capacity); }

    public ICFluxStorage(int capacity, boolean canExtract, boolean canReceive) {
        this(capacity);
        this.canExtract = canExtract;
        this.canReceive = canReceive;
        if (!canExtract) { limitExtract = 0; }
        if (!canReceive) { limitReceive = 0; }
    }

    public ICFluxStorage readFromNBT(NBTTagCompound nbt) {
        this.energy = nbt.getInteger("ifluxEnergy");
        if (energy > capacity) { energy = capacity; }
        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if (energy < 0) { energy = 0; }
        nbt.setInteger("ifluxEnergy", energy);
        return nbt;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        if (energy > capacity) { energy = capacity; }
    }

    public void setLimitTransfer(int limitTransfer) {
        setLimitReceive(limitTransfer);
        setMaxExtract(limitTransfer);
    }

    public void setLimitReceive(int limitReceive) { this.limitReceive = limitReceive; }

    public void setMaxExtract(int limitExtract) { this.limitExtract = limitExtract; }

    public int getLimitReceive() { return limitReceive; }

    public int getLimitExtract() { return limitExtract; }

    public void setEnergy(int energy) {
        this.energy = energy;
        if (this.energy > capacity) { this.energy = capacity; }
        else if (this.energy < 0) { this.energy = 0; }
    }

    public void modifyEnergyStored(int energy) {
        this.energy += energy;
        if (this.energy > capacity) { this.energy = capacity; }
        else if (this.energy < 0) { this.energy = 0; }
    }

    @Override public int receiveEnergy(int energy, boolean simulate) {
        int received = Math.min(capacity - this.energy, Math.min(this.limitReceive, energy));
        if (!simulate) { this.energy += received; }
        return received;
    }

    @Override public int extractEnergy(int energy, boolean simulate) {
        int extracted = Math.min(this.energy, Math.min(this.limitExtract, energy));
        if (!simulate) { this.energy -= extracted; }
        return extracted;
    }

    @Override public int getEnergyStored() { return energy; }

    @Override public int getMaxEnergyStored() { return capacity; }

    @Override public boolean canExtract() { return canExtract; }

    @Override public boolean canReceive() { return canReceive; }
}
