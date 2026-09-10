package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.block.ICTileEntityBase;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IBlockBounds;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IDirectionalTile;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IGeneralMultiblock;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.ITileDrop;
import com.immersiveconvergence.api.util.ICUtils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public abstract class ICTileEntityMultiblockPart<T extends ICTileEntityMultiblockPart<T>> extends ICTileEntityBase implements ITickable, IDirectionalTile, IBlockBounds, IGeneralMultiblock, ICMultiblockPart {
    public boolean formed = false;
    public int pos = -1;
    public int[] offset = {0, 0, 0};
    public boolean mirrored = false;
    public EnumFacing facing = EnumFacing.NORTH;
    public long onlyLocalDisassembly = -1;
    protected final int[] structureDimensions;

    private T masterCache;
    private MultiblockFluidWrapper[] fluidWrappers;

    protected ICTileEntityMultiblockPart(int[] structureDimensions) { this.structureDimensions = structureDimensions; }

    @Override public boolean isPartUnformed() { return !formed; }

    @Override public void unformPart() { this.formed = false; invalidateStructureCaches(); }

    @Override public int[] getPartOffset() { return offset; }

    @Override public EnumFacing getPartFacing() { return facing; }

    @Override public boolean isPartMirrored() { return mirrored; }

    @Override public long getPartDisassemblyTime() { return onlyLocalDisassembly; }

    @Override public BlockPos getPartOrigin() { return getOrigin(); }

    @Override @Nonnull public ItemStack getPartOriginalBlock() { return getOriginalBlock(); }

    @Override @Nullable public TileEntity getPartMaster() { return master(); }

    @Override public EnumFacing getFacing() { return this.facing; }

    @Override public void setFacing(EnumFacing facing) { this.facing = facing; }

    @Override public int getFacingLimitation() { return 2; }

    @Override public boolean mirrorFacingOnPlacement(EntityLivingBase placer) { return false; }

    @Override public boolean canHammerRotate(EnumFacing side, float hitX, float hitY, float hitZ, EntityLivingBase entity) { return false; }

    @Override public boolean cannotRotate(EnumFacing axis) { return true; }

    @Override public void readCustomNBT(NBTTagCompound nbt, boolean descPacket) {
        invalidateStructureCaches();
        formed = nbt.getBoolean("formed");
        pos = nbt.getInteger("pos");
        offset = nbt.getIntArray("offset");
        mirrored = nbt.getBoolean("mirrored");
        facing = EnumFacing.byIndex(nbt.getInteger("facing"));
    }

    @Override public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket) {
        nbt.setBoolean("formed", formed);
        nbt.setInteger("pos", pos);
        nbt.setIntArray("offset", offset);
        nbt.setBoolean("mirrored", mirrored);
        nbt.setInteger("facing", facing.ordinal());
    }

    @Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && this.getAccessibleFluidTanks(facing).length > 0) { return true; }
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override public <C> C getCapability(@Nonnull Capability<C> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && this.getAccessibleFluidTanks(facing).length > 0) { return (C)fluidWrapper(facing); }
        return super.getCapability(capability, facing);
    }

    private MultiblockFluidWrapper fluidWrapper(@Nullable EnumFacing facing) {
        if (fluidWrappers == null) { fluidWrappers = new MultiblockFluidWrapper[EnumFacing.VALUES.length + 1]; }
        int index = facing == null ? EnumFacing.VALUES.length : facing.ordinal();
        MultiblockFluidWrapper wrapper = fluidWrappers[index];
        if (wrapper == null) {
            wrapper = new MultiblockFluidWrapper(this, facing);
            fluidWrappers[index] = wrapper;
        }
        return wrapper;
    }

    @Nonnull protected abstract IFluidTank[] getAccessibleFluidTanks(@Nullable EnumFacing side);

    protected abstract boolean canFillTankFrom(int iTank, EnumFacing side, FluidStack resource);

    protected abstract boolean canDrainTankFrom(int iTank, EnumFacing side);

    public static class MultiblockFluidWrapper implements IFluidHandler {
        final ICTileEntityMultiblockPart<?> multiblock;
        final EnumFacing side;

        public MultiblockFluidWrapper(ICTileEntityMultiblockPart<?> multiblock, EnumFacing side) {
            this.multiblock = multiblock;
            this.side = side;
        }

        @Override public IFluidTankProperties[] getTankProperties() {
            if (!this.multiblock.formed) { return new IFluidTankProperties[0]; }
            IFluidTank[] tanks = this.multiblock.getAccessibleFluidTanks(side);
            IFluidTankProperties[] array = new IFluidTankProperties[tanks.length];
            for (int i = 0; i < tanks.length; i++) { array[i] = new FluidTankProperties(tanks[i].getFluid(), tanks[i].getCapacity()); }
            return array;
        }

        @Override public int fill(FluidStack resource, boolean doFill) {
            if (!this.multiblock.formed || resource == null) { return 0; }
            IFluidTank[] tanks = this.multiblock.getAccessibleFluidTanks(side);
            int fill = -1;
            for (int i = 0; i < tanks.length; i++) {
                IFluidTank tank = tanks[i];
                if (tank != null && this.multiblock.canFillTankFrom(i, side, resource) && tank.getFluid() != null && tank.getFluid().isFluidEqual(resource)) {
                    fill = tank.fill(resource, doFill);
                    if (fill > 0) { break; }
                }
            }
            if (fill == -1) {
                for (int i = 0; i < tanks.length; i++) {
                    IFluidTank tank = tanks[i];
                    if (tank != null && this.multiblock.canFillTankFrom(i, side, resource)) {
                        fill = tank.fill(resource, doFill);
                        if (fill > 0) { break; }
                    }
                }
            }
            if (fill > 0) { this.multiblock.updateMasterBlock(null, true); }
            return Math.max(fill, 0);
        }

        @Nullable @Override public FluidStack drain(FluidStack resource, boolean doDrain) {
            if (!this.multiblock.formed || resource == null) { return null; }
            IFluidTank[] tanks = this.multiblock.getAccessibleFluidTanks(side);
            FluidStack drain = null;
            for (int i = 0; i < tanks.length; i++) {
                IFluidTank tank = tanks[i];
                if (tank != null && this.multiblock.canDrainTankFrom(i, side)) {
                    drain = tank instanceof IFluidHandler ? ((IFluidHandler)tank).drain(resource, doDrain) : tank.drain(resource.amount, doDrain);
                    if (drain != null) { break; }
                }
            }
            if (drain != null) { this.multiblock.updateMasterBlock(null, true); }
            return drain;
        }

        @Nullable @Override public FluidStack drain(int maxDrain, boolean doDrain) {
            if (!this.multiblock.formed || maxDrain == 0) { return null; }
            IFluidTank[] tanks = this.multiblock.getAccessibleFluidTanks(side);
            FluidStack drain = null;
            for (int i = 0; i < tanks.length; i++) {
                IFluidTank tank = tanks[i];
                if (tank != null && this.multiblock.canDrainTankFrom(i, side)) {
                    drain = tank.drain(maxDrain, doDrain);
                    if (drain != null) { break; }
                }
            }
            if (drain != null) { this.multiblock.updateMasterBlock(null, true); }
            return drain;
        }
    }

    public static boolean _Immovable() { return true; }

    @SuppressWarnings("unchecked")
    @Nullable public T master() {
        if (offset[0] == 0 && offset[1] == 0 && offset[2] == 0) { return (T)this; }
        T cached = masterCache;
        if (cached != null && !cached.isInvalid()) { return cached; }
        BlockPos masterPos = getPos().add(-offset[0], -offset[1], -offset[2]);
        TileEntity te = ICUtils.getExistingTileEntity(world, masterPos);
        masterCache = this.getClass().isInstance(te) ? (T)te : null;
        return masterCache;
    }

    public void invalidateStructureCaches() { masterCache = null; }

    private IFluidTank[][] tankViews;

    protected IFluidTank[] tankView(int index, IFluidTank tank) {
        if (tankViews == null || tankViews.length <= index) {
            IFluidTank[][] grown = new IFluidTank[index + 1][];
            if (tankViews != null) { System.arraycopy(tankViews, 0, grown, 0, tankViews.length); }
            tankViews = grown;
        }
        IFluidTank[] view = tankViews[index];
        if (view == null || view[0] != tank) {
            view = new IFluidTank[]{tank};
            tankViews[index] = view;
        }
        return view;
    }

    public void updateMasterBlock(IBlockState state, boolean blockUpdate) {
        T master = master();
        if (master == null) { return; }
        master.markDirty();
        if (blockUpdate) { master.markContainingBlockForUpdate(state); }
    }

    public boolean isDummy() { return offset[0] != 0 || offset[1] != 0 || offset[2] != 0; }

    @Override public boolean isLogicDummy() { return isDummy(); }

    public abstract ItemStack getOriginalBlock();

    public void disassemble() {
        if (!formed || world.isRemote) { return; }
        BlockPos startPos = getOrigin();
        BlockPos masterPos = getPos().add(-offset[0], -offset[1], -offset[2]);
        long time = world.getTotalWorldTime();
        for (int yy = 0; yy < structureDimensions[0]; yy++) {
            for (int ll = 0; ll < structureDimensions[1]; ll++) {
                for (int ww = 0; ww < structureDimensions[2]; ww++) {
                    int w = mirrored ? -ww : ww;
                    BlockPos target = startPos.offset(facing, ll).offset(facing.rotateY(), w).add(0, yy, 0);
                    ItemStack s = ItemStack.EMPTY;
                    TileEntity te = world.getTileEntity(target);
                    if (te instanceof ICTileEntityMultiblockPart) {
                        ICTileEntityMultiblockPart<?> part = (ICTileEntityMultiblockPart<?>)te;
                        Vec3i diff = target.subtract(masterPos);
                        if (part.offset[0] != diff.getX() || part.offset[1] != diff.getY() || part.offset[2] != diff.getZ()) { continue; }
                        if (time != part.onlyLocalDisassembly) {
                            s = part.getOriginalBlock();
                            part.formed = false;
                        }
                    }
                    if (target.equals(getPos())) { s = this.getOriginalBlock(); }
                    IBlockState state = ICUtils.getStateFromItemStack(s);
                    if (state == null) { continue; }
                    if (target.equals(getPos())) { world.spawnEntity(new EntityItem(world, target.getX() + .5, target.getY() + .5, target.getZ() + .5, s)); }
                    else { replaceStructureBlock(target, state, s, yy, ll, ww); }
                }
            }
        }
    }

    public BlockPos getOrigin() { return getBlockPosForPos(0); }

    public BlockPos getBlockPosForPos(int targetPos) {
        int blocksPerLevel = structureDimensions[1] * structureDimensions[2];
        int distH = (targetPos / blocksPerLevel) - (pos / blocksPerLevel);
        int distL = (targetPos % blocksPerLevel / structureDimensions[2]) - (pos % blocksPerLevel / structureDimensions[2]);
        int distW = (targetPos % structureDimensions[2]) - (pos % structureDimensions[2]);
        int w = mirrored ? -distW : distW;
        return getPos().offset(facing, distL).offset(facing.rotateY(), w).add(0, distH, 0);
    }

    public void replaceStructureBlock(BlockPos pos, IBlockState state, ItemStack stack, int h, int l, int w) {
        if (state.getBlock() == this.getBlockType()) { world.setBlockToAir(pos); }
        world.setBlockState(pos, state);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ITileDrop) { ((ITileDrop)tile).readOnPlacement(null, stack); }
    }
}
