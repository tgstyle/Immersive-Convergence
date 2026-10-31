package com.immersiveconvergence.api.wires.connectors;

import com.immersiveconvergence.api.wires.ICConnectionPoint;
import com.immersiveconvergence.api.wires.ICWires;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nonnull;

@SuppressWarnings("unused") public abstract class ICConnectorRedstoneBlockEntity extends ICConnectableBlockEntity {
    public ICConnectorRedstoneBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    @Override public boolean canConnect() { return true; }

    public abstract void redstoneInput(byte[] signals, @Nonnull ICConnectionPoint cp);

    public abstract void redstoneChanged();

    protected int redstoneNetworkValue(int channel) { return ieWired() ? ICWires.redstoneValue(level, worldPosition, channel) : 0; }

    protected void updateRedstoneNetwork() { if (ieWired()) { ICWires.updateRedstoneValues(level, worldPosition); } }
}
