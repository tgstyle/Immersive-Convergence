package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.ICLib;
import com.immersiveconvergence.api.block.ICProperties;
import com.immersiveconvergence.api.block.ICProperties.PropertyBoolInverted;
import com.immersiveconvergence.api.block.ICSideConfig;
import com.immersiveconvergence.api.crafting.ICIngredient;
import com.immersiveconvergence.api.crafting.ICRecipe;
import com.immersiveconvergence.api.energy.ICFluxWrapper;
import com.immersiveconvergence.api.energy.IICInternalFluxHandler;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IComparatorOverride;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IActiveState;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IHammerInteraction;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IMirrorAble;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IProcessTile;
import com.immersiveconvergence.api.multiblock.ICBlockInterfaces.IUsesBooleanProperty;
import com.immersiveconvergence.api.util.ICFluxStorage;
import com.immersiveconvergence.api.util.ICFluxStorageAdvanced;
import com.immersiveconvergence.api.util.ICUtils;
import com.immersiveconvergence.api.util.IICInventory;
import com.immersiveconvergence.common.event.ICTickingRegistry;
import com.immersiveconvergence.common.util.ICLogger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public abstract class ICTileEntityMultiblockMetal<T extends ICTileEntityMultiblockMetal<T, R>, R extends ICRecipe> extends ICTileEntityMultiblockPart<T> implements IICInventory, IICInternalFluxHandler, IHammerInteraction, IMirrorAble, IProcessTile, IComparatorOverride {
    public final ICFluxStorageAdvanced energyStorage;
    protected final boolean hasRedstoneControl;
    protected final ICMultiblock multiblockInstance;
    protected boolean redstoneControlInverted = false;
    @Nullable public Boolean computerOn = null;
    public List<MultiblockProcess<R>> processQueue = new ArrayList<>();
    public int tickedProcesses = 0;

    public ICTileEntityMultiblockMetal(ICMultiblock multiblockInstance, int[] structureDimensions, int energyCapacity, boolean redstoneControl) {
        super(structureDimensions);
        this.energyStorage = new ICFluxStorageAdvanced(energyCapacity);
        this.hasRedstoneControl = redstoneControl;
        this.multiblockInstance = multiblockInstance;
    }

    @Override public void readCustomNBT(NBTTagCompound nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        energyStorage.readFromNBT(nbt);
        redstoneControlInverted = nbt.getBoolean("redstoneControlInverted");
        NBTTagList processNBT = nbt.getTagList("processQueue", 10);
        processQueue.clear();
        for (int i = 0; i < processNBT.tagCount(); i++) {
            NBTTagCompound tag = processNBT.getCompoundTagAt(i);
            ICRecipe recipe = readRecipeFromNBT(tag);
            if (recipe != null) {
                int processTick = tag.getInteger("process_processTick");
                MultiblockProcess<R> process = loadProcessFromNBT(tag);
                if (process != null) {
                    process.processTick = processTick;
                    process.displaySlot = tag.hasKey("process_displaySlot") ? tag.getInteger("process_displaySlot") : -1;
                    processQueue.add(process);
                }
            }
        }
        if (nbt.hasKey("computerOn", Constants.NBT.TAG_BYTE) && Loader.isModLoaded("opencomputers")) {
            byte cOn = nbt.getByte("computerOn");
            switch (cOn) {
                case 0: computerOn = Boolean.FALSE; break;
                case 1: computerOn = Boolean.TRUE; break;
                case 2: computerOn = null; break;
            }
        }
    }

    @Override public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        energyStorage.writeToNBT(nbt);
        nbt.setBoolean("redstoneControlInverted", redstoneControlInverted);
        NBTTagList processNBT = new NBTTagList();
        for (MultiblockProcess<R> process : this.processQueue) { processNBT.appendTag(writeProcessToNBT(process)); }
        nbt.setTag("processQueue", processNBT);
        if (computerOn != null) { nbt.setBoolean("computerOn", computerOn); }
        else { nbt.setByte("computerOn", (byte)2); }
    }

    protected abstract R readRecipeFromNBT(NBTTagCompound tag);

    @Nullable protected MultiblockProcess<R> loadProcessFromNBT(NBTTagCompound tag) {
        R recipe = readRecipeFromNBT(tag);
        if (recipe == null) { return null; }
        if (isInWorldProcessingMachine()) { return new MultiblockProcessInWorld<>(recipe, tag.getFloat("process_transformationPoint"), ICUtils.loadItemStacksFromNBT(tag.getTag("process_inputItem"))); }
        MultiblockProcessInMachine<R> process = new MultiblockProcessInMachine<>(recipe, tag.getIntArray("process_inputSlots")).setInputTanks(tag.getIntArray("process_inputTanks"));
        return tag.hasKey("process_inputAmounts") ? process.setInputAmounts(tag.getIntArray("process_inputAmounts")) : process;
    }

    protected NBTTagCompound writeProcessToNBT(MultiblockProcess<R> process) {
        NBTTagCompound tag = process.recipe.writeToNBT(new NBTTagCompound());
        tag.setInteger("process_processTick", process.processTick);
        tag.setInteger("process_displaySlot", process.displaySlot);
        process.writeExtraDataToNBT(tag);
        return tag;
    }

    public abstract int[] getEnergyPos();

    public boolean isEnergyPos() {
        for (int i : cachedEnergyPos()) {
            if (pos == i) { return true; }
        }
        return false;
    }

    private int[] energyPosCache;
    private int[] redstonePosCache;
    private T redstoneTileCache;

    private int[] cachedEnergyPos() {
        if (energyPosCache == null) { energyPosCache = getEnergyPos(); }
        return energyPosCache == null ? EMPTY_POS : energyPosCache;
    }

    private int[] cachedRedstonePos() {
        if (redstonePosCache == null) { redstonePosCache = getRedstonePos(); }
        return redstonePosCache == null ? EMPTY_POS : redstonePosCache;
    }

    private static final int[] EMPTY_POS = new int[0];

    @Override public void invalidateStructureCaches() {
        super.invalidateStructureCaches();
        energyPosCache = null;
        redstonePosCache = null;
        redstoneTileCache = null;
    }

    @Override @Nonnull public ICFluxStorage getStorage() {
        T master = this.master();
        if (master != null) { return master.energyStorage; }
        return energyStorage;
    }

    @Override @Nonnull public ICSideConfig getSideConfig(@Nullable EnumFacing facing) { return this.formed && this.isEnergyPos() ? ICSideConfig.INPUT : ICSideConfig.NONE; }

    ICFluxWrapper wrapper = new ICFluxWrapper(this, null);

    @Override public ICFluxWrapper getCapabilityWrapper(EnumFacing facing) {
        if (this.formed && this.isEnergyPos()) { return wrapper; }
        return null;
    }

    @Override public void postEnergyTransferUpdate(int energy, boolean simulate) {
        if (simulate) { return; }
        T master = master();
        if (master == null) { return; }
        master.markDirty();
        if (energy != 0) { master.requestClientSync(); }
    }

    @SideOnly(Side.CLIENT)
    @Override @Nonnull public AxisAlignedBB getRenderBoundingBox() {
        if (!isDummy()) {
            BlockPos nullPos = this.getBlockPosForPos(0);
            return new AxisAlignedBB(nullPos, nullPos.offset(facing, structureDimensions[1]).offset(mirrored ? facing.rotateYCCW() : facing.rotateY(), structureDimensions[2]).up(structureDimensions[0]));
        }
        return super.getRenderBoundingBox();
    }

    public abstract int[] getRedstonePos();

    public boolean isRedstonePos() {
        if (!hasRedstoneControl) { return false; }
        for (int i : cachedRedstonePos()) {
            if (pos == i) { return true; }
        }
        return false;
    }

    @Override public int getComparatorInputOverride() {
        if (!this.isRedstonePos()) { return 0; }
        T master = master();
        if (master == null) { return 0; }
        return ICUtils.calcRedstoneFromInventory(master);
    }

    @Override public boolean hammerUseSide(EnumFacing side, EntityPlayer player, float hitX, float hitY, float hitZ) {
        if (this.isRedstonePos()) {
            T master = master();
            if (master == null) { return false; }
            master.redstoneControlInverted = !master.redstoneControlInverted;
            ICUtils.sendServerNoSpamMessages(player, new TextComponentTranslation(ICLib.CHAT_INFO + "rsControl." + (master.redstoneControlInverted ? "invertedOn" : "invertedOff")));
            this.updateMasterBlock(null, true);
            return true;
        }
        return false;
    }

    public boolean isRSDisabled() {
        if (computerOn != null) { return !computerOn; }
        T cached = redstoneTileCache;
        if (cached != null && !cached.isInvalid()) {
            return redstoneControlInverted != (world.getRedstonePowerFromNeighbors(cached.getPos()) > 0);
        }
        int[] rsPositions = cachedRedstonePos();
        if (rsPositions.length < 1) { return false; }
        for (int rsPos : rsPositions) {
            T tile = this.getTileForPos(rsPos);
            if (tile != null) {
                redstoneTileCache = tile;
                return redstoneControlInverted != (world.getRedstonePowerFromNeighbors(tile.getPos()) > 0);
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Nullable public T getTileForPos(int targetPos) {
        BlockPos target = getBlockPosForPos(targetPos);
        TileEntity tile = ICUtils.getExistingTileEntity(world, target);
        if (this.getClass().isInstance(tile)) { return (T)tile; }
        return null;
    }

    @Override @Nonnull public ItemStack getOriginalBlock() {
        if (pos < 0) { return ItemStack.EMPTY; }
        ItemStack s = ItemStack.EMPTY;
        try {
            int blocksPerLevel = structureDimensions[1] * structureDimensions[2];
            int h = (pos / blocksPerLevel);
            int l = (pos % blocksPerLevel / structureDimensions[2]);
            int w = (pos % structureDimensions[2]);
            s = this.multiblockInstance.getStructureManual()[h][l][w];
        }
        catch (Exception e) { ICLogger.error("Failed to read the original block for multiblock position " + pos + ": " + e); }
        return s.copy();
    }

    @Override public boolean getIsMirrored() { return this.mirrored; }

    @Override @Nonnull public PropertyBoolInverted getBoolProperty(@Nonnull Class<? extends IUsesBooleanProperty> inf) { return ICProperties.BOOLEANS[0]; }

    protected boolean lastRenderedActive;

    protected void tickPendingNotifications() {}

    private boolean syncParity() { return ((world.getTotalWorldTime() + getPos().getX() + getPos().getZ()) & 1L) == 0L; }

    private void syncProgress() {
        T master = master();
        if (master == null) { return; }
        master.markDirty();
        boolean active = master instanceof IActiveState && ((IActiveState)master).getIsActive();
        if (active != master.lastRenderedActive) {
            master.lastRenderedActive = active;
            master.markContainingBlockForUpdate(null);
            return;
        }
        if (syncParity()) { master.syncToTrackingClients(); }
    }

    @Override public void update() {
        ICTickingRegistry.checkForNeedlessTicking(this);
        boolean syncedThisTick = false;
        tickedProcesses = 0;
        if (world.isRemote || isDummy()) { return; }
        if (syncParity()) { flushClientSync(); }
        tickPendingNotifications();
        if (isRSDisabled()) { return; }
        int max = getMaxProcessPerTick();
        int i = 0;
        Iterator<MultiblockProcess<R>> processIterator = processQueue.iterator();
        tickedProcesses = 0;
        while (processIterator.hasNext() && i++ < max) {
            MultiblockProcess<R> process = processIterator.next();
            if (process.canProcess(this)) {
                process.doProcessTick(this);
                tickedProcesses++;
                syncedThisTick = true;
            }
            if (process.clearProcess) { processIterator.remove(); }
        }
        if (syncedThisTick) { syncProgress(); }
    }

    public abstract IFluidTank[] getInternalTanks();

    public abstract R findRecipeForInsertion(ItemStack inserting);

    public abstract int[] getOutputSlots();

    public abstract int[] getOutputTanks();

    public abstract boolean additionalCanProcessCheck(MultiblockProcess<R> process);

    public abstract void doProcessOutput(ItemStack output);

    public abstract void doProcessFluidOutput(FluidStack output);

    public abstract void onProcessFinish(MultiblockProcess<R> process);

    public abstract int getMaxProcessPerTick();

    public abstract int getProcessQueueMaxLength();

    public abstract float getMinProcessDistance(MultiblockProcess<R> process);

    public abstract boolean isInWorldProcessingMachine();

    public boolean addProcessToQueue(MultiblockProcess<R> process, boolean simulate) { return addProcessToQueue(process, simulate, false); }

    public boolean addProcessToQueue(MultiblockProcess<R> process, boolean simulate, boolean addToPrevious) {
        if (addToPrevious && process instanceof MultiblockProcessInWorld) {
            for (MultiblockProcess<R> curr : processQueue) {
                if (curr instanceof MultiblockProcessInWorld && process.recipe.equals(curr.recipe)) {
                    MultiblockProcessInWorld<R> p = (MultiblockProcessInWorld<R>)curr;
                    boolean canStack = true;
                    for (ItemStack old : p.inputItems) {
                        for (ItemStack in : ((MultiblockProcessInWorld<R>)process).inputItems) {
                            if (OreDictionary.itemMatches(old, in, true) && ICUtils.compareItemNBT(old, in)) {
                                if (old.getCount() + in.getCount() > old.getMaxStackSize()) {
                                    canStack = false;
                                    break;
                                }
                            }
                        }
                        if (!canStack) { break; }
                    }
                    if (canStack) {
                        if (!simulate) {
                            for (ItemStack old : p.inputItems) {
                                for (ItemStack in : ((MultiblockProcessInWorld<R>)process).inputItems) {
                                    if (OreDictionary.itemMatches(old, in, true) && ICUtils.compareItemNBT(old, in)) {
                                        old.grow(in.getCount());
                                        break;
                                    }
                                }
                            }
                        }
                        return true;
                    }
                }
            }
        }
        if (getProcessQueueMaxLength() < 0 || processQueue.size() < getProcessQueueMaxLength()) {
            float dist = 1;
            MultiblockProcess<R> p = null;
            if (!processQueue.isEmpty()) {
                p = processQueue.get(processQueue.size() - 1);
                if (p != null) { dist = p.processTick / (float)p.maxTicks; }
            }
            if (p != null && dist < getMinProcessDistance(p)) { return false; }
            if (!simulate) {
                processQueue.add(process);
                assignDisplaySlot(process);
            }
            return true;
        }
        return false;
    }

    private int displaySlotCount() {
        int slots = getProcessQueueMaxLength();
        if (slots < 1) { slots = processQueue.size(); }
        for (MultiblockProcess<R> process : processQueue) {
            if (process.displaySlot >= slots) { slots = process.displaySlot + 1; }
        }
        return slots;
    }

    private void assignDisplaySlot(MultiblockProcess<R> process) {
        if (process.displaySlot >= 0) { return; }
        int slots = Math.max(1, getProcessQueueMaxLength());
        for (int slot = 0; slot < slots; slot++) {
            boolean taken = false;
            for (MultiblockProcess<R> other : processQueue) {
                if (other != process && other.displaySlot == slot) { taken = true; break; }
            }
            if (!taken) { process.displaySlot = slot; return; }
        }
        process.displaySlot = processQueue.size();
    }

    @Override public int[] getCurrentProcessesStep() {
        T master = master();
        if (master != this && master != null) { return master.getCurrentProcessesStep(); }
        if (processQueue.isEmpty()) { return EMPTY_POS; }
        int[] ia = new int[displaySlotCount()];
        for (MultiblockProcess<R> process : processQueue) {
            if (process.displaySlot >= 0 && process.displaySlot < ia.length) { ia[process.displaySlot] = process.processTick; }
        }
        return ia;
    }

    @Override public int[] getCurrentProcessesMax() {
        T master = master();
        if (master != this && master != null) { return master.getCurrentProcessesMax(); }
        if (processQueue.isEmpty()) { return EMPTY_POS; }
        int[] ia = new int[displaySlotCount()];
        for (MultiblockProcess<R> process : processQueue) {
            if (process.displaySlot >= 0 && process.displaySlot < ia.length) { ia[process.displaySlot] = process.maxTicks; }
        }
        return ia;
    }

    public boolean shouldRenderAsActive() { return getEnergyStored(null) > 0 && !isRSDisabled() && !processQueue.isEmpty(); }

    public abstract static class MultiblockProcess<R extends ICRecipe> {
        public R recipe;
        public int processTick;
        public int maxTicks;
        public int energyPerTick;
        public boolean clearProcess = false;
        public int displaySlot = -1;

        public MultiblockProcess(R recipe) {
            this.recipe = recipe;
            this.processTick = 0;
            this.maxTicks = this.recipe.getTotalProcessTime();
            this.energyPerTick = this.recipe.getTotalProcessEnergy() / this.maxTicks;
        }

        protected List<ItemStack> getRecipeItemOutputs(ICTileEntityMultiblockMetal<?, ?> multiblock) { return recipe.getActualItemOutputs(multiblock); }

        protected List<FluidStack> getRecipeFluidOutputs(ICTileEntityMultiblockMetal<?, ?> multiblock) { return recipe.getActualFluidOutputs(multiblock); }

        public boolean canProcess(ICTileEntityMultiblockMetal<?, ?> multiblock) {
            if (multiblock.energyStorage.extractEnergy(energyPerTick, true) == energyPerTick) {
                List<ItemStack> outputs = recipe.getItemOutputs();
                if (outputs != null && !outputs.isEmpty()) {
                    int[] outputSlots = multiblock.getOutputSlots();
                    for (ItemStack output : outputs) {
                        if (!output.isEmpty()) {
                            boolean canOutput = false;
                            if (outputSlots == null) { canOutput = true; }
                            else {
                                for (int iOutputSlot : outputSlots) {
                                    ItemStack s = multiblock.getInventory().get(iOutputSlot);
                                    if (s.isEmpty() || (ItemHandlerHelper.canItemStacksStack(s, output) && s.getCount() + output.getCount() <= multiblock.getSlotLimit(iOutputSlot))) {
                                        canOutput = true;
                                        break;
                                    }
                                }
                            }
                            if (!canOutput) { return false; }
                        }
                    }
                }
                List<FluidStack> fluidOutputs = recipe.getFluidOutputs();
                if (fluidOutputs != null && !fluidOutputs.isEmpty()) {
                    IFluidTank[] tanks = multiblock.getInternalTanks();
                    int[] outputTanks = multiblock.getOutputTanks();
                    for (FluidStack output : fluidOutputs) {
                        if (output != null && output.amount > 0) {
                            boolean canOutput = false;
                            if (tanks == null || outputTanks == null) { canOutput = true; }
                            else {
                                for (int iOutputTank : outputTanks) {
                                    if (iOutputTank >= 0 && iOutputTank < tanks.length && tanks[iOutputTank] != null && tanks[iOutputTank].fill(output, false) == output.amount) {
                                        canOutput = true;
                                        break;
                                    }
                                }
                            }
                            if (!canOutput) { return false; }
                        }
                    }
                }
                return multiblock.additionalCanProcessCheckGeneric(this);
            }
            return false;
        }

        public void doProcessTick(ICTileEntityMultiblockMetal<?, ?> multiblock) {
            int energyExtracted = energyPerTick;
            int ticksAdded = 1;
            if (this.recipe.getMultipleProcessTicks() > 1) {
                int averageInsertion = multiblock.energyStorage.getAverageInsertion();
                averageInsertion = multiblock.energyStorage.extractEnergy(averageInsertion, true);
                if (averageInsertion > energyExtracted) {
                    int possibleTicks;
                    if (energyPerTick > 0) { possibleTicks = Math.min(averageInsertion / energyPerTick, Math.min(this.recipe.getMultipleProcessTicks(), this.maxTicks - this.processTick)); }
                    else { possibleTicks = Math.min(this.recipe.getMultipleProcessTicks(), this.maxTicks - this.processTick); }
                    if (possibleTicks > 1) {
                        ticksAdded = possibleTicks;
                        energyExtracted *= ticksAdded;
                    }
                }
            }
            multiblock.energyStorage.extractEnergy(energyExtracted, false);
            this.processTick += ticksAdded;
            if (this.processTick >= this.maxTicks) { this.processFinish(multiblock); }
        }

        protected void processFinish(ICTileEntityMultiblockMetal<?, ?> multiblock) {
            List<ItemStack> outputs = getRecipeItemOutputs(multiblock);
            if (outputs != null && !outputs.isEmpty()) {
                int[] outputSlots = multiblock.getOutputSlots();
                for (ItemStack output : outputs) {
                    if (!output.isEmpty()) {
                        if (outputSlots == null || multiblock.getInventory() == null) { multiblock.doProcessOutput(output.copy()); }
                        else {
                            for (int iOutputSlot : outputSlots) {
                                ItemStack s = multiblock.getInventory().get(iOutputSlot);
                                if (s.isEmpty()) {
                                    multiblock.getInventory().set(iOutputSlot, output.copy());
                                    break;
                                }
                                else if (ItemHandlerHelper.canItemStacksStack(s, output) && s.getCount() + output.getCount() <= multiblock.getSlotLimit(iOutputSlot)) {
                                    multiblock.getInventory().get(iOutputSlot).grow(output.getCount());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            List<FluidStack> fluidOutputs = getRecipeFluidOutputs(multiblock);
            if (fluidOutputs != null && !fluidOutputs.isEmpty()) {
                IFluidTank[] tanks = multiblock.getInternalTanks();
                int[] outputTanks = multiblock.getOutputTanks();
                for (FluidStack output : fluidOutputs) {
                    if (output != null && output.amount > 0) {
                        if (tanks == null || outputTanks == null) { multiblock.doProcessFluidOutput(output); }
                        else {
                            for (int iOutputTank : outputTanks) {
                                if (iOutputTank >= 0 && iOutputTank < tanks.length && tanks[iOutputTank] != null && tanks[iOutputTank].fill(output, false) == output.amount) {
                                    tanks[iOutputTank].fill(output, true);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            multiblock.onProcessFinishGeneric(this);
            this.clearProcess = true;
        }

        protected abstract void writeExtraDataToNBT(NBTTagCompound nbt);
    }

    @SuppressWarnings("unchecked")
    void onProcessFinishGeneric(MultiblockProcess<?> process) { onProcessFinish((MultiblockProcess<R>)process); }

    @SuppressWarnings("unchecked")
    boolean additionalCanProcessCheckGeneric(MultiblockProcess<?> process) { return additionalCanProcessCheck((MultiblockProcess<R>)process); }

    public static class MultiblockProcessInMachine<R extends ICRecipe> extends MultiblockProcess<R> {
        protected int[] inputSlots;
        protected int[] inputAmounts = null;
        protected int[] inputTanks = new int[0];

        public MultiblockProcessInMachine(R recipe, int... inputSlots) {
            super(recipe);
            this.inputSlots = inputSlots;
        }

        public MultiblockProcessInMachine<R> setInputTanks(int... inputTanks) {
            this.inputTanks = inputTanks;
            return this;
        }

        public MultiblockProcessInMachine<R> setInputAmounts(int... inputAmounts) {
            this.inputAmounts = inputAmounts;
            return this;
        }

        public int[] getInputSlots() { return this.inputSlots; }

        @Nullable public int[] getInputAmounts() { return this.inputAmounts; }

        public int[] getInputTanks() { return this.inputTanks; }

        protected List<ICIngredient> getRecipeItemInputs(ICTileEntityMultiblockMetal<?, ?> multiblock) { return recipe.getItemInputs(); }

        protected List<FluidStack> getRecipeFluidInputs(ICTileEntityMultiblockMetal<?, ?> multiblock) { return recipe.getFluidInputs(); }

        @Override public void doProcessTick(ICTileEntityMultiblockMetal<?, ?> multiblock) {
            NonNullList<ItemStack> inv = multiblock.getInventory();
            if (recipe.shouldCheckItemAvailability() && recipe.getItemInputs() != null && inv != null) {
                NonNullList<ItemStack> query = NonNullList.withSize(inputSlots.length, ItemStack.EMPTY);
                for (int i = 0; i < inputSlots.length; i++) {
                    if (inputSlots[i] >= 0 && inputSlots[i] < inv.size()) { query.set(i, multiblock.getInventory().get(inputSlots[i])); }
                }
                if (!ICUtils.stacksMatchIngredientList(recipe.getItemInputs(), query)) {
                    this.clearProcess = true;
                    return;
                }
            }
            super.doProcessTick(multiblock);
        }

        @Override protected void processFinish(ICTileEntityMultiblockMetal<?, ?> multiblock) {
            super.processFinish(multiblock);
            NonNullList<ItemStack> inv = multiblock.getInventory();
            List<ICIngredient> itemInputList = this.getRecipeItemInputs(multiblock);
            if (inv != null && this.inputSlots != null && itemInputList != null) {
                if (this.inputAmounts != null && this.inputSlots.length == this.inputAmounts.length) {
                    for (int i = 0; i < this.inputSlots.length; i++) {
                        if (this.inputAmounts[i] > 0) { inv.get(this.inputSlots[i]).shrink(this.inputAmounts[i]); }
                    }
                }
                else {
                    for (ICIngredient ingr : new ArrayList<>(itemInputList)) {
                        int ingrSize = ingr.inputSize;
                        for (int slot : this.inputSlots) {
                            if (!inv.get(slot).isEmpty() && ingr.matchesItemStackIgnoringSize(inv.get(slot))) {
                                int taken = Math.min(inv.get(slot).getCount(), ingrSize);
                                inv.get(slot).shrink(taken);
                                if (inv.get(slot).getCount() <= 0) { inv.set(slot, ItemStack.EMPTY); }
                                if ((ingrSize -= taken) <= 0) { break; }
                            }
                        }
                    }
                }
            }
            IFluidTank[] tanks = multiblock.getInternalTanks();
            List<FluidStack> fluidInputList = this.getRecipeFluidInputs(multiblock);
            if (tanks != null && this.inputTanks != null && fluidInputList != null) {
                for (FluidStack ingr : new ArrayList<>(fluidInputList)) {
                    int ingrSize = ingr.amount;
                    for (int tank : this.inputTanks) {
                        if (tanks[tank] != null) {
                            if (tanks[tank] instanceof IFluidHandler && ((IFluidHandler)tanks[tank]).drain(ingr, false) != null) {
                                FluidStack taken = ((IFluidHandler)tanks[tank]).drain(ingr, true);
                                if (taken != null && (ingrSize -= taken.amount) <= 0) { break; }
                            }
                            else {
                                FluidStack held = tanks[tank].getFluid();
                                if (held != null && held.isFluidEqual(ingr)) {
                                    int taken = Math.min(tanks[tank].getFluidAmount(), ingrSize);
                                    tanks[tank].drain(taken, true);
                                    if ((ingrSize -= taken) <= 0) { break; }
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override protected void writeExtraDataToNBT(NBTTagCompound nbt) {
            if (inputSlots != null) { nbt.setIntArray("process_inputSlots", inputSlots); }
            if (inputAmounts != null) { nbt.setIntArray("process_inputAmounts", inputAmounts); }
            if (inputTanks != null) { nbt.setIntArray("process_inputTanks", inputTanks); }
        }
    }

    public static class MultiblockProcessInWorld<R extends ICRecipe> extends MultiblockProcess<R> {
        public List<ItemStack> inputItems;
        protected float transformationPoint;

        public MultiblockProcessInWorld(R recipe, float transformationPoint, NonNullList<ItemStack> inputItem) {
            super(recipe);
            this.inputItems = new ArrayList<>(inputItem);
            this.transformationPoint = transformationPoint;
        }

        public List<ItemStack> getDisplayItem() {
            if (processTick / (float)maxTicks > transformationPoint) {
                List<ItemStack> list = this.recipe.getItemOutputs();
                if (!list.isEmpty()) { return list; }
            }
            return inputItems;
        }

        @Override protected void writeExtraDataToNBT(NBTTagCompound nbt) {
            nbt.setTag("process_inputItem", ICUtils.writeInventory(inputItems));
            nbt.setFloat("process_transformationPoint", transformationPoint);
        }

        @Override protected void processFinish(ICTileEntityMultiblockMetal<?, ?> multiblock) {
            super.processFinish(multiblock);
            int size = -1;
            for (ItemStack inputItem : this.inputItems) {
                for (ICIngredient s : recipe.getItemInputs()) {
                    if (s.matchesItemStackIgnoringSize(inputItem)) {
                        size = s.inputSize;
                        break;
                    }
                }
                if (size > 0 && inputItem.getCount() > size) {
                    inputItem.splitStack(size);
                    processTick = 0;
                    clearProcess = false;
                }
            }
        }
    }

    public static class MultiblockInventoryHandler_DirectProcessing implements IItemHandlerModifiable {
        ICTileEntityMultiblockMetal<?, ?> multiblock;
        float transformationPoint = .5f;
        boolean doProcessStacking = false;

        public MultiblockInventoryHandler_DirectProcessing(ICTileEntityMultiblockMetal<?, ?> multiblock) { this.multiblock = multiblock; }

        public MultiblockInventoryHandler_DirectProcessing setTransformationPoint(float point) {
            this.transformationPoint = point;
            return this;
        }

        public MultiblockInventoryHandler_DirectProcessing setProcessStacking(boolean stacking) {
            this.doProcessStacking = stacking;
            return this;
        }

        @Override public int getSlots() { return 1; }

        @Override @Nonnull public ItemStack getStackInSlot(int slot) { return ItemStack.EMPTY; }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override @Nonnull public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            stack = stack.copy();
            ICRecipe recipe = this.multiblock.findRecipeForInsertion(stack);
            if (recipe == null) { return stack; }
            ItemStack displayStack = recipe.getDisplayStack(stack);
            if (multiblock.addProcessToQueue(new MultiblockProcessInWorld(recipe, transformationPoint, ICUtils.createNonNullItemStackListFromItemStack(displayStack)), simulate, doProcessStacking)) {
                multiblock.markDirty();
                multiblock.markContainingBlockForUpdate(null);
                stack.shrink(displayStack.getCount());
                if (stack.getCount() <= 0) { stack = ItemStack.EMPTY; }
            }
            return stack;
        }

        @Override @Nonnull public ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }

        @Override public int getSlotLimit(int slot) { return 64; }

        @Override public void setStackInSlot(int slot, @Nonnull ItemStack stack) {}
    }
}
