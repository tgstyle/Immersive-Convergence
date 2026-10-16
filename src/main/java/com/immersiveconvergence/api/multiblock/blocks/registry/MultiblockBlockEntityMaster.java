package com.immersiveconvergence.api.multiblock.blocks.registry;

import com.immersiveconvergence.api.multiblock.blocks.MultiblockRegistration;
import com.immersiveconvergence.api.multiblock.blocks.env.IMultiblockBEHelperMaster;
import com.immersiveconvergence.api.multiblock.blocks.logic.IMultiblockState;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MultiblockBlockEntityMaster<State extends IMultiblockState> extends MultiblockBlockEntityBase<State, IMultiblockBEHelperMaster<State>> {
    public MultiblockBlockEntityMaster(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState, MultiblockRegistration<State> multiblock) { super(type, worldPosition, blockState, be -> IMultiblockBEHelperMaster.MAKE_HELPER.get().makeFor(be, multiblock)); }

    @Override public void setRemoved() {
        super.setRemoved();
        getHelper().invalidateAllCaps();
    }

    @Override public void onLoad() {
        super.onLoad();
        getHelper().invalidateAllCaps();
    }
}
