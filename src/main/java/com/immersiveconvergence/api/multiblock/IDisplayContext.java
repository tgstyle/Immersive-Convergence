package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.integration.DisplayLines;
import com.immersiveconvergence.api.multiblock.process.ProcessContext;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import java.util.List;

public interface IDisplayContext {
    default boolean isActive() { return false; }

    default IEnergyStorage getEnergyStorage() { return null; }

    default List<IEnergyStorage> getEnergies() { IEnergyStorage e = getEnergyStorage(); return e != null ? List.of(e) : List.of(); }

    default IItemHandlerModifiable getInventory() { return ProcessContext.EMPTY_ITEM_HANDLER; }

    default IFluidTank[] getInternalTanks() { return ProcessContext.EMPTY_TANKS; }

    default void writeDisplaySyncNBT(CompoundTag nbt) { }

    default void readDisplaySyncNBT(CompoundTag nbt) { }

    default void addDisplayLines(Level level, DisplayLines lines) { }
}
