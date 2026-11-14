package com.immersiveconvergence.api.wires.fittings;

import com.immersiveconvergence.api.block.IServerTickableBE;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ICEnergyFittingBlockEntity extends ICFittingBlockEntity implements IServerTickableBE {
    protected ICEnergyFittingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ICFittingTier tier) { super(type, pos, state, tier, false); }

    @Override public void tickServer() { offerBuffer(); }
}
