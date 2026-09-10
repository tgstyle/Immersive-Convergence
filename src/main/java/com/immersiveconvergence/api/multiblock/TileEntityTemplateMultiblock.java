package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.client.split.ISubmodelOffsetProvider;
import com.immersiveconvergence.common.util.ICLogger;
import com.immersiveconvergence.api.crafting.ICRecipe;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IPlayerInteraction;
import com.immersiveconvergence.api.util.ICUtils;
import com.immersiveconvergence.api.util.IICInventory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

@SuppressWarnings("unused")
public abstract class TileEntityTemplateMultiblock<T extends TileEntityTemplateMultiblock<T, R, M>, R extends ICRecipe, M extends T> extends ICTileEntityMultiblockMetal<T, R> implements IPlayerInteraction, ISubmodelOffsetProvider, IICInventory {
    private static final String KEY_INPUT_TANK_CLEARED = "gui.immersiveconvergence.input_tank_cleared";
    private static final String KEY_INPUT_TANKS_CLEARED = "gui.immersiveconvergence.input_tanks_cleared";
    private int blockUpdateCooldown = 0;
    private List<AxisAlignedBB> boundsCache;
    private int boundsCachePos = Integer.MIN_VALUE;
    private EnumFacing boundsCacheFacing;
    private boolean boundsCacheMirrored;
    public boolean shouldDropOriginal = true;
    public boolean shouldDropInventory = true;

    public TileEntityTemplateMultiblock(MachineTemplateMultiblock<?> instance, int energyCapacity, boolean redstoneControl) { super(instance, new int[]{instance.height, instance.length, instance.width}, energyCapacity, redstoneControl); }

    public abstract M master();

    public static class ProcessInMachine<R extends ICRecipe> extends MultiblockProcessInMachine<R> {
        public ProcessInMachine(R recipe, int... inputSlots) { super(recipe, inputSlots); }

        @Override public boolean canProcess(@Nonnull ICTileEntityMultiblockMetal<?, ?> multiblock) { return canProcess((TileEntityTemplateMultiblock<?, ?, ?>)multiblock); }

        @Override public void doProcessTick(@Nonnull ICTileEntityMultiblockMetal<?, ?> multiblock) { doProcessTick((TileEntityTemplateMultiblock<?, ?, ?>)multiblock); }

        public boolean canProcess(TileEntityTemplateMultiblock<?, ?, ?> multiblock) { return super.canProcess(multiblock); }

        public void doProcessTick(TileEntityTemplateMultiblock<?, ?, ?> multiblock) { super.doProcessTick(multiblock); }
    }

    protected abstract GenericShape getShapeGetter();

    protected boolean useMirroredShape() { return true; }

    protected boolean isInputFluidPoI(BlockPos position) { return false; }

    protected int clearInputTanks() { return 0; }

    @Override public boolean interact(@Nonnull EnumFacing side, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull ItemStack heldItem, float hitX, float hitY, float hitZ) {
        if (!formed || !player.isSneaking() || !ICUtils.isHammer(heldItem)) { return false; }
        M master = master();
        if (master == null || !master.isInputFluidPoI(posInMultiblock())) { return false; }
        if (!world.isRemote) {
            int cleared = master.clearInputTanks();
            player.sendStatusMessage(new TextComponentTranslation(cleared > 1 ? KEY_INPUT_TANKS_CLEARED : KEY_INPUT_TANK_CLEARED), true);
        }
        return true;
    }

    protected AxisAlignedBB preprocessShapeAABB(AxisAlignedBB aabb) { return aabb; }

    protected abstract IFluidTank[] getAccessibleFluidTanks(EnumFacing side, BlockPos position);

    protected abstract boolean canFillTankFrom(int iTank, EnumFacing side, FluidStack resource, BlockPos position);

    protected abstract boolean canDrainTankFrom(int iTank, EnumFacing side, BlockPos position);

    @Override protected void setWorldCreate(@Nonnull World worldIn) { this.world = worldIn; }

    @Override public void readCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        formed = nbt.getBoolean("formed");
        pos = nbt.getInteger("pos");
        offset = nbt.getIntArray("offset");
        facing = EnumFacing.values()[nbt.getInteger("facing")];
        mirrored = nbt.getBoolean("mirrored");
    }

    @Override public void writeCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.setBoolean("formed", formed);
        nbt.setInteger("pos", pos);
        nbt.setIntArray("offset", offset);
        nbt.setInteger("facing", facing.ordinal());
        nbt.setBoolean("mirrored", mirrored);
    }

    @SuppressWarnings("unchecked")
    @Override @Nullable public T getTileForPos(int targetPos) {
        BlockPos target = getBlockPosForPos(targetPos);
        TileEntity tile = ICUtils.getExistingTileEntity(world, target);
        if (tile instanceof TileEntityTemplateMultiblock && tile.getClass().isInstance(this)) return (T)tile;
        return null;
    }

    @Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            assert facing != null;
            if (this.getAccessibleFluidTanks(facing).length > 0) return true;
        }
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override @Nullable public <TE> TE getCapability(@Nonnull Capability<TE> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            assert facing != null;
            if (this.getAccessibleFluidTanks(facing).length > 0) return (TE)new MultiblockFluidWrapper(this, facing);
        }
        return super.getCapability(capability, facing);
    }

    private static final String[] DEFAULT_COMPARATOR_POIS = {"comparator0"};

    private Set<BlockPos> comparatorPositionsCache;

    protected String[] comparatorPoINames() { return DEFAULT_COMPARATOR_POIS; }

    public Set<BlockPos> comparatorPositions() {
        if (comparatorPositionsCache == null) {
            MachineTemplateMultiblock<?> instance = (MachineTemplateMultiblock<?>)multiblockInstance;
            Set<BlockPos> found = new LinkedHashSet<>();
            for (String name : comparatorPoINames()) {
                for (PoIJSONSchema poi : instance.pointsOfInterest) {
                    if (name.equals(poi.name)) { found.add(poi.position); }
                }
            }
            comparatorPositionsCache = found;
        }
        return comparatorPositionsCache;
    }

    public boolean isComparatorPos() {
        Set<BlockPos> positions = comparatorPositions();
        return positions.isEmpty() || positions.contains(posInMultiblock());
    }

    private static final int COMPARATOR_NOTIFY_MIN_INTERVAL = 5;
    private static final int COMPARATOR_NOTIFY_MAX_INTERVAL = 40;
    private static final int COMPARATOR_POSITIONS_PER_TICK = 12;
    private long lastComparatorNotify = Long.MIN_VALUE;
    private boolean comparatorNotifyPending;

    public void notifyComparators() {
        if (world == null || world.isRemote) { return; }
        long now = world.getTotalWorldTime();
        if (now - lastComparatorNotify < comparatorInterval()) {
            comparatorNotifyPending = true;
            return;
        }
        sweepComparators(now);
    }

    private int comparatorInterval() {
        int size = comparatorSweepPositions().size();
        int scaled = size / COMPARATOR_POSITIONS_PER_TICK;
        return Math.max(COMPARATOR_NOTIFY_MIN_INTERVAL, Math.min(COMPARATOR_NOTIFY_MAX_INTERVAL, scaled));
    }

    private List<BlockPos> comparatorSweepCache;

    private List<BlockPos> comparatorSweepPositions() {
        if (comparatorSweepCache != null) { return comparatorSweepCache; }
        List<BlockPos> found = new ArrayList<>();
        Set<BlockPos> declared = comparatorPositions();
        if (!declared.isEmpty()) {
            for (BlockPos poi : declared) { found.add(getBlockPosForPos(poi)); }
        }
        else {
            int levels = structureDimensions[1] * structureDimensions[2];
            int total = structureDimensions[0] * levels;
            boolean filtered = false;
            try {
                ItemStack[][][] manual = multiblockInstance.getStructureManual();
                if (manual != null) {
                    for (int i = 0; i < total; i++) {
                        ItemStack stack = manual[i / levels][i % levels / structureDimensions[2]][i % structureDimensions[2]];
                        if (stack == null || !stack.isEmpty()) { found.add(getBlockPosForPos(i)); }
                    }
                    filtered = true;
                }
            }
            catch (Exception e) {
                ICLogger.error("Multiblock structure manual did not match its dimensions: " + e);
                found.clear();
            }
            if (!filtered) {
                for (int i = 0; i < total; i++) { found.add(getBlockPosForPos(i)); }
            }
        }
        comparatorSweepCache = found;
        return found;
    }

    @Override public void invalidateStructureCaches() {
        super.invalidateStructureCaches();
        comparatorSweepCache = null;
    }

    private void sweepComparators(long now) {
        lastComparatorNotify = now;
        comparatorNotifyPending = false;
        for (BlockPos worldPos : comparatorSweepPositions()) {
            world.updateComparatorOutputLevel(worldPos, world.getBlockState(worldPos).getBlock());
        }
    }

    @Override protected void tickPendingNotifications() {
        if (!comparatorNotifyPending || world == null || world.isRemote) { return; }
        long now = world.getTotalWorldTime();
        if (now - lastComparatorNotify >= comparatorInterval()) { sweepComparators(now); }
    }

    public BlockPos posInMultiblock() {
        MachineTemplateMultiblock<?> instance = (MachineTemplateMultiblock<?>)multiblockInstance;
        return MultiblockShapes.localPos(pos, instance.width, instance.length);
    }

    public int toFlatIndex(BlockPos posInMultiblock) {
        MachineTemplateMultiblock<?> instance = (MachineTemplateMultiblock<?>)multiblockInstance;
        return posInMultiblock.getY() * (instance.length * instance.width) + posInMultiblock.getZ() * instance.width + posInMultiblock.getX();
    }

    public BlockPos getBlockPosForPos(BlockPos posInMultiblock) { return getBlockPosForPos(toFlatIndex(posInMultiblock)); }

    private BlockPos posToMultiblock() {
        MachineTemplateMultiblock<?> instance = (MachineTemplateMultiblock<?>)multiblockInstance;
        return adjustPosInMultiblock(posInMultiblock(), instance.width);
    }

    protected BlockPos adjustPosInMultiblock(BlockPos posInMultiblock, int width) { return posInMultiblock; }

    private List<AxisAlignedBB> getAabbs() {
        if (boundsCache != null && boundsCachePos == pos && boundsCacheFacing == facing && boundsCacheMirrored == mirrored) { return boundsCache; }
        List<AxisAlignedBB> list = getShapeGetter().getShape(posToMultiblock());
        List<AxisAlignedBB> processed = new ArrayList<>(list.size());
        for (AxisAlignedBB aabb : list) { processed.add(preprocessShapeAABB(aabb)); }
        boundsCache = MultiblockShapes.bounds(processed, facing, useMirroredShape() && mirrored);
        boundsCachePos = pos;
        boundsCacheFacing = facing;
        boundsCacheMirrored = mirrored;
        return boundsCache;
    }

    @Nonnull public List<AxisAlignedBB> getAdvancedCollisionBounds() { return getAabbs(); }

    @Nonnull public List<AxisAlignedBB> getAdvancedSelectionBounds() { return getAabbs(); }

    @SuppressWarnings("unused") public boolean isOverrideBox(@Nonnull AxisAlignedBB box, @Nonnull EntityPlayer player, @Nonnull RayTraceResult mop, @Nonnull List<AxisAlignedBB> list) { return true; }

    @Override @Nonnull public float[] getBlockBounds() { return MultiblockShapes.blockBounds(getAabbs()); }

    @Override @Nullable public BlockPos getModelOffset() { return formed ? new BlockPos(offset[0], offset[1], offset[2]) : null; }

    @Override @Nonnull public ItemStack getOriginalBlock() { return ((MachineTemplateMultiblock<?>)this.multiblockInstance).getOriginalBlock(pos); }

    @Override public void doGraphicalUpdates(int slot) { this.markDirty(); this.markContainingBlockForUpdate(null); }

    @Override @Nonnull public R findRecipeForInsertion(@Nonnull ItemStack inserting) { throw new UnsupportedOperationException(); }

    @Override @Nonnull public int[] getEnergyPos() { return new int[0]; }

    @Override @Nonnull public int[] getOutputSlots() { return new int[0]; }

    @Override @Nonnull public int[] getRedstonePos() {
        M master = master();
        return master == null ? new int[0] : master.getRedstonePos();
    }

    @Override public boolean additionalCanProcessCheck(@Nonnull MultiblockProcess<R> process) { return false; }

    @Override public void doProcessOutput(@Nonnull ItemStack output) {}

    @Override public void doProcessFluidOutput(@Nonnull FluidStack output) {}

    @Override public void onProcessFinish(@Nonnull MultiblockProcess<R> process) {}

    @Override public int getMaxProcessPerTick() { return 0; }

    @Override public int getProcessQueueMaxLength() { return 0; }

    @Override public float getMinProcessDistance(@Nonnull MultiblockProcess<R> process) { return 0f; }

    @Override public boolean isInWorldProcessingMachine() { return false; }

    @Override @Nonnull protected IFluidTank[] getAccessibleFluidTanks(EnumFacing side) {
        M master = master();
        if (master == null) return new IFluidTank[0];
        return master.getAccessibleFluidTanks(side, posInMultiblock());
    }

    @Override protected boolean canFillTankFrom(int iTank, @Nonnull EnumFacing side, @Nonnull FluidStack resource) {
        M master = master();
        if (master == null) return false;
        return master.canFillTankFrom(iTank, side, resource, posInMultiblock());
    }

    @Override protected boolean canDrainTankFrom(int iTank, @Nonnull EnumFacing side) {
        M master = master();
        if (master == null) return false;
        return master.canDrainTankFrom(iTank, side, posInMultiblock());
    }

    @Override public void disassemble() {
        if (formed && !world.isRemote) {
            BlockPos masterPos = getPos().add(-offset[0], -offset[1], -offset[2]);
            if (QueueProcessor.isDisassembling(world, masterPos)) { return; }
            TileEntity teMaster = world.getTileEntity(masterPos);
            if (teMaster instanceof IICInventory && shouldDropInventory) {
                NonNullList<ItemStack> inv = ((IICInventory)teMaster).getInventory();
                for (ItemStack stack : inv) {
                    if (!stack.isEmpty()) {
                        float rx = world.rand.nextFloat() * 0.8F + 0.1F;
                        float ry = world.rand.nextFloat() * 0.8F + 0.1F;
                        float rz = world.rand.nextFloat() * 0.8F + 0.1F;
                        EntityItem entityitem = new EntityItem(world, masterPos.getX() + rx, masterPos.getY() + ry, masterPos.getZ() + rz, stack.copy());
                        entityitem.motionX = world.rand.nextGaussian() * 0.05;
                        entityitem.motionY = world.rand.nextGaussian() * 0.05 + 0.2F;
                        entityitem.motionZ = world.rand.nextGaussian() * 0.05;
                        world.spawnEntity(entityitem);
                    }
                }
                inv.clear();
            }
            if (QueueProcessor.handleDisassembly(this, structureDimensions, shouldDropOriginal) != QueueProcessor.Result.FALLBACK) { return; }
            BlockPos startPos = getBlockPosForPos(0);
            long time = world.getTotalWorldTime();
            for (int h = 0; h < structureDimensions[0]; h++) for (int l = 0; l < structureDimensions[1]; l++) for (int w = 0; w < structureDimensions[2]; w++) {
                int ww = mirrored ? -w : w;
                BlockPos pos2 = startPos.offset(facing, l).offset(facing.rotateY(), ww).add(0, h, 0);
                ItemStack s = ItemStack.EMPTY;
                TileEntity te = world.getTileEntity(pos2);
                if (te instanceof ICTileEntityMultiblockPart) {
                    ICTileEntityMultiblockPart<?> part = (ICTileEntityMultiblockPart<?>)te;
                    Vec3i diff = pos2.subtract(masterPos);
                    if (part.offset[0] != diff.getX() || part.offset[1] != diff.getY() || part.offset[2] != diff.getZ()) continue;
                    if (time != part.onlyLocalDisassembly) {
                        s = part.getOriginalBlock();
                        part.formed = false;
                    }
                }
                if (pos2.equals(getPos())) s = this.getOriginalBlock();
                IBlockState state = ICUtils.getStateFromItemStack(s);
                if (state != null) {
                    if (pos2.equals(getPos())) { if (shouldDropOriginal) world.spawnEntity(new EntityItem(world, pos2.getX() + 0.5, pos2.getY() + 0.5, pos2.getZ() + 0.5, s)); }
                    else replaceStructureBlock(pos2, state, s, h, l, w);
                }
            }
        }
    }

    protected void throttledBlockUpdate() {
        if (blockUpdateCooldown > 0) {
            blockUpdateCooldown--;
            return;
        }
        blockUpdateCooldown = 20;
        syncToTrackingClients();
    }
}
