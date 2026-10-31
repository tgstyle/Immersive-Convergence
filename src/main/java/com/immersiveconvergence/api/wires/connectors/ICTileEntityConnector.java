package com.immersiveconvergence.api.wires.connectors;

import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IBlockBounds;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IDirectionalTile;
import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWireType;
import com.immersiveconvergence.api.wires.IICEnergyConnector;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ICTileEntityConnector extends ICTileEntityConnectable implements IICEnergyConnector, IDirectionalTile, IBlockBounds {
    private static final String FACING = "facing";
    protected static final float WIDTH_MIN = 0.3125F;
    protected static final float WIDTH_MAX = 0.6875F;
    private final ICConnectorTier tier;
    private final boolean relay;
    @Nullable private final ICConnectorEnergy energy;
    private EnumFacing facing = EnumFacing.DOWN;

    protected ICTileEntityConnector(ICConnectorTier tier, boolean relay) {
        this.tier = tier;
        this.relay = relay;
        this.energy = relay || tier.getRate() == 0 ? null : new ICConnectorEnergy(this, tier.getRate());
    }

    @Override protected boolean isRelay() { return relay; }

    protected final void offerBuffer() {
        if (energy != null && !world.isRemote) { energy.update(); }
    }

    @Override public boolean accepts(@Nonnull ICConnectionPoint point, @Nonnull ICWireType type) { return tier.getCategory() == type.getCategory() && limitAllows(type); }

    @Override public int demand(@Nonnull ICConnectionPoint point) { return energy == null ? 0 : energy.output(facing, Integer.MAX_VALUE, true); }

    @Override public int accept(@Nonnull ICConnectionPoint point, int amount, boolean simulate) { return energy == null ? 0 : energy.output(facing, amount, simulate); }

    @Override public void onPassthrough(@Nonnull ICConnectionPoint point, int amount) {}

    @Override @Nonnull public Vec3d offset(@Nonnull ICConnectionPoint point, @Nonnull ICWireType type) { return offset(tier.inset(relay) - type.getRenderDiameter() / 2); }

    @Override public float[] getBlockBounds() { return bounds(0.5F + (float)tier.inset(relay), WIDTH_MIN, WIDTH_MAX); }

    protected final Vec3d offset(double reach) {
        EnumFacing side = facing.getOpposite();
        return new Vec3d(0.5 + side.getXOffset() * reach, 0.5 + side.getYOffset() * reach, 0.5 + side.getZOffset() * reach);
    }

    protected final float[] bounds(float length, float min, float max) {
        switch (facing) {
            case DOWN: return new float[] {min, 0, min, max, length, max};
            case UP: return new float[] {min, 1 - length, min, max, 1, max};
            case NORTH: return new float[] {min, min, 0, max, max, length};
            case SOUTH: return new float[] {min, min, 1 - length, max, max, 1};
            case WEST: return new float[] {0, min, min, length, max, max};
            default: return new float[] {1 - length, min, min, 1, max, max};
        }
    }

    @Override public EnumFacing getFacing() { return facing; }

    @Override public void setFacing(EnumFacing facing) { this.facing = facing; }

    @Override public int getFacingLimitation() { return 0; }

    @Override public boolean mirrorFacingOnPlacement(EntityLivingBase placer) { return true; }

    @Override public boolean canHammerRotate(EnumFacing side, float hitX, float hitY, float hitZ, EntityLivingBase entity) { return false; }

    @Override public boolean cannotRotate(EnumFacing axis) { return true; }

    @Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing side) {
        if (capability == CapabilityEnergy.ENERGY) { return energy != null && side == facing; }
        return super.hasCapability(capability, side);
    }

    @Override @Nullable public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing side) {
        if (capability == CapabilityEnergy.ENERGY) { return energy != null && side == facing ? CapabilityEnergy.ENERGY.cast(energy) : null; }
        return super.getCapability(capability, side);
    }

    @Override public void readCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        facing = EnumFacing.byIndex(nbt.getInteger(FACING));
        if (energy != null) { energy.read(nbt); }
    }

    @Override public void writeCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.setInteger(FACING, facing.getIndex());
        if (energy != null) { energy.write(nbt); }
    }
}
