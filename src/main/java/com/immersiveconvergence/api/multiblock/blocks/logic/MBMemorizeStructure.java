package com.immersiveconvergence.api.multiblock.blocks.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;

@SuppressWarnings({"unused", "RedundantSuppression"}) public interface MBMemorizeStructure<State extends IMultiblockState> extends IMultiblockLogic<State> {
    void setMemorizedBlockState(State state, BlockPos pos, BlockState blockState);

    BlockState getMemorizedBlockState(State state, BlockPos pos);

    class StructureMemo extends HashMap<BlockPos, BlockState> implements IMultiblockState {
        @Override public void writeSaveNBT(CompoundTag nbt, Provider provider) {
            ListTag list = new ListTag();
            this.forEach((pos, state) -> {
                CompoundTag compound = new CompoundTag();
                compound.put("pos", NbtUtils.writeBlockPos(pos));
                compound.put("state", NbtUtils.writeBlockState(state));
                list.add(compound);
            });
            nbt.put("memorizedStates", list);
        }

        @Override public void readSaveNBT(CompoundTag nbt, Provider provider) {
            HolderGetter<Block> lookup = provider.lookupOrThrow(Registries.BLOCK);
            ListTag list = nbt.getList("memorizedStates", Tag.TAG_COMPOUND);
            list.forEach(tag -> {
                if (tag instanceof CompoundTag compound) { this.put(NbtUtils.readBlockPos(compound, "pos").orElseThrow(), NbtUtils.readBlockState(lookup, compound.getCompound("state"))); }
            });
        }
    }
}
