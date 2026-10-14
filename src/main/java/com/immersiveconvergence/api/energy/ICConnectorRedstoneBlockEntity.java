package com.immersiveconvergence.api.energy;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.common.ie.IEWireBridge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

@SuppressWarnings({"unused", "RedundantSuppression"}) public abstract class ICConnectorRedstoneBlockEntity extends ICConnectableBlockEntity {
    public ICConnectorRedstoneBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    @Override public boolean canConnect() { return true; }

    public abstract void redstoneInput(byte[] signals, @Nonnull ICConnectionPoint cp);

    public abstract void redstoneChanged();

    protected int redstoneNetworkValue(int channel) { return level != null && ICMods.immersiveEngineering() ? IEWireBridge.redstoneValue(level, worldPosition, channel) : 0; }

    protected void updateRedstoneNetwork() { if (level != null && ICMods.immersiveEngineering()) { IEWireBridge.updateRedstoneValues(level, worldPosition); } }
}
