package com.immersiveconvergence.api.multiblock.blocks.registry;

import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelperDummy;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MultiblockBlockEntityDummy<State extends IMultiblockState> extends MultiblockBlockEntityBase<State, IMultiblockBEHelperDummy<State>> {
    public MultiblockBlockEntityDummy(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState, MultiblockRegistration<State> multiblock) { super(type, worldPosition, blockState, be -> IMultiblockBEHelperDummy.MAKE_HELPER.getValue().makeFor(be, multiblock)); }
}
