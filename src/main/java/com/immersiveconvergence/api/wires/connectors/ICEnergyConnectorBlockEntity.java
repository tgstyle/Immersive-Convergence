package com.immersiveconvergence.api.wires.connectors;

import com.immersiveconvergence.api.block.IServerTickableBE;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ICEnergyConnectorBlockEntity extends ICConnectorBlockEntity implements IServerTickableBE {
    protected ICEnergyConnectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ICConnectorTier tier) { super(type, pos, state, tier, false); }

    @Override public void tickServer() { offerBuffer(); }
}
