package com.immersiveconvergence.api.energy;

import com.immersiveconvergence.api.block.ICTileEntityBase;
import com.immersiveconvergence.common.energy.IEWireBridge;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public abstract class ICTileEntityConnectable extends ICTileEntityBase {

    protected ICWireType limitType = null;

    protected boolean canTakeLV() { return false; }

    protected boolean canTakeMV() { return false; }

    protected boolean canTakeHV() { return false; }

    protected boolean isRelay() { return false; }

    public void onEnergyPassthrough(int amount) {}

    public boolean allowEnergyToPass() { return true; }

    public boolean canConnect() { return true; }

    public boolean isEnergyOutput() { return false; }

    public int outputEnergy(int amount, boolean simulate, int energyType) { return 0; }

    public BlockPos connectionMaster() { return getPos(); }

    @Nonnull public abstract Vec3d connectionOffset(@Nonnull ICWireType cable, @Nullable BlockPos otherEnd);

    @Nonnull public Vec3d connectionOffset(@Nonnull ICWireType cable, @Nonnull ICTargetingInfo target) { return connectionOffset(cable, (BlockPos)null); }

    @Nullable public Boolean canConnectCable(@Nonnull ICWireType cable, @Nonnull ICTargetingInfo target, @Nonnull Vec3i offset) { return null; }

    public boolean acceptsCable(@Nonnull ICWireType cable) {
        String category = cable.getCategory();
        boolean accepting = (ICWireType.HV_CATEGORY.equals(category) && canTakeHV())
                || (ICWireType.MV_CATEGORY.equals(category) && canTakeMV())
                || (ICWireType.LV_CATEGORY.equals(category) && canTakeLV());
        if (!accepting) { return false; }
        return limitType == null || (isRelay() && ICWireType.canMix(limitType, cable));
    }

    public boolean connectCable(@Nonnull ICWireType cable, @Nonnull ICTargetingInfo target, BlockPos otherMaster) { return false; }

    public void acceptCable(@Nonnull ICWireType cable) { this.limitType = cable; }

    @Nullable public ICWireType cableLimiter(@Nonnull ICTargetingInfo target) { return limitType; }

    public boolean removeCable(@Nullable BlockPos otherEnd, boolean all) { return false; }

    @Nullable public java.util.Set<?> wireConnections() { return null; }

    public void forgetCable(@Nullable ICWireType type) {
        if (IEWireBridge.connectionCount(world, pos) == 0 && (type == limitType || type == null)) { this.limitType = null; }
        markDirty();
        if (world != null) { markContainingBlockForUpdate(null); }
    }

    @Override public void readCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) {
        limitType = ICWireType.readFromNBT(nbt, "limitType");
    }

    @Override public void writeCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) {
        if (limitType != null) { limitType.writeToNBT(nbt, "limitType"); }
    }
}
