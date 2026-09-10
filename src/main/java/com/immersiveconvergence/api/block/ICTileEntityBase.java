package com.immersiveconvergence.api.block;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.energy.ICFluxWrapper;
import com.immersiveconvergence.api.energy.IICFluxConnector;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IDirectionalTile;
import com.immersiveconvergence.common.block.IETileBridge;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public abstract class ICTileEntityBase extends TileEntity {
    private static Double renderDistanceMultiplier;

    @Override public void readFromNBT(@Nonnull NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.readCustomNBT(nbt, false);
    }

    public abstract void readCustomNBT(NBTTagCompound nbt, boolean descPacket);

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        this.writeCustomNBT(nbt, false);
        return nbt;
    }

    public abstract void writeCustomNBT(NBTTagCompound nbt, boolean descPacket);

    @Override public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeCustomNBT(nbt, true);
        return new SPacketUpdateTileEntity(this.pos, 3, nbt);
    }

    @Override @Nonnull public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.writeToNBT(new NBTTagCompound());
        writeCustomNBT(nbt, true);
        return nbt;
    }

    @Override public void onDataPacket(@Nonnull NetworkManager net, SPacketUpdateTileEntity pkt) { this.readCustomNBT(pkt.getNbtCompound(), true); }

    @Override public void rotate(@Nonnull Rotation rot) {
        if (rot == Rotation.NONE || !(this instanceof IDirectionalTile) || ((IDirectionalTile)this).cannotRotate(EnumFacing.UP)) { return; }
        EnumFacing f = ((IDirectionalTile)this).getFacing();
        switch (rot) {
            case CLOCKWISE_90: f = f.rotateY(); break;
            case CLOCKWISE_180: f = f.getOpposite(); break;
            case COUNTERCLOCKWISE_90: f = f.rotateYCCW(); break;
            default: break;
        }
        ((IDirectionalTile)this).setFacing(f);
        this.markDirty();
        if (this.pos != null) { this.markBlockForUpdate(this.pos, null); }
    }

    @Override public void mirror(@Nonnull Mirror mirrorIn) {
        if (mirrorIn != Mirror.FRONT_BACK || !(this instanceof IDirectionalTile)) { return; }
        ((IDirectionalTile)this).setFacing(((IDirectionalTile)this).getFacing());
        this.markDirty();
        if (this.pos != null) { this.markBlockForUpdate(this.pos, null); }
    }

    public void receiveMessageFromClient(NBTTagCompound message) {}

    public void receiveMessageFromServer(NBTTagCompound message) {}

    public void onEntityCollision(World world, Entity entity) {}

    @Override public boolean receiveClientEvent(int id, int type) {
        if (id == -1 || id == 0 || id == 255) {
            markContainingBlockForUpdate(null);
            return true;
        }
        if (id == 254) {
            IBlockState state = world.getBlockState(pos);
            if (state instanceof IExtendedBlockState && ICMods.immersiveEngineering()) { IETileBridge.removeStateFromSmartModelCache((IExtendedBlockState)state); }
            world.notifyBlockUpdate(pos, state, state, 3);
            return true;
        }
        return super.receiveClientEvent(id, type);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override public boolean shouldRefresh(World world, @Nonnull BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState) {
        if (world.isBlockLoaded(pos)) { newState = world.getBlockState(pos); }
        if (oldState.getBlock() != newState.getBlock() || !(oldState.getBlock() instanceof ICBlockBase) || !(newState.getBlock() instanceof ICBlockBase)) { return true; }
        IProperty type = ((ICBlockBase<?>)oldState.getBlock()).getMetaProperty();
        return oldState.getValue(type) != newState.getValue(type);
    }

    public void markContainingBlockForUpdate(@Nullable IBlockState newState) { markBlockForUpdate(getPos(), newState); }

    public void markBlockForUpdate(BlockPos pos, @Nullable IBlockState newState) {
        IBlockState state = world.getBlockState(pos);
        if (newState == null) { newState = state; }
        world.notifyBlockUpdate(pos, state, newState, 3);
        world.notifyNeighborsOfStateChange(pos, newState.getBlock(), true);
    }

    @Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY && this instanceof IICFluxConnector) { return ((IICFluxConnector)this).getCapabilityWrapper(facing) != null; }
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY && this instanceof IICFluxConnector) {
            ICFluxWrapper wrapper = ((IICFluxConnector)this).getCapabilityWrapper(facing);
            if (wrapper != null) { return (T)wrapper; }
        }
        return super.getCapability(capability, facing);
    }

    @Override public double getMaxRenderDistanceSquared() {
        if (renderDistanceMultiplier == null) { renderDistanceMultiplier = ICMods.immersiveEngineering() ? IETileBridge.renderDistanceMultiplier() : 1.0D; }
        return super.getMaxRenderDistanceSquared() * renderDistanceMultiplier * renderDistanceMultiplier;
    }
}
