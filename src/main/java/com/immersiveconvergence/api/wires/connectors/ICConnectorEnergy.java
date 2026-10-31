package com.immersiveconvergence.api.wires.connectors;

import com.immersiveconvergence.api.multiblock.util.SafeChunkUtils;
import com.immersiveconvergence.api.wires.ICWires;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import javax.annotation.Nullable;

public final class ICConnectorEnergy implements IEnergyStorage {
    private static final String KEY = "energy";
    private final ICConnectorBlockEntity owner;
    private final int rate;
    private int stored;
    private int output;
    private long outputTime = -1;

    public ICConnectorEnergy(ICConnectorBlockEntity owner, int rate) {
        this.owner = owner;
        this.rate = rate;
    }

    public void update() {
        if (stored > 0) { ICWires.energy(owner.getLevelNonnull()).offer(owner.points().iterator().next(), null, () -> stored, this::moved); }
    }

    public boolean canOutput(Direction facing) {
        IEnergyStorage outside = outside(facing);
        return outside != null && outside.canReceive();
    }

    public int output(Direction facing, int amount, boolean simulate) {
        long now = owner.getLevelNonnull().getGameTime();
        if (now != outputTime) {
            outputTime = now;
            output = 0;
        }
        int room = Math.min(rate - output, amount);
        if (room <= 0) { return 0; }
        IEnergyStorage outside = outside(facing);
        int sent = outside == null ? 0 : outside.receiveEnergy(room, simulate);
        if (!simulate) { output += sent; }
        return sent;
    }

    public void read(CompoundTag nbt) { stored = Mth.clamp(nbt.getInt(KEY), 0, rate); }

    public void write(CompoundTag nbt) { nbt.putInt(KEY, stored); }

    @Override public int receiveEnergy(int maxReceive, boolean simulate) {
        if (owner.getLevelNonnull().isClientSide) { return 0; }
        int accepted = Mth.clamp(maxReceive, 0, rate - stored);
        if (!simulate && accepted > 0) {
            stored += accepted;
            owner.setChanged();
        }
        return accepted;
    }

    @Override public int extractEnergy(int maxExtract, boolean simulate) { return 0; }

    @Override public int getEnergyStored() { return stored; }

    @Override public int getMaxEnergyStored() { return rate; }

    @Override public boolean canExtract() { return false; }

    @Override public boolean canReceive() { return true; }

    void moved(int amount) {
        stored = Math.max(0, stored - amount);
        owner.setChanged();
    }

    @Nullable private IEnergyStorage outside(Direction facing) {
        Level level = owner.getLevelNonnull();
        BlockPos neighbor = owner.getBlockPos().relative(facing);
        return SafeChunkUtils.isChunkUnsafe(level, neighbor) ? null : level.getCapability(Capabilities.EnergyStorage.BLOCK, neighbor, facing.getOpposite());
    }
}
