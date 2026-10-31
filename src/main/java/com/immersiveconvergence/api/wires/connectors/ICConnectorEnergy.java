package com.immersiveconvergence.api.wires.connectors;

import com.immersiveconvergence.api.energy.IICFluxAcceptor;
import com.immersiveconvergence.api.util.ICUtils;
import com.immersiveconvergence.api.wires.ICWires;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public final class ICConnectorEnergy implements IEnergyStorage {
    private static final String KEY = "energy";
    private final ICTileEntityConnector owner;
    private final int rate;
    private int stored;
    private int output;
    private long outputTime = -1;

    public ICConnectorEnergy(ICTileEntityConnector owner, int rate) {
        this.owner = owner;
        this.rate = rate;
    }

    public void update() {
        if (stored > 0) { ICWires.energy(owner.getWorld()).offer(owner.points().iterator().next(), null, () -> stored, this::moved); }
    }

    private boolean canOutput(EnumFacing facing) {
        TileEntity tile = neighbor(facing);
        if (tile instanceof IICFluxAcceptor) { return ((IICFluxAcceptor)tile).canConnectEnergy(facing.getOpposite()); }
        IEnergyStorage outside = outside(facing);
        return outside != null && outside.canReceive();
    }

    public int output(EnumFacing facing, int amount, boolean simulate) {
        long now = owner.getWorld().getTotalWorldTime();
        if (now != outputTime) {
            outputTime = now;
            output = 0;
        }
        int room = Math.min(rate - output, amount);
        if (room <= 0 || !canOutput(facing)) { return 0; }
        int sent = ICUtils.insertFlux(neighbor(facing), facing.getOpposite(), room, simulate);
        if (!simulate) { output += sent; }
        return sent;
    }

    public void read(NBTTagCompound nbt) { stored = Math.max(0, Math.min(rate, nbt.getInteger(KEY))); }

    public void write(NBTTagCompound nbt) { nbt.setInteger(KEY, stored); }

    @Override public int receiveEnergy(int maxReceive, boolean simulate) {
        if (owner.getWorld().isRemote) { return 0; }
        int accepted = Math.max(0, Math.min(rate - stored, maxReceive));
        if (!simulate && accepted > 0) {
            stored += accepted;
            owner.markDirty();
        }
        return accepted;
    }

    @Override public int extractEnergy(int maxExtract, boolean simulate) { return 0; }

    @Override public int getEnergyStored() { return stored; }

    @Override public int getMaxEnergyStored() { return rate; }

    @Override public boolean canExtract() { return false; }

    @Override public boolean canReceive() { return true; }

    private void moved(int amount) {
        stored = Math.max(0, stored - amount);
        owner.markDirty();
    }

    @Nullable private TileEntity neighbor(EnumFacing facing) { return ICUtils.getExistingTileEntity(owner.getWorld(), owner.getPos().offset(facing)); }

    @Nullable private IEnergyStorage outside(EnumFacing facing) {
        TileEntity tile = neighbor(facing);
        return tile == null ? null : tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
    }
}
