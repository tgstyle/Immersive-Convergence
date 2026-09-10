package com.immersiveconvergence.api.energy;

import com.immersiveconvergence.api.client.IICOBJModelCallback;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IBlockBounds;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IBlockOverlayText;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IDirectionalTile;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IHammerInteraction;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IRedstoneOutput;

import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class ICTileEntityConnectorRedstone extends ICTileEntityConnectable
        implements ITickable, IDirectionalTile, IRedstoneOutput, IHammerInteraction, IBlockBounds, IBlockOverlayText, IICOBJModelCallback<IBlockState> {

    public EnumFacing facing = EnumFacing.DOWN;
    public int ioMode = 0;
    public int redstoneChannel = 0;
    public boolean rsDirty = false;

    protected int outputClient = -1;
    private boolean refreshNetwork = false;

    public int networkPower(int channel) { return 0; }

    public void networkUpdateValues() {}

    protected void networkDetachAll() {}

    protected void networkLeave() {}

    protected void networkConnectorsUpdated() {}

    public void resetNetworkRefresh() { refreshNetwork = false; }

    @Override public void update() {
        if (hasWorld() && !world.isRemote && !refreshNetwork) {
            refreshNetwork = true;
            networkDetachAll();
        }
        if (hasWorld() && !world.isRemote && rsDirty) { networkUpdateValues(); }
    }

    @Override public int getStrongRSOutput(@Nonnull IBlockState state, @Nonnull EnumFacing side) {
        if (!isRSOutput() || side != this.facing.getOpposite()) { return 0; }
        if (world.isRemote) { return outputClient; }
        return networkPower(redstoneChannel);
    }

    @Override public int getWeakRSOutput(@Nonnull IBlockState state, @Nonnull EnumFacing side) {
        if (!isRSOutput()) { return 0; }
        if (world.isRemote) { return outputClient; }
        return networkPower(redstoneChannel);
    }

    @Override public boolean canConnectRedstone(@Nonnull IBlockState state, @Nonnull EnumFacing side) { return true; }

    public void onChange() {
        if (!isInvalid() && isRSOutput()) {
            markDirty();
            IBlockState stateHere = world.getBlockState(pos);
            markContainingBlockForUpdate(stateHere);
            markBlockForUpdate(pos.offset(facing), stateHere);
        }
    }

    public boolean isRSInput() { return ioMode == 0; }

    public boolean isRSOutput() { return ioMode == 1; }

    public void updateInput(@Nonnull byte[] signals) {
        if (isRSInput()) { signals[redstoneChannel] = (byte)Math.max(getLocalRS(), signals[redstoneChannel]); }
        rsDirty = false;
    }

    protected int getLocalRS() {
        int val = world.getRedstonePowerFromNeighbors(pos);
        if (val == 0) {
            for (EnumFacing f : EnumFacing.HORIZONTALS) {
                IBlockState state = world.getBlockState(pos.offset(f));
                if (state.getBlock() == Blocks.REDSTONE_WIRE && state.getValue(BlockRedstoneWire.POWER) > val) { val = state.getValue(BlockRedstoneWire.POWER); }
            }
        }
        return val;
    }

    @Override public boolean hammerUseSide(@Nonnull EnumFacing side, @Nonnull EntityPlayer player, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) { redstoneChannel = (redstoneChannel + 1) % 16; }
        else { ioMode = ioMode == 0 ? 1 : 0; }
        markDirty();
        networkUpdateValues();
        onChange();
        markContainingBlockForUpdate(null);
        world.addBlockEvent(getPos(), getBlockType(), 254, 0);
        return true;
    }

    @Override @Nullable public Boolean canConnectCable(@Nonnull ICWireType cable, @Nonnull ICTargetingInfo target, @Nonnull net.minecraft.util.math.Vec3i offset) {
        if (!ICWireType.REDSTONE_CATEGORY.equals(cable.getCategory())) { return false; }
        return limitType == null || limitType == cable;
    }

    @Override public boolean connectCable(@Nonnull ICWireType cable, @Nonnull ICTargetingInfo target, BlockPos otherMaster) {
        acceptCable(cable);
        networkConnectorsUpdated();
        return true;
    }

    @Override public boolean removeCable(@Nullable BlockPos otherEnd, boolean all) {
        forgetCable(null);
        networkLeave();
        return true;
    }

    @Nullable public Vec3d raytraceOffset() { return null; }

    @Nullable public Vec3d connectionOffset(@Nonnull ICWireType cable) { return null; }

    @Override @Nonnull public Vec3d connectionOffset(@Nonnull ICWireType cable, @Nullable BlockPos otherEnd) {
        Vec3d custom = connectionOffset(cable);
        if (custom != null) { return custom; }
        EnumFacing side = facing.getOpposite();
        double conRadius = cable.getRenderDiameter() / 2;
        return new Vec3d(.5 - conRadius * side.getXOffset(), .5 - conRadius * side.getYOffset(), .5 - conRadius * side.getZOffset());
    }

    @Override public EnumFacing getFacing() { return this.facing; }

    @Override public void setFacing(EnumFacing facing) { this.facing = facing; }

    @Override public int getFacingLimitation() { return 0; }

    @Override public boolean mirrorFacingOnPlacement(EntityLivingBase placer) { return true; }

    @Override public boolean canHammerRotate(EnumFacing side, float hitX, float hitY, float hitZ, EntityLivingBase entity) { return false; }

    @Override public boolean cannotRotate(EnumFacing axis) { return true; }

    @Override public void writeCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.setInteger("facing", facing.ordinal());
        nbt.setInteger("ioMode", ioMode);
        nbt.setInteger("redstoneChannel", redstoneChannel);
        nbt.setInteger("output", networkPower(redstoneChannel));
    }

    @Override public void readCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        facing = EnumFacing.byIndex(nbt.getInteger("facing"));
        ioMode = nbt.getInteger("ioMode");
        redstoneChannel = nbt.getInteger("redstoneChannel");
        outputClient = nbt.getInteger("output");
    }

    @Override public float[] getBlockBounds() {
        float length = .625f;
        float wMin = .3125f;
        float wMax = .6875f;
        switch (facing.getOpposite()) {
            case UP: return new float[]{wMin, 0, wMin, wMax, length, wMax};
            case DOWN: return new float[]{wMin, 1 - length, wMin, wMax, 1, wMax};
            case SOUTH: return new float[]{wMin, wMin, 0, wMax, wMax, length};
            case NORTH: return new float[]{wMin, wMin, 1 - length, wMax, wMax, 1};
            case EAST: return new float[]{0, wMin, wMin, length, wMax, wMax};
            case WEST: return new float[]{1 - length, wMin, wMin, 1, wMax, wMax};
        }
        return new float[]{0, 0, 0, 1, 1, 1};
    }

    @Override public boolean shouldRenderGroup(@Nonnull IBlockState object, @Nonnull String group) {
        if ("io_out".equals(group)) { return this.ioMode == 1; }
        if ("io_in".equals(group)) { return this.ioMode == 0; }
        return true;
    }

    @Override public int getRenderColour(@Nonnull IBlockState object, @Nonnull String group) {
        if ("coloured".equals(group)) { return 0xff000000 | EnumDyeColor.byMetadata(this.redstoneChannel).getColorValue(); }
        return 0xffffffff;
    }

    @Override @Nonnull public String getCacheKey(@Nonnull IBlockState object) { return redstoneChannel + ";" + ioMode; }

    @Override public String[] getOverlayText(EntityPlayer player, RayTraceResult mop, boolean hammer) { return null; }

    @Override public boolean useNixieFont(EntityPlayer player, RayTraceResult mop) { return false; }
}
