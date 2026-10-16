package com.immersiveconvergence.api.multiblock.process;

import com.immersiveconvergence.api.crafting.ICMultiblockRecipe;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockLevel;
import com.immersiveconvergence.api.multiblock.process.ProcessContext.ProcessContextInMachine;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

public class MultiblockProcessor<R extends ICMultiblockRecipe, CTX extends ProcessContext<R>> {
    private final List<MultiblockProcess<R, CTX>> processQueue = new ArrayList<>();
    private final int maxQueueLength;
    private final float minDelayAfter;
    private final int maxProcessPerTick;
    private final Runnable markDirty;
    private final BiFunction<Level, ResourceLocation, @Nullable R> getRecipeFromID;

    public MultiblockProcessor(int maxQueueLength, float minDelayAfter, int maxProcessPerTick, Runnable markDirty, BiFunction<Level, ResourceLocation, @Nullable R> getRecipeFromID) {
        this.maxQueueLength = maxQueueLength;
        this.minDelayAfter = minDelayAfter;
        this.maxProcessPerTick = maxProcessPerTick;
        this.markDirty = markDirty;
        this.getRecipeFromID = getRecipeFromID;
    }

    public boolean tickServer(CTX ctx, IMultiblockLevel level, boolean canWork) {
        ctx.getEnergy().updateAverage();
        if (!canWork) { return false; }

        int i = 0;
        Iterator<MultiblockProcess<R, CTX>> processIterator = processQueue.iterator();
        boolean tickedAny = false;
        while (processIterator.hasNext() && i++ < maxProcessPerTick) {
            MultiblockProcess<R, CTX> process = processIterator.next();
            if (process.canProcess(ctx, level.getRawLevel())) {
                process.doProcessTick(ctx, level);
                tickedAny = true;
            }
            if (process.clearProcess) { processIterator.remove(); }
        }
        if (tickedAny) { markDirty.run(); }
        return tickedAny;
    }

    public Tag toNBT() {
        ListTag processList = new ListTag();
        for (final MultiblockProcess<R, CTX> process : processQueue) { processList.add(process.toNBT()); }
        return processList;
    }

    public void fromNBT(Tag nbt, ProcessLoader<R, CTX> loader) {
        if (!(nbt instanceof ListTag list)) { return; }
        this.processQueue.clear();
        for (final Tag tag : list) {
            if (tag instanceof CompoundTag processTag) {
                final MultiblockProcess<R, CTX> loadedProcess = loader.fromNBT(getRecipeFromID, processTag);
                if (loadedProcess != null) { this.processQueue.add(loadedProcess); }
            }
        }
    }

    public boolean addProcessToQueue(MultiblockProcess<R, CTX> process, Level level, boolean simulate) {
        if (maxQueueLength < 0 || processQueue.size() < maxQueueLength) {
            if (!processQueue.isEmpty()) {
                MultiblockProcess<R, CTX> previousProcess = processQueue.getLast();
                float dist = previousProcess.processTick / (float)previousProcess.getMaxTicks(level);
                if (dist < minDelayAfter) { return false; }
            }

            if (!simulate) { processQueue.add(process); }
            markDirty.run();
            return true;
        }
        return false;
    }

    public int getMaxQueueSize() { return maxQueueLength; }

    public int getQueueSize() { return processQueue.size(); }

    public List<MultiblockProcess<R, CTX>> getQueue() { return Collections.unmodifiableList(processQueue); }

    public interface ProcessLoader<R extends ICMultiblockRecipe, CTX extends ProcessContext<R>> {
        MultiblockProcess<R, CTX> fromNBT(BiFunction<Level, ResourceLocation, R> getRecipe, CompoundTag data);
    }

    public static class InMachineProcessor<R extends ICMultiblockRecipe> extends MultiblockProcessor<R, ProcessContextInMachine<R>> {
        public InMachineProcessor(int maxQueueLength, float minDelayAfter, int maxProcessPerTick, Runnable markDirty, BiFunction<Level, ResourceLocation, @Nullable R> getRecipeFromID) { super(maxQueueLength, minDelayAfter, maxProcessPerTick, markDirty, getRecipeFromID); }
    }
}
