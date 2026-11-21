package com.immersiveconvergence.common.blocks.tileentities;

import com.immersiveconvergence.api.block.ICSideConfig;
import com.immersiveconvergence.api.block.ICTileEntityBase;
import com.immersiveconvergence.api.energy.ICFluxWrapper;
import com.immersiveconvergence.api.energy.IICFluxHandler;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IConfigurableSides;
import com.immersiveconvergence.api.util.ICFluxStorage;
import com.immersiveconvergence.api.util.ICUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class TileEntityCreativeEnergySource extends ICTileEntityBase implements ITickable, IICFluxHandler, IConfigurableSides {
    private final ICSideConfig[] sides = new ICSideConfig[EnumFacing.VALUES.length];
    private final ICFluxStorage storage = new ICFluxStorage(Integer.MAX_VALUE);
    private final ICFluxWrapper[] wrappers = ICFluxWrapper.getDefaultWrapperArray(this);

    public TileEntityCreativeEnergySource() { Arrays.fill(sides, ICSideConfig.OUTPUT); }

    @Override public void update() {
        if (world.isRemote) { return; }
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (sides[facing.getIndex()] == ICSideConfig.OUTPUT) { ICUtils.insertFlux(ICUtils.getExistingTileEntity(world, pos.offset(facing)), facing.getOpposite(), Integer.MAX_VALUE, false); }
        }
    }

    @Override public ICSideConfig sideConfig(int side) { return sides[side]; }

    @Override public boolean toggleSide(int side, EntityPlayer player) {
        sides[side] = ICSideConfig.next(sides[side]);
        markDirty();
        markContainingBlockForUpdate(null);
        world.addBlockEvent(getPos(), getBlockType(), 0, 0);
        return true;
    }

    @Override @Nonnull public ICSideConfig getEnergySideConfig(@Nullable EnumFacing facing) { return facing == null ? ICSideConfig.NONE : sides[facing.getIndex()]; }

    @Override @Nonnull public ICFluxStorage getFluxStorage() { return storage; }

    @Override @Nullable public ICFluxWrapper getCapabilityWrapper(@Nullable EnumFacing facing) { return facing == null || sides[facing.getIndex()] == ICSideConfig.NONE ? null : wrappers[facing.getIndex()]; }

    @Override public int extractEnergy(@Nullable EnumFacing facing, int amount, boolean simulate) { return world.isRemote || getEnergySideConfig(facing) != ICSideConfig.OUTPUT ? 0 : amount; }

    @Override public int receiveEnergy(@Nullable EnumFacing facing, int amount, boolean simulate) { return world.isRemote || getEnergySideConfig(facing) != ICSideConfig.INPUT ? 0 : amount; }

    @Override public int getEnergyStored(@Nullable EnumFacing facing) { return Integer.MAX_VALUE; }

    @Override public int getMaxEnergyStored(@Nullable EnumFacing facing) { return Integer.MAX_VALUE; }

    @Override public void readCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) {
        for (int i = 0; i < sides.length; i++) {
            if (nbt.hasKey("sideConfig_" + i)) { sides[i] = ICSideConfig.values()[MathHelper.clamp(nbt.getInteger("sideConfig_" + i), 0, ICSideConfig.values().length - 1)]; }
        }
    }

    @Override public void writeCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) {
        for (int i = 0; i < sides.length; i++) { nbt.setInteger("sideConfig_" + i, sides[i].ordinal()); }
    }
}
