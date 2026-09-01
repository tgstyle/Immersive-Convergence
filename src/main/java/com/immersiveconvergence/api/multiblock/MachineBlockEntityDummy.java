package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.client.split.ISubmodelOffsetProvider;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityDummy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;
import com.immersiveconvergence.api.block.BlockInterfaces;
import com.immersiveconvergence.api.util.IItemDropProvider;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class MachineBlockEntityDummy<State extends IMultiblockState> extends MultiblockBlockEntityDummy<State> implements ISubmodelOffsetProvider, BlockInterfaces.IPlayerInteraction, IItemDropProvider {
    private final MachineBlockEntityCommon<State> common;
    private boolean disassembling = false;

    public MachineBlockEntityDummy(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState, MultiblockRegistration<State> multiblock) { super(type, worldPosition, blockState, multiblock); this.common = new MachineBlockEntityCommon<>(multiblock, this::getHelper, this::getLevel); }

    public boolean isDisassembling() { return disassembling; }

    public void markDisassembling() { this.disassembling = true; }

    @Override public boolean interact(Direction side, Player player, InteractionHand hand, ItemStack heldItem, float hitX, float hitY, float hitZ) { return common.interact(side, player, hand, heldItem, hitX, hitY, hitZ); }

    @Override public Stream<ItemStack> getDroppedItems() {
        if (disassembling) { return Stream.empty(); }
        return common.getDroppedItems();
    }

    @Override public BlockPos getModelOffset(BlockState state, Vec3i size) { return common.getModelOffset(state, size); }

    @Override @NotNull public ModelData getModelData() { return common.getModelData(); }
}
