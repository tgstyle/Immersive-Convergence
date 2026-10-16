package com.immersiveconvergence.api.energy;

import com.immersiveconvergence.common.ie.IEWireBridge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nonnull;

public abstract class ICConnectorRedstoneBlockEntity extends ICConnectableBlockEntity {
    public ICConnectorRedstoneBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    @Override public boolean canConnect() { return true; }

    public abstract void redstoneInput(byte[] signals, @Nonnull ICConnectionPoint cp);

    public abstract void redstoneChanged();

    protected int redstoneNetworkValue(int channel) { return ieWired() ? IEWireBridge.redstoneValue(level, worldPosition, channel) : 0; }

    protected void updateRedstoneNetwork() { if (ieWired()) { IEWireBridge.updateRedstoneValues(level, worldPosition); } }
}
