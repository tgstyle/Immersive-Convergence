package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.client.split.ISubmodelOffsetProvider;
import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;
import com.immersiveconvergence.api.multiblock.blocks.registry.MultiblockBlockEntityMaster;
import com.immersiveconvergence.api.util.IItemDropProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import java.util.stream.Stream;

public class MachineBlockEntityMaster<State extends IMultiblockState> extends MultiblockBlockEntityMaster<State> implements ISubmodelOffsetProvider, IItemDropProvider {
    private final MachineBlockEntityCommon<State> common;
    private boolean disassembling = false;

    public MachineBlockEntityMaster(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState, MultiblockRegistration<State> multiblock) { super(type, worldPosition, blockState, multiblock); this.common = new MachineBlockEntityCommon<>(multiblock, this::getHelper, this::getLevel); }

    public void markDisassembling() { this.disassembling = true; }

    @Override public Stream<ItemStack> getDroppedItems() {
        if (disassembling) { return Stream.empty(); }
        return common.getDroppedItems();
    }

    @Override public BlockPos getModelOffset(BlockState state, Vec3i size) { return common.getModelOffset(state, size); }
}
